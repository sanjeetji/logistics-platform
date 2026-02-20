package com.logistics.dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverScore {
    private Long driverId;
    private String driverName;
    private Double score;
    private Double distanceToPickup;
    private Integer estimatedTimeToPickup;
    private String vehicleType;
    private String currentStatus;
}
