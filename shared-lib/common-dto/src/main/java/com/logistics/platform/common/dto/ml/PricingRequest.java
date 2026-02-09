package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingRequest {
    private String region;
    private double distanceKm;
    private String vehicleType;
    private String timeOfDay;
    private int currentDemand;
}
