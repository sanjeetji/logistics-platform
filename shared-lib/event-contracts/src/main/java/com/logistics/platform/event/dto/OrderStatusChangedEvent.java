package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Event published when an order status changes
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderStatusChangedEvent extends BaseEvent {
    private String orderId;
    private String previousStatus;
    private String newStatus;
    private String changedBy;
    private String reason;
    private Map<String, Object> metadata;

    public static OrderStatusChangedEvent create(String orderId, String previousStatus, String newStatus) {
        return OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .eventType("ORDER_STATUS_CHANGED")
                .build();
    }
}
