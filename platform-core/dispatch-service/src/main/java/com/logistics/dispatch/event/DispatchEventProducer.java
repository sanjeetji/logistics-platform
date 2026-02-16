package com.logistics.dispatch.event;

import com.logistics.platform.event.dto.OrchestrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DispatchEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAssignmentSuccess(String orderId, String driverId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("driverId", driverId);

        OrchestrationEvent event = OrchestrationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .type(OrchestrationEvent.EventType.ORDER_DISPATCHED)
                .details(payload)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        log.info("Publishing OrderDispatched event for order: {}", orderId);
        kafkaTemplate.send("orchestration.event.order", orderId, event);
    }

    public void publishAssignmentFailure(String orderId, String reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("reason", reason);

        OrchestrationEvent event = OrchestrationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .type(OrchestrationEvent.EventType.ORDER_DISPATCH_FAILED)
                .details(payload)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        log.info("Publishing OrderDispatchFailed event for order: {} (Reason: {})", orderId, reason);
        kafkaTemplate.send("orchestration.event.order", orderId, event);
    }

    public void publishRouteUpdate(String driverId, List<String> stopSequence) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("driverId", driverId);
        payload.put("stopSequence", stopSequence);
        payload.put("type", "ROUTE_OPTIMIZED");

        log.info("Publishing RouteUpdate event for driver: {}", driverId);
        kafkaTemplate.send("fleet.route.updates", driverId, payload);
    }
}
