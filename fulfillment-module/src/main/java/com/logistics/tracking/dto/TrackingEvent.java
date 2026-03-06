package com.logistics.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEvent {
    private String orderId;
    private Long driverId; // Changed to Long to match DriverScore
    private String eventType; // START, UPDATE, DELIVERED, SLA_BREACH
    private Double currentLat;
    private Double currentLng;
    private Double estimatedTimeRemainingSeconds;
    private LocalDateTime timestamp;
    private String message;
}
