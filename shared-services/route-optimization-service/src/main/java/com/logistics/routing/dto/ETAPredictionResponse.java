package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ETA Prediction Response from ML Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ETAPredictionResponse {

    private String predictionId;
    private Long predictedDurationSeconds;
    private Double confidenceScore; // 0.0 - 1.0
    private String modelVersion;
    private Long timestamp;
    
    // Prediction breakdown
    private Long baselineDurationSeconds;
    private Long trafficAdjustmentSeconds;
    private Long weatherAdjustmentSeconds;
    private Long driverAdjustmentSeconds;
    
    // Uncertainty bounds
    private Long lowerBoundSeconds; // 95% confidence interval
    private Long upperBoundSeconds; // 95% confidence interval
    
    private String status; // SUCCESS, FALLBACK, ERROR
    private String errorMessage;
}
