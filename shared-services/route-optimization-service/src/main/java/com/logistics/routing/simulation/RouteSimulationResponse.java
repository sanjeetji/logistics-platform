package com.logistics.routing.simulation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Route Simulation Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSimulationResponse {

    private String simulationId;
    private String routeId;
    private SimulationStatus status;
    
    private List<SimulationStep> steps;
    private Double totalDistanceKm;
    private Long totalDurationMinutes;
    private LocalDateTime estimatedStartTime;
    private LocalDateTime estimatedEndTime;
    
    // Validation
    private Boolean isValid;
    private List<String> violations;
    private List<String> warnings;

    public enum SimulationStatus {
        SUCCESS,
        FAILED,
        PARTIAL
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimulationStep {
        private Integer stepNumber;
        private String stopId;
        private Double latitude;
        private Double longitude;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private Double distanceFromPreviousKm;
        private Long durationFromPreviousMinutes;
    }
}
