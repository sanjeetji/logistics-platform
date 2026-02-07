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
public class LocationUpdate {
    private String orderId;
    private String driverId;
    private Double latitude;
    private Double longitude;
    private String status; // e.g., IN_TRANSIT, DELIVERED
    private LocalDateTime timestamp;
}
