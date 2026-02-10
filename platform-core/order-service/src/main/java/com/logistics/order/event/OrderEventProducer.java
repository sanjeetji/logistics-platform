package com.logistics.order.event;

import com.logistics.platform.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final StreamBridge streamBridge;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent: {}", event.getOrderId());
        streamBridge.send("orderCreatedSupplier-out-0", event);
    }

    public void publishAuditLog(com.logistics.platform.event.dto.AuditLogEvent event) {
        log.info("Publishing AuditLogEvent for entity: {}", event.getEntityId());
        streamBridge.send("auditLogSupplier-out-0", event);
    }
}
