package com.logistics.routing.optimizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optimization Objectives with Weights
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationObjectives {

    @Builder.Default
    private Double costWeight = 0.4;
    
    @Builder.Default
    private Double speedWeight = 0.3;
    
    @Builder.Default
    private Double greenWeight = 0.2;
    
    @Builder.Default
    private Double driverSatisfactionWeight = 0.1;

    /**
     * Validate weights sum to 1.0
     */
    public boolean isValid() {
        double sum = costWeight + speedWeight + greenWeight + driverSatisfactionWeight;
        return Math.abs(sum - 1.0) < 0.001; // Allow small floating point error
    }

    /**
     * Normalize weights to sum to 1.0
     */
    public void normalize() {
        double sum = costWeight + speedWeight + greenWeight + driverSatisfactionWeight;
        if (sum > 0) {
            costWeight /= sum;
            speedWeight /= sum;
            greenWeight /= sum;
            driverSatisfactionWeight /= sum;
        }
    }
}
