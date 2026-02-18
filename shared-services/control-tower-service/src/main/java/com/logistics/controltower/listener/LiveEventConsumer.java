package com.logistics.controltower.listener;

import com.logistics.controltower.model.DashboardMetric;
import com.logistics.controltower.service.LiveUpdateService;
import com.logistics.platform.event.dto.DispatchAssignedEvent;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiveEventConsumer {

    private final LiveUpdateService liveUpdateService;

    @KafkaListener(topics = "${spring.kafka.topics.order-events:order.events}", groupId = "${spring.kafka.consumer.group-id:control-tower-group}")
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getOrderId());

        DashboardMetric metric = DashboardMetric.builder()
                .metricName("NEW_ORDER")
                .value(1)
                .unit("COUNT")
                .timestamp(LocalDateTime.now())
                .tags(Map.of("orderId", event.getOrderId()))
                .build();

        liveUpdateService.broadcastMetric(metric);
    }

    @KafkaListener(topics = "${spring.kafka.topics.dispatch-events:dispatch.events}", groupId = "${spring.kafka.consumer.group-id:control-tower-group}")
    public void handleDispatchEvent(DispatchAssignedEvent event) {
        log.info("Received DispatchAssignedEvent for order: {}", event.getOrderId());

        DashboardMetric metric = DashboardMetric.builder()
                .metricName("DISPATCH_ASSIGNED")
                .value(1)
                .unit("COUNT")
                .timestamp(LocalDateTime.now())
                .tags(Map.of("orderId", event.getOrderId(), "driverId", String.valueOf(event.getDriverId())))
                .build();

        liveUpdateService.broadcastMetric(metric);
    }
}
