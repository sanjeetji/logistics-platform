package com.logistics.orchestration.messaging.kafka;

import com.logistics.orchestration.internal.saga.Orchestrator;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final Orchestrator orchestrator;

    @KafkaListener(topics = "${spring.kafka.topics.order-events:order.events}", groupId = "${spring.kafka.consumer.group-id:orchestration-group}")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getOrderId());
        try {
            orchestrator.startSaga(event);
        } catch (Exception e) {
            log.error("Error starting saga for order: {}", event.getOrderId(), e);
            // In a real system, we would send to DLQ here
        }
    }
}
