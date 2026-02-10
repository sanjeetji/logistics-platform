package com.logistics.platform.dto.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String status; // ACTIVE, INACTIVE, ON_DUTY, OFF_DUTY
    private String vehicleId;
    private Double rating;
    private Integer totalDeliveries;
    private String currentLocation;
    private Boolean isAvailable;
    private String tenantId;
}
