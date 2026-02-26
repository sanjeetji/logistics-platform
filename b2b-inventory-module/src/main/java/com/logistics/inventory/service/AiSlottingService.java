package com.logistics.inventory.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSlottingService {

    // private final MlServiceClient mlServiceClient;

    /**
     * Determines the optimal warehouse slot for a new inventory batch based on
     * velocity, size, and affinity.
     */
    public SlottingRecommendation getOptimalSlot(String warehouseId, String productId, double volumeCbm,
            String velocityClass) {
        log.info("Requesting AI Slotting recommendation for warehouse {} and product {}", warehouseId, productId);

        try {
            // ML Integration point:
            // return mlServiceClient.predictOptimalSlot(warehouseId, productId, ...);
            return calculateHeuristicSlotting(warehouseId, productId, velocityClass);
        } catch (Exception e) {
            log.warn("ML Slotting Service unavailable. Falling back to heuristic slotting. Error: {}", e.getMessage());
            return calculateHeuristicSlotting(warehouseId, productId, velocityClass);
        }
    }

    private SlottingRecommendation calculateHeuristicSlotting(String warehouseId, String productId,
            String velocityClass) {
        // Fallback strategy based on ABC analysis
        String suggestedZone = "ZONE_C"; // Default slow mover
        String suggestedAisle = "A-15";

        if ("FAST_MOVER".equalsIgnoreCase(velocityClass) || "A".equalsIgnoreCase(velocityClass)) {
            suggestedZone = "ZONE_A"; // Near dispatch
            suggestedAisle = "A-01";
        } else if ("MEDIUM_MOVER".equalsIgnoreCase(velocityClass) || "B".equalsIgnoreCase(velocityClass)) {
            suggestedZone = "ZONE_B";
            suggestedAisle = "A-05";
        }

        return SlottingRecommendation.builder()
                .warehouseId(warehouseId)
                .productId(productId)
                .recommendedZone(suggestedZone)
                .recommendedAisle(suggestedAisle)
                .recommendedBin(suggestedAisle + "-BIN-01") // Mocked available bin
                .confidenceScore(0.65) // Heuristics carry lower confidence
                .build();
    }

    @Data
    @Builder
    public static class SlottingRecommendation {
        private final String warehouseId;
        private final String productId;
        private final String recommendedZone;
        private final String recommendedAisle;
        private final String recommendedBin;
        private final Double confidenceScore;
    }
}
