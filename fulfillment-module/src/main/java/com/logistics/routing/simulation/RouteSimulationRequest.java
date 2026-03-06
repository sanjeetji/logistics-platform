package com.logistics.routing.simulation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Route Simulation Request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSimulationRequest {

    private String routeId;
    private List<SimulationStop> stops;
    private Double startLatitude;
    private Double startLongitude;
    
    // Simulation parameters
    private Integer speedKmh;
    private Integer stopDurationMinutes;
    private Boolean includeTraffic;
    private Boolean includeBreaks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimulationStop {
        private String stopId;
        private Double latitude;
        private Double longitude;
        private Integer serviceTimeMinutes;
    }
}
