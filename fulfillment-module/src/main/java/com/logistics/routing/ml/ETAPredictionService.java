package com.logistics.routing.ml;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import com.logistics.routing.dto.RiskPredictionRequest;
import com.logistics.routing.dto.RiskPredictionResponse;
import com.logistics.routing.feign.MLServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ETA Prediction Service using ML and Risk Predictors
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ETAPredictionService {

    private final MLServiceClient mlServiceClient;
    private final DelayRiskService delayRiskService;

    @Value("${routing.optimization.ml.enabled:true}")
    private boolean mlEnabled;

    @Value("${routing.optimization.ml.confidence-threshold:0.85}")
    private double confidenceThreshold;

    /**
     * Predict ETA using ML model and calculate side-by-side Delay Risk
     */
    @Cacheable(value = "etaPredictions", key = "#request.routeId + '_' + #request.originLat + '_' + #request.destinationLat")
    public ETAPredictionResponse predictETA(ETAPredictionRequest request) {

        // 1. Enrich request with contextual features (weather, traffic, experience)
        enrichRequestWithContext(request);

        // 2. Assess Delay Risk heuristic regardless of ML availability
        RiskPredictionResponse riskAssessment = assessRouteRisk(request);

        if (!mlEnabled) {
            log.debug("ML ETA prediction disabled, using traffic duration");
            return createFallbackResponse(request, riskAssessment);
        }

        try {
            // Call ML service
            ETAPredictionResponse response = mlServiceClient.predictETA(request);

            // Validate confidence score
            if (response.getConfidenceScore() < confidenceThreshold) {
                log.warn("ML prediction confidence {} below threshold {}, using fallback",
                        response.getConfidenceScore(), confidenceThreshold);
                return createFallbackResponse(request, riskAssessment);
            }

            log.debug("ML ETA prediction: {}s (confidence: {}, model: {})",
                    response.getPredictedDurationSeconds(),
                    response.getConfidenceScore(),
                    response.getModelVersion());

            // Append Risk data
            appendRiskToResponse(response, riskAssessment);
            return response;

        } catch (Exception e) {
            log.error("Error calling ML service for ETA prediction", e);
            return createFallbackResponse(request, riskAssessment);
        }
    }

    private RiskPredictionResponse assessRouteRisk(ETAPredictionRequest request) {
        double trafficDelayPercent = 0.0;
        if (request.getCurrentTrafficDurationSeconds() != null && request.getDistanceKm() != null
                && request.getDistanceKm() > 0) {
            double baseDuration = (request.getDistanceKm() / 40.0) * 3600;
            if (request.getCurrentTrafficDurationSeconds() > baseDuration) {
                trafficDelayPercent = ((request.getCurrentTrafficDurationSeconds() - baseDuration) / baseDuration)
                        * 100.0;
            }
        }

        RiskPredictionRequest riskRequest = RiskPredictionRequest.builder()
                .routeId(request.getRouteId())
                .weatherCondition(request.getWeatherCondition())
                .driverExperienceLevel(request.getDriverExperienceLevel())
                .trafficDelayPercent(trafficDelayPercent)
                .distanceKm(request.getDistanceKm())
                .estimatedMinutes(request.getCurrentTrafficDurationSeconds() != null
                        ? request.getCurrentTrafficDurationSeconds() / 60
                        : 30L)
                .build();

        return delayRiskService.calculateRisk(riskRequest);
    }

    private void appendRiskToResponse(ETAPredictionResponse response, RiskPredictionResponse riskAssessment) {
        if (riskAssessment != null) {
            response.setDelayRiskProbability(riskAssessment.getRiskProbability());
            response.setRiskLevel(riskAssessment.getRiskLevel() != null ? riskAssessment.getRiskLevel().name() : null);
            response.setEstimatedDelayMinutes(riskAssessment.getEstimatedDelayMinutes());
            response.setContributingRiskFactors(riskAssessment.getContributingFactors());
        }
    }

    /**
     * Enrich request with contextual features
     */
    private void enrichRequestWithContext(ETAPredictionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getDayOfWeek() == null) {
            request.setDayOfWeek(now.getDayOfWeek().name());
        }

        if (request.getHourOfDay() == null) {
            request.setHourOfDay(now.getHour());
        }

        // Default weather if not provided
        if (request.getWeatherCondition() == null) {
            request.setWeatherCondition("CLEAR");
        }

        // Default vehicle type
        if (request.getVehicleType() == null) {
            request.setVehicleType("VAN");
        }

        // Default driver experience
        if (request.getDriverExperienceLevel() == null) {
            request.setDriverExperienceLevel("INTERMEDIATE");
        }
    }

    /**
     * Create fallback response when ML is unavailable
     */
    private ETAPredictionResponse createFallbackResponse(ETAPredictionRequest request,
            RiskPredictionResponse riskAssessment) {
        long fallbackDuration = request.getCurrentTrafficDurationSeconds() != null
                ? request.getCurrentTrafficDurationSeconds()
                : (long) ((request.getDistanceKm() / 40.0) * 3600);

        ETAPredictionResponse response = ETAPredictionResponse.builder()
                .predictedDurationSeconds(fallbackDuration)
                .confidenceScore(0.7)
                .modelVersion("fallback")
                .timestamp(System.currentTimeMillis())
                .baselineDurationSeconds(fallbackDuration)
                .status("FALLBACK")
                .build();

        appendRiskToResponse(response, riskAssessment);
        return response;
    }
}
