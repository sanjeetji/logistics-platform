package com.logistics.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationResponse {
    private BigDecimal basePrice;
    private BigDecimal surgeMultiplier;
    private BigDecimal finalPrice;
    private String rateCardVersion;
    private String appliedSurgeRules;
    private Boolean isSurgeActive;
}
