package com.logistics.routing.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * What-If Analysis Request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatIfAnalysisRequest {

    private String baseRouteId;
    private ScenarioType scenarioType;
    private String description;
    
    // Scenario parameters
    private List<String> additionalStopIds;
    private List<String> removedStopIds;
    private Integer additionalVehicles;
    private Double trafficMultiplier; // 1.0 = normal, 1.5 = 50% slower
    private Integer delayMinutes;
    
    // Comparison metrics
    private Boolean compareCost;
    private Boolean compareTime;
    private Boolean compareCO2;
    private Boolean compareDriverWorkload;

    public enum ScenarioType {
        ADD_STOPS,
        REMOVE_STOPS,
        ADD_VEHICLES,
        TRAFFIC_INCREASE,
        DRIVER_DELAY,
        COMBINED
    }
}
