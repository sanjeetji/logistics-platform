package com.logistics.orchestration.internal.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.orchestration.internal.domain.OrderContext;
import com.logistics.orchestration.internal.domain.SagaInstance;
import com.logistics.orchestration.internal.domain.SagaStatus;
import com.logistics.orchestration.internal.repository.SagaRepository;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class Orchestrator {

    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    // In a real implementation, we would inject a list of steps or a step factory
    // private final List<SagaStep> steps;

    @Transactional
    public void startSaga(OrderCreatedEvent event) {
        log.info("Starting saga for order: {}", event.getOrderId());

        // check for duplicate
        if (sagaRepository.findByCorrelationId(event.getOrderId()).isPresent()) {
            log.warn("Saga already exists for order: {}", event.getOrderId());
            return;
        }

        OrderContext context = new OrderContext(
                event.getOrderId(),
                event.getOrderDto().getCustomerId(),
                java.math.BigDecimal
                        .valueOf(event.getOrderDto().getPrice() != null ? event.getOrderDto().getPrice() : 0.0),
                event.getOrderDto().getPickupAddress(),
                event.getOrderDto().getDropAddress(),
                "STANDARD", // Default, as vehicleType isn't in TransportOrderDto yet
                null);
        SagaInstance saga = SagaInstance.builder()
                .correlationId(event.getOrderId())
                .sagaType("ORDER_FULFILLMENT")
                .status(SagaStatus.STARTED)
                .currentStep("INIT")
                .payload(context)
                .build();

        sagaRepository.save(saga);

        // Trigger first step: Dispatch Request
        processDispatch(saga);
    }

    @Transactional
    public void processDispatch(SagaInstance saga) {
        // Logic to transition state and trigger dispatch
        // In full implementation, this calls DispatchStep.execute()
        updateSagaStatus(saga, SagaStatus.DISPATCH_REQUESTED, "DISPATCH_REQUESTED");

        // TODO: Publish DispatchRequestedEvent via Kafka Producer
        log.info("Dispatch requested for saga: {}", saga.getId());
    }

    private void updateSagaStatus(SagaInstance saga, SagaStatus status, String step) {
        saga.setStatus(status);
        saga.setCurrentStep(step);
        sagaRepository.save(saga);
    }
}
