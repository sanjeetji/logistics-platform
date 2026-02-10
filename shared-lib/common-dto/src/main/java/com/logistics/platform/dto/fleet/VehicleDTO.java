package com.logistics.platform.dto.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private String id;
    private String vehicleNumber;
    private String vehicleType; // BIKE, CAR, VAN, TRUCK
    private String make;
    private String model;
    private Integer year;
    private String status; // ACTIVE, INACTIVE, MAINTENANCE
    private Double capacity; // in kg
    private String fuelType; // PETROL, DIESEL, ELECTRIC, CNG
    private String currentLocation;
    private String assignedDriverId;
    private String tenantId;
}
