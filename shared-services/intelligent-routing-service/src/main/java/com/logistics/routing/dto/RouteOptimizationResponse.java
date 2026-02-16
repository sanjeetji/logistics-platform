package com.logistics.routing.dto;

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

    private List<OptimizedRouteDTO> routes;

    private Double totalDistanceKm;
    private Integer estimatedTimeMinutes;
    private String algorithm;
    private Double savingsPercentage;
    private Double totalCo2EmissionsKg;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizedRouteDTO {
        private Long vehicleId;
        private List<LocationDTO> waypoints;
        private Double distanceKm;
        private Integer timeMinutes;
        private Double co2EmissionsKg;
    }
}
