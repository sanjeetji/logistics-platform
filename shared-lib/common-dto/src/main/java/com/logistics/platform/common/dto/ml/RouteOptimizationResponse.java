package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationResponse {
    private OptimizedRoute optimizedRoute;
    private List<OptimizedRoute> alternativeRoutes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizedRoute {
        private List<String> stopSequence;
        private double totalDistanceKm;
        private int estimatedTimeMinutes;
        private float optimizationScore;
    }
}
