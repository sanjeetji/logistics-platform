package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ETA Prediction Request for ML Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ETAPredictionRequest {

    private String routeId;
    private String orderId;
    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
    private Double distanceKm;
    private Long currentTrafficDurationSeconds;
    private Double trafficDelayPercent;

    // Contextual features
    private String dayOfWeek; // MONDAY, TUESDAY, etc.
    private Integer hourOfDay; // 0-23
    private String weatherCondition; // CLEAR, RAIN, SNOW, etc.
    private Double temperatureCelsius;
    private String vehicleType; // VAN, TRUCK, MOTORCYCLE
    private String driverExperienceLevel; // NOVICE, INTERMEDIATE, EXPERT

    // Historical features
    private Double averageSpeedKmh;
    private Integer numberOfStops;
    private Double totalWeightKg;

    // Additional metadata
    private Map<String, Object> additionalFeatures;
}
