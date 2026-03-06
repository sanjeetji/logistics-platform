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
        @Builder.Default
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
        @Builder.Default
        private Integer priority = 1;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlaFact {
        private String orderType;
        private String priority;
        private String customerTier; // SILVER, GOLD, PLATINUM

        // Output fields
        @Builder.Default
        private Integer deadlineMinutes = 0;
        private String escalationPolicy;
    }
}
