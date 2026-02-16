package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a driver/vehicle status changes
 * Statuses: AVAILABLE, BUSY, OFFLINE, ON_BREAK
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetStatusChangedEvent {
    private String driverId;
    private String vehicleId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private String reason;
}
