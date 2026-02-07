package com.logistics.route.client;

import com.logistics.route.dto.DeliveryTimePredictionRequest;
import com.logistics.route.dto.DeliveryTimePredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Client for ML Service integration
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MLServiceClient {

    private final RestTemplate restTemplate;
    
    @Value("${ml.service.url:http://localhost:8092}")
    private String mlServiceUrl;

    /**
     * Predict delivery time using ML service
     */
    public DeliveryTimePredictionResponse predictDeliveryTime(
            Double pickupLat, Double pickupLng,
            Double deliveryLat, Double deliveryLng,
            String vehicleType, String timeOfDay) {
        
        log.info("Calling ML service for delivery time prediction");
        
        DeliveryTimePredictionRequest request = DeliveryTimePredictionRequest.builder()
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .deliveryLat(deliveryLat)
                .deliveryLng(deliveryLng)
                .vehicleType(vehicleType)
                .timeOfDay(timeOfDay)
                .weatherCondition("CLEAR")
                .build();
        
        String url = mlServiceUrl + "/api/v1/ml/predict/delivery-time";
        
        try {
            DeliveryTimePredictionResponse response = restTemplate.postForObject(
                    url, request, DeliveryTimePredictionResponse.class);
            
            log.info("ML prediction: {} minutes (confidence: {})", 
                    response.getPredictedTimeMinutes(), response.getConfidence());
            
            return response;
        } catch (Exception e) {
            log.error("Failed to call ML service, using fallback", e);
            return fallbackPrediction();
        }
    }
    
    /**
     * Fallback prediction if ML service is unavailable
     */
    private DeliveryTimePredictionResponse fallbackPrediction() {
        log.warn("Using fallback delivery time prediction");
        
        Map<String, Object> factors = new HashMap<>();
        factors.put("fallback", true);
        factors.put("reason", "ML service unavailable");
        
        return DeliveryTimePredictionResponse.builder()
                .predictedTimeMinutes(30)
                .confidence(0.5)
                .factors(factors)
                .build();
    }
}
