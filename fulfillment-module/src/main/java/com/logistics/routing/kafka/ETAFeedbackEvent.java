package com.logistics.routing.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ETA Feedback Event for ML Model Training
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ETAFeedbackEvent {

    private String feedbackId;
    private String routeId;
    private String deliveryId;
    
    // Prediction data
    private Long predictedDurationSeconds;
    private Double confidenceScore;
    private String modelVersion;
    private Long predictionTimestamp;
    
    // Actual data
    private Long actualDurationSeconds;
    private Long actualArrivalTimestamp;
    
    // Prediction accuracy
    private Long errorSeconds; // actual - predicted
    private Double errorPercentage;
    private Boolean withinConfidenceInterval;
    
    // Context at prediction time
    private String dayOfWeek;
    private Integer hourOfDay;
    private String weatherCondition;
    private String trafficLevel;
    private String vehicleType;
    private String driverExperienceLevel;
    
    // Route characteristics
    private Double distanceKm;
    private Integer numberOfStops;
    private Double totalWeightKg;
    
    private Long timestamp;
}
