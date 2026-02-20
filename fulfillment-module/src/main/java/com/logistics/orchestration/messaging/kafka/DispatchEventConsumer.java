package com.logistics.orchestration.messaging.kafka;

import com.logistics.orchestration.internal.domain.SagaInstance;
import com.logistics.orchestration.internal.domain.SagaStatus;
import com.logistics.orchestration.internal.repository.SagaRepository;
import com.logistics.platform.event.dto.DispatchAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchEventConsumer {

    private final SagaRepository sagaRepository;

    @KafkaListener(topics = "${spring.kafka.topics.dispatch-events:dispatch.events}", groupId = "${spring.kafka.consumer.group-id:orchestration-group}")
    @Transactional
    public void handleDispatchAssigned(DispatchAssignedEvent event) {
        log.info("Received DispatchAssignedEvent for order: {}", event.getOrderId());

        sagaRepository.findByCorrelationId(event.getOrderId()).ifPresentOrElse(saga -> {
            saga.setStatus(SagaStatus.DISPATCH_ASSIGNED);
            saga.setCurrentStep("DISPATCH_ASSIGNED"); // Update step
            sagaRepository.save(saga);
            log.info("Updated saga status to DISPATCH_ASSIGNED for order: {}", event.getOrderId());
            // Trigger next step...
        }, () -> {
            log.warn("Saga not found for dispatched order: {}", event.getOrderId());
        });
    }
}
