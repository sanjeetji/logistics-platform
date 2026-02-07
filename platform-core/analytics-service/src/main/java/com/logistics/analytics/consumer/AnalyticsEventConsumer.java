package com.logistics.analytics.consumer;

import com.logistics.analytics.dto.EventMessage;
import com.logistics.analytics.service.EventIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for analytics events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventConsumer {

    private final EventIngestionService eventIngestionService;

    @KafkaListener(topics = "order-events", groupId = "analytics-service")
    public void consumeOrderEvents(EventMessage event) {
        log.info("Received order event: {} for entity: {}", event.getEventType(), event.getEntityId());
        try {
            eventIngestionService.ingestEvent(event);
        } catch (Exception e) {
            log.error("Error processing order event: {}", event, e);
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "analytics-service")
    public void consumePaymentEvents(EventMessage event) {
        log.info("Received payment event: {} for entity: {}", event.getEventType(), event.getEntityId());
        try {
            eventIngestionService.ingestEvent(event);
        } catch (Exception e) {
            log.error("Error processing payment event: {}", event, e);
        }
    }

    @KafkaListener(topics = "driver-events", groupId = "analytics-service")
    public void consumeDriverEvents(EventMessage event) {
        log.info("Received driver event: {} for entity: {}", event.getEventType(), event.getEntityId());
        try {
            eventIngestionService.ingestEvent(event);
        } catch (Exception e) {
            log.error("Error processing driver event: {}", event, e);
        }
    }
}
