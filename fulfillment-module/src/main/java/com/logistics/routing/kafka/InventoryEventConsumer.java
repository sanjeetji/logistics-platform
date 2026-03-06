package com.logistics.routing.kafka;

import com.logistics.order.service.BackorderService;
import com.logistics.platform.event.dto.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for Inventory events to trigger real-time backorder recovery.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final BackorderService backorderService;

    @KafkaListener(topics = "inventory-service-events", groupId = "fulfillment-backorder-group")
    public void handleInventoryUpdate(InventoryUpdatedEvent event) {
        log.info("Received InventoryUpdateEvent for SKU: {} with action: {}", event.getSkuId(), event.getAction());

        if ("RESTOCKED".equalsIgnoreCase(event.getAction()) || "ADJUSTED".equalsIgnoreCase(event.getAction())) {
            if (event.getDelta() != null && event.getDelta() > 0) {
                log.info("Processing replenishment for SKU: {}. Attempting backorder recovery.", event.getSkuId());
                backorderService.recoverOrdersBySku(event.getSkuId());
            } else if ("RESTOCKED".equalsIgnoreCase(event.getAction())) {
                log.info("Processing restock for SKU: {}. Attempting backorder recovery.", event.getSkuId());
                backorderService.recoverOrdersBySku(event.getSkuId());
            }
        }
    }
}
