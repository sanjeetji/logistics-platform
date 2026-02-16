package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when an order is assigned to a driver
 * Assignment strategies: MANUAL, AUTO, AI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchAssignedEvent {
    private String orderId;
    private String driverId;
    private String vehicleId;
    private LocalDateTime assignedAt;
    private LocalDateTime expectedPickupTime;
    private LocalDateTime expectedDeliveryTime;
    private String assignmentStrategy;
}
