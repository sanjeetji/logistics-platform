package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskPredictionRequest {
    private String routeId;
    private String weatherCondition; // CLEAR, RAIN, SNOW, STORM, FOG
    private Double trafficDelayPercent; // e.g., 10.5 for 10.5%
    private String driverExperienceLevel; // BEGINNER, INTERMEDIATE, EXPERT
    private Double distanceKm;
    private Long estimatedMinutes;
}
