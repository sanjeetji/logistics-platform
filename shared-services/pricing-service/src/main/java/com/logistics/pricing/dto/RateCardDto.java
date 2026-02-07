package com.logistics.pricing.dto;

import com.logistics.pricing.model.RateCard.PricingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCardDto {
    private Long id;
    private String name;
    private String tenantId;
    private PricingType type;
    private String vehicleType;
    private BigDecimal basePrice;
    private BigDecimal pricePerKm;
    private BigDecimal pricePerMinute;
    private BigDecimal minimumPrice;
}
