package com.logistics.routing.optimizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Route Score Breakdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteScore {

    // Individual objective scores (0-100, higher is better)
    private Double costScore;
    private Double speedScore;
    private Double greenScore;
    private Double driverSatisfactionScore;
    
    // Weighted composite score (0-100)
    private Double compositeScore;
    
    // Raw metrics
    private Double totalCost;
    private Double totalDurationHours;
    private Double totalCO2Kg;
    private Double driverWorkloadScore;
    
    // Normalization factors (for scoring)
    private Double maxCost;
    private Double maxDuration;
    private Double maxCO2;
}
