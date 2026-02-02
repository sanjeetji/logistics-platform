package com.logistics.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class PricingDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRequest {
        private String tenantId;
        private String customerId; // Optional, for contracts
        private String vehicleType;
        private double distanceKm;
        private double estimatedTimeMinutes;
        private boolean isSurgeActive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculatedPrice {
        private BigDecimal totalPrice;
        private BigDecimal basePrice;
        private BigDecimal distancePrice;
        private BigDecimal timePrice;
        private BigDecimal surgeMultiplier;
        private String currency;
        private List<String> appliedRules;
    }
}
