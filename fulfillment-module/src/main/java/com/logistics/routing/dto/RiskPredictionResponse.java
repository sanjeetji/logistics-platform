package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskPredictionResponse {
    private Double riskProbability; // 0.0 to 1.0 (e.g. 0.85 = 85% chance of delay)
    private RiskLevel riskLevel;
    private Integer estimatedDelayMinutes;
    private List<String> contributingFactors;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
