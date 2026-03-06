package com.logistics.routing.ml;

import com.logistics.routing.dto.RiskPredictionRequest;
import com.logistics.routing.dto.RiskPredictionResponse;
import com.logistics.routing.dto.RiskPredictionResponse.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelayRiskServiceTest {

    private DelayRiskService delayRiskService;

    @BeforeEach
    void setUp() {
        delayRiskService = new DelayRiskService();
    }

    @Test
    void calculateRisk_withClearWeatherAndExpertDriver_shouldReturnLowRisk() {
        RiskPredictionRequest request = RiskPredictionRequest.builder()
                .weatherCondition("CLEAR")
                .trafficDelayPercent(5.0)
                .driverExperienceLevel("EXPERT")
                .estimatedMinutes(45L)
                .build();

        RiskPredictionResponse response = delayRiskService.calculateRisk(request);

        // Base 0.05 - 0.10 (Expert) = -0.05 -> clamped to 0.01
        assertEquals(0.01, response.getRiskProbability());
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
        assertTrue(response.getContributingFactors().contains("Expert driver mitigation"));
    }

    @Test
    void calculateRisk_withSnowAndBeginnerDriver_shouldReturnCriticalRisk() {
        RiskPredictionRequest request = RiskPredictionRequest.builder()
                .weatherCondition("SNOW")
                .trafficDelayPercent(15.0)
                .driverExperienceLevel("BEGINNER")
                .estimatedMinutes(60L)
                .build();

        RiskPredictionResponse response = delayRiskService.calculateRisk(request);

        // Base 0.05 + 0.35 (Snow) + 0.10 (Traffic) + 0.15 (Beginner) = 0.65 (High)
        assertEquals(0.65, response.getRiskProbability());
        assertEquals(RiskLevel.HIGH, response.getRiskLevel());
        assertTrue(response.getContributingFactors().contains("Hazardous snow/ice"));
        assertTrue(response.getContributingFactors().contains("Inexperienced driver on route"));
    }

    @Test
    void calculateRisk_withSevereStormAndTraffic_shouldReturnMaxRisk() {
        RiskPredictionRequest request = RiskPredictionRequest.builder()
                .weatherCondition("STORM")
                .trafficDelayPercent(60.0) // Severe traffic
                .estimatedMinutes(200L) // Fatigue
                .build();

        RiskPredictionResponse response = delayRiskService.calculateRisk(request);

        // Base 0.05 + 0.50 (Storm) + 0.40 (Traffic) + 0.15 (Fatigue) = 1.10 -> clamped
        // to 0.99
        assertEquals(0.99, response.getRiskProbability());
        assertEquals(RiskLevel.CRITICAL, response.getRiskLevel());
        assertTrue(response.getContributingFactors().contains("Severe storm warning"));
        assertTrue(response.getContributingFactors().contains("Severe traffic >50% delay"));
        assertTrue(response.getContributingFactors().contains("Long route driving fatigue risk"));
    }
}
