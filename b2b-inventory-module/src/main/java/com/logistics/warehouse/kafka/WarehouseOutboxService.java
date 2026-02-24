package com.logistics.warehouse.kafka;

import com.logistics.platform.dto.warehouse.WarehouseOrderPackedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseOutboxService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String WAREHOUSE_EVENTS_TOPIC = "warehouse-events";

    public void publishOrderPackedEvent(WarehouseOrderPackedEvent event) {
        log.info("Publishing WarehouseOrderPackedEvent for order: {}", event.getOrderId());
        try {
            kafkaTemplate.send(WAREHOUSE_EVENTS_TOPIC, event.getOrderId(), event);
            log.debug("Successfully published packing event to Kafka");
        } catch (Exception e) {
            log.error("Failed to publish packing event to Kafka for order: {}", event.getOrderId(), e);
        }
    }
}
