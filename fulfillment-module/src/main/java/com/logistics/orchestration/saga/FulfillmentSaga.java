package com.logistics.orchestration.saga;

import com.logistics.orchestration.model.SagaState;
import com.logistics.orchestration.repository.SagaStateRepository;
import com.logistics.platform.event.dto.OrchestrationCommand;
import com.logistics.platform.event.dto.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FulfillmentSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SagaStateRepository sagaStateRepository;

    @KafkaListener(topics = "order.confirmed", groupId = "orchestration-fulfillment-group")
    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        String orderId = event.getOrderId();
        log.info("FULFILLMENT STARTED: Order Confirmed Event received for OrderId: {}", orderId);

        SagaState state = sagaStateRepository.findById(orderId).orElse(null);
        if (state == null) {
            log.error("Saga State not found for OrderId: {}", orderId);
            return;
        }

        try {
            // Step 1: Dispatch Command
            log.info("Step 1: Requesting Dispatch for OrderId: {}", orderId);
            
            // Fetch Order Details (Simulated)
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);
            // In real world, fetch details from Order Service or enrich event
            
            OrchestrationCommand dispatchCommand = OrchestrationCommand.builder()
                    .commandId(UUID.randomUUID().toString())
                    .traceId(UUID.randomUUID().toString()) 
                    .type(OrchestrationCommand.CommandType.DISPATCH_ORDER)
                    .targetService("dispatch-service")
                    .payload(payload)
                    .timestamp(LocalDateTime.now())
                    .build();

            log.info("Sending Dispatch Command: {}", dispatchCommand.getCommandId());
            kafkaTemplate.send("orchestration.command.dispatch", dispatchCommand);
            
            state.setStatus(SagaState.SagaStatus.DISPATCH_REQUESTED);
            state.setCurrentStep("WAITING_FOR_DRIVER");
            state.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(state);

        } catch (Exception e) {
            log.error("Fulfillment Saga Failed for OrderId: {}", orderId, e);
            state.setStatus(SagaState.SagaStatus.FAILED);
            state.setFailureReason("Fulfillment Failed: " + e.getMessage());
            state.setUpdatedAt(LocalDateTime.now());
            sagaStateRepository.save(state);
        }
    }
}
