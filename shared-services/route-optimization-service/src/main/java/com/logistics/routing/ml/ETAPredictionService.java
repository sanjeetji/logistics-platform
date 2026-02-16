package com.logistics.routing.ml;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import com.logistics.routing.feign.MLServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * ETA Prediction Service using ML
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ETAPredictionService {

    private final MLServiceClient mlServiceClient;

    @Value("${routing.optimization.ml.enabled:true}")
    private boolean mlEnabled;

    @Value("${routing.optimization.ml.confidence-threshold:0.85}")
    private double confidenceThreshold;

    /**
     * Predict ETA using ML model
     */
    @Cacheable(value = "etaPredictions", key = "#request.routeId + '_' + #request.originLat + '_' + #request.destinationLat")
    public ETAPredictionResponse predictETA(ETAPredictionRequest request) {
        
        if (!mlEnabled) {
            log.debug("ML ETA prediction disabled, using traffic duration");
            return createFallbackResponse(request);
        }

        try {
            // Enrich request with contextual features
            enrichRequestWithContext(request);
            
            // Call ML service
            ETAPredictionResponse response = mlServiceClient.predictETA(request);
            
            // Validate confidence score
            if (response.getConfidenceScore() < confidenceThreshold) {
                log.warn("ML prediction confidence {} below threshold {}, using fallback",
                    response.getConfidenceScore(), confidenceThreshold);
                return createFallbackResponse(request);
            }
            
            log.debug("ML ETA prediction: {}s (confidence: {}, model: {})",
                response.getPredictedDurationSeconds(),
                response.getConfidenceScore(),
                response.getModelVersion());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error calling ML service for ETA prediction", e);
            return createFallbackResponse(request);
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
    private ETAPredictionResponse createFallbackResponse(ETAPredictionRequest request) {
        long fallbackDuration = request.getCurrentTrafficDurationSeconds() != null 
            ? request.getCurrentTrafficDurationSeconds()
            : (long) ((request.getDistanceKm() / 40.0) * 3600);
        
        return ETAPredictionResponse.builder()
            .predictedDurationSeconds(fallbackDuration)
            .confidenceScore(0.7)
            .modelVersion("fallback")
            .timestamp(System.currentTimeMillis())
            .baselineDurationSeconds(fallbackDuration)
            .status("FALLBACK")
            .build();
    }
}
