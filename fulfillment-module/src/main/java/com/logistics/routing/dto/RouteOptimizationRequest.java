package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Route Optimization Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteOptimizationRequest {

    private String tenantId;
    private List<DeliveryStop> stops;
    private List<Vehicle> vehicles;
    private OptimizationObjective objective;
    private OptimizationConstraints constraints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryStop {
        private String stopId;
        private String orderId;
        private Double latitude;
        private Double longitude;
        private String address;
        private Integer demandWeight; // kg
        private Integer demandVolume; // cubic meters
        private LocalDateTime timeWindowStart;
        private LocalDateTime timeWindowEnd;
        private Integer serviceDurationMinutes;
        private Integer priority; // 1-5, 5 being highest
        private Boolean isPickup;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Vehicle {
        private String vehicleId;
        private String driverId;
        private Double startLatitude;
        private Double startLongitude;
        private Double endLatitude;
        private Double endLongitude;
        private Integer capacityWeight; // kg
        private Integer capacityVolume; // cubic meters
        private LocalDateTime shiftStart;
        private LocalDateTime shiftEnd;
        private List<String> skills; // e.g., "FRAGILE", "REFRIGERATED"
        private Double costPerKm;
        private Double costPerHour;
    }

    public enum OptimizationObjective {
        MINIMIZE_COST,
        MINIMIZE_TIME,
        MINIMIZE_DISTANCE,
        MINIMIZE_CO2,
        BALANCED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptimizationConstraints {
        private Boolean respectTimeWindows;
        private Boolean respectCapacity;
        private Boolean respectSkills;
        private Boolean allowSplitDeliveries;
        private Integer maxStopsPerRoute;
        private Integer maxDurationMinutes;
    }
}
