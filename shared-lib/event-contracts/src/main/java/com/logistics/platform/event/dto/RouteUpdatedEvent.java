package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a route is updated dynamically
 * Reasons: NEW_ORDER, TRAFFIC, DRIVER_DELAY, CANCELLATION
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteUpdatedEvent {
    private String routeId;
    private String reason;
    private List<String> addedOrderIds;
    private List<String> removedOrderIds;
    private LocalDateTime updatedAt;
}
