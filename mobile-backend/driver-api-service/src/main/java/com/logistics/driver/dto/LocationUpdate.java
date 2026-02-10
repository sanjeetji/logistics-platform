package com.logistics.driver.dto;

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
    private String driverId;
    private Double latitude;
    private Double longitude;
    private Double accuracy; // in meters
    private Double speed; // in km/h
    private Double heading; // in degrees
    private LocalDateTime timestamp;
    private String orderId; // Current order being serviced
    private String status; // IDLE, EN_ROUTE_TO_PICKUP, EN_ROUTE_TO_DROP, etc.
}
