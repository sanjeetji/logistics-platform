package com.logistics.routing.feign;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for ML Service Client
 */
@Component
@Slf4j
public class MLServiceFallback implements MLServiceClient {

    @Override
    public ETAPredictionResponse predictETA(ETAPredictionRequest request) {
        log.warn("ML Service unavailable, using fallback ETA prediction");
        
        // Fallback: use traffic duration with 20% buffer
        long fallbackDuration = request.getCurrentTrafficDurationSeconds() != null 
            ? (long) (request.getCurrentTrafficDurationSeconds() * 1.2)
            : (long) ((request.getDistanceKm() / 40.0) * 3600); // 40 km/h average
        
        return ETAPredictionResponse.builder()
            .predictedDurationSeconds(fallbackDuration)
            .confidenceScore(0.5)
            .modelVersion("fallback")
            .timestamp(System.currentTimeMillis())
            .baselineDurationSeconds(fallbackDuration)
            .status("FALLBACK")
            .errorMessage("ML Service unavailable")
            .build();
    }
}
