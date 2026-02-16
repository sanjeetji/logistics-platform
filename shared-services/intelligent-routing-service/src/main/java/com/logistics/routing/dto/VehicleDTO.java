package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private LocationDTO startLocation;
    private Integer capacityWeight;
    private Integer capacityVolume;
    private String vehicleType;
    private String fuelType;
    private Double costPerKm;
    private Double costPerFixed;
}
