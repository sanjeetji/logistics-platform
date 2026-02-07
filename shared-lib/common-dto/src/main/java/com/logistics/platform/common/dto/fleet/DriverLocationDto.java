package com.logistics.platform.common.dto.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDto {
    private String driverId;
    private Double lat;
    private Double lng;
    private String vehicleType;
    private Double distanceKm;
}
