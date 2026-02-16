package com.logistics.platform.common.dto.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

public class RuleFacts {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingFact {
        private String orderType;
        private Double weightKg;
        private Double volumeCbm;
        private Double distanceKm;
        private BigDecimal baseRate;

        // Output fields
        private BigDecimal calculatedRate;
        private Double surgeMultiplier = 1.0;
        private String pricingModel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchFact {
        private String orderType;
        private Double weightKg;
        private Double distanceKm;

        // Output fields
        private String strategyName;
        private Integer priority = 1;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SLAFact {
        private String orderType;
        private String priority;
        private String clientTier; // SILVER, GOLD, PLATINUM

        // Output fields
        private Integer targetDurationMinutes = 0;
        private String escalationPolicy;
    }
}
