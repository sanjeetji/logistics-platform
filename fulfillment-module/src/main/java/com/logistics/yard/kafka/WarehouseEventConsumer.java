package com.logistics.yard.kafka;

import com.logistics.platform.dto.warehouse.WarehouseOrderPackedEvent;
import com.logistics.yard.service.YardManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseEventConsumer {

    private final YardManagementService yardManagementService;

    @KafkaListener(topics = "warehouse-events", groupId = "fulfillment-yard-group")
    public void consumeWarehouseEvent(WarehouseOrderPackedEvent event) {
        log.info("Consumed Warehouse Event. Order {} is packed.", event.getOrderId());
        try {
            yardManagementService.scheduleDockAndDispatch(event);
        } catch (Exception e) {
            log.error("Failed to process packed event for order {} via Yard Management", event.getOrderId(), e);
        }
    }
}
