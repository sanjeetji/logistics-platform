package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingResponse {
    private double basePrice;
    private float surgeMultiplier;
    private double finalPrice;
    private Map<String, Object> factors;
}
