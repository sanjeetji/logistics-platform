package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Route Optimization Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteOptimizationResponse {

    private String optimizationId;
    private String tenantId;
    private OptimizationStatus status;
    private List<OptimizedRoute> routes;
    private OptimizationMetrics metrics;
    private LocalDateTime createdAt;
    private Integer computationTimeMs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptimizedRoute {
        private String routeId;
        private String vehicleId;
        private String driverId;
        private List<RouteStop> stops;
        private RouteMetrics routeMetrics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteStop {
        private Integer sequence;
        private String stopId;
        private String orderId;
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime estimatedArrival;
        private LocalDateTime estimatedDeparture;
        private Integer serviceDurationMinutes;
        private Integer cumulativeWeight;
        private Integer cumulativeVolume;
        private Double distanceFromPreviousKm;
        private Integer travelTimeFromPreviousMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteMetrics {
        private Double totalDistanceKm;
        private Integer totalDurationMinutes;
        private Double totalCost;
        private Double estimatedCO2Kg;
        private Integer numberOfStops;
        private Double utilizationPercent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptimizationMetrics {
        private Double totalDistanceKm;
        private Integer totalDurationMinutes;
        private Double totalCost;
        private Double totalCO2Kg;
        private Integer totalStops;
        private Integer vehiclesUsed;
        private Double averageUtilization;
        private Double routeEfficiency; // 0-100%
    }

    public enum OptimizationStatus {
        PENDING,
        OPTIMIZING,
        COMPLETED,
        FAILED,
        PARTIAL
    }
}
