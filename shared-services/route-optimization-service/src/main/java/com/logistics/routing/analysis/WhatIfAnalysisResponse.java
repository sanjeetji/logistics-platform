package com.logistics.routing.analysis;

import com.logistics.routing.optimizer.RouteScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What-If Analysis Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatIfAnalysisResponse {

    private String analysisId;
    private String scenarioDescription;
    
    // Baseline metrics
    private RouteScore baselineScore;
    private Double baselineCost;
    private Double baselineDuration;
    private Double baselineCO2;
    
    // Scenario metrics
    private RouteScore scenarioScore;
    private Double scenarioCost;
    private Double scenarioDuration;
    private Double scenarioCO2;
    
    // Impact analysis
    private Double costDelta;
    private Double costDeltaPercent;
    private Double durationDelta;
    private Double durationDeltaPercent;
    private Double co2Delta;
    private Double co2DeltaPercent;
    
    // Recommendation
    private String recommendation;
    private Boolean recommendApply;
    
    private Long computationTimeMs;
}
