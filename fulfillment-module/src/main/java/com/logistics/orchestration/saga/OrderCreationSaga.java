package com.logistics.orchestration.saga;

import com.logistics.orchestration.model.SagaState;
import com.logistics.orchestration.repository.SagaStateRepository;
import com.logistics.platform.api.inventory.InventoryClient;
import com.logistics.platform.api.payment.PaymentClient;
import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.platform.event.dto.OrderConfirmedEvent;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import com.logistics.platform.event.dto.OrderInventoryFailedEvent;
import com.logistics.platform.event.dto.OrderPaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationSaga {

    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final SagaStateRepository sagaStateRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order.created", groupId = "orchestration-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        log.info("SAGA STARTED: Order Created Event received for OrderId: {}", orderId);

        // Initialize Saga State
        SagaState state = SagaState.builder()
                .orderId(orderId)
                .status(SagaState.SagaStatus.STARTED)
                .currentStep("INVENTORY_RESERVATION")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        sagaStateRepository.save(state);

        try {
            // Step 1: Reserve Inventory
            log.info("Step 1: Reserving Inventory for OrderId: {}", orderId);
            // Mock call - separate task to fully implement InventoryClient interaction
            boolean reserved = true; 
            
            if (!reserved) {
                failSaga(state, "Inventory Reservation Failed", "INVENTORY");
                return;
            }
            
            updateState(state, SagaState.SagaStatus.INVENTORY_RESERVED, "PAYMENT_PROCESSING");

            // Step 2: Process Payment
            log.info("Step 2: Processing Payment for OrderId: {}", orderId);
            // Mock Payment Request
            PaymentDtos.PaymentRequest paymentRequest = PaymentDtos.PaymentRequest.builder()
                    .userId(1L) // Mock User ID: Event needs enrichment
                    .orderId(orderId)
                    .amount(BigDecimal.TEN) // Mock Amount
                    .description("Order Payment")
                    .build();
            
            // boolean paymentSuccess = paymentClient.processPayment(paymentRequest); 
            boolean paymentSuccess = true; // Mocking success

            if (!paymentSuccess) {
                failSaga(state, "Payment Failed", "PAYMENT");
                return;
            }

            updateState(state, SagaState.SagaStatus.PAYMENT_PROCESSED, "ORDER_CONFIRMATION");

            // Step 3: Confirm Order & Trigger Fulfillment
            log.info("Step 3: Confirming OrderId: {}", orderId);
            
            OrderConfirmedEvent confirmedEvent = OrderConfirmedEvent.builder()
                    .orderId(orderId)
                    .userId(1L) // Mock User ID
                    .confirmedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("order.confirmed", confirmedEvent);
            
            updateState(state, SagaState.SagaStatus.ORDER_CONFIRMED, "FULFILLMENT_STARTED");
            log.info("SAGA COMPLETED: OrderId: {}", orderId);

        } catch (Exception e) {
            log.error("SAGA FAILED: OrderId: {}", orderId, e);
            failSaga(state, e.getMessage(), "UNKNOWN");
        }
    }

    private void updateState(SagaState state, SagaState.SagaStatus status, String nextStep) {
        state.setStatus(status);
        state.setCurrentStep(nextStep);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);
    }

    private void failSaga(SagaState state, String reason, String failureSource) {
        log.error("Compensating Saga for OrderId: {}. Reason: {}", state.getOrderId(), reason);
        
        state.setStatus(SagaState.SagaStatus.FAILED);
        state.setFailureReason(reason);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        if ("PAYMENT".equals(failureSource)) {
             kafkaTemplate.send("order.payment.failed", OrderPaymentFailedEvent.builder()
                     .orderId(state.getOrderId())
                     .reason(reason)
                     .failedAt(LocalDateTime.now())
                     .build());
        } else if ("INVENTORY".equals(failureSource)) {
            kafkaTemplate.send("order.inventory.failed", OrderInventoryFailedEvent.builder()
                    .orderId(state.getOrderId())
                    .reason(reason)
                    .failedAt(LocalDateTime.now())
                    .build());
        }
        
        // Trigger generic compensation (e.g., Cancel Order, Release Inventory)
    }
}
