package com.logistics.marketplace.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class MarketTrendAnalyzer {

    // Mock data for now, in real world this would come from Kafka Streams or Database
    private final Random random = new Random();

    public MarketTrendDto analyzeTrend(String zoneId) {
        // Simulate fetching real-time data
        int activeOrders = random.nextInt(100);
        int activeDrivers = random.nextInt(50) + 1; // Avoid division by zero

        double demandRatio = (double) activeOrders / activeDrivers;
        DemandLevel demandLevel = calculateDemandLevel(demandRatio);
        double multiplier = calculateMultiplier(demandLevel);

        return MarketTrendDto.builder()
                .zoneId(zoneId)
                .activeOrders(activeOrders)
                .activeDrivers(activeDrivers)
                .demandLevel(demandLevel)
                .pricingMultiplier(BigDecimal.valueOf(multiplier))
                .build();
    }

    private DemandLevel calculateDemandLevel(double ratio) {
        if (ratio < 0.5) return DemandLevel.LOW;
        if (ratio < 1.0) return DemandLevel.NORMAL;
        if (ratio < 1.5) return DemandLevel.HIGH;
        return DemandLevel.CRITICAL;
    }

    private double calculateMultiplier(DemandLevel level) {
        return switch (level) {
            case LOW -> 0.9;
            case NORMAL -> 1.0;
            case HIGH -> 1.2;
            case CRITICAL -> 1.5;
        };
    }

    public enum DemandLevel {
        LOW, NORMAL, HIGH, CRITICAL
    }

    @lombok.Data
    @lombok.Builder
    public static class MarketTrendDto {
        private String zoneId;
        private int activeOrders;
        private int activeDrivers;
        private DemandLevel demandLevel;
        private BigDecimal pricingMultiplier;
    }
}
