package com.logistics.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {
    private Double distanceKm;
    private Integer estimatedMinutes;
    private String vehicleType;
    private String pickupLocation;
    private String dropLocation;
    private String orderType;
    private String clientId; // Optional: Used for Enterprise Contract Pricing overrides
}
