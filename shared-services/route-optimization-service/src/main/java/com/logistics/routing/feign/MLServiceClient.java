package com.logistics.routing.feign;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for ML Service ETA Prediction
 */
@FeignClient(
    name = "ml-service",
    url = "${ml.service.url:http://localhost:8099}",
    fallback = MLServiceFallback.class
)
public interface MLServiceClient {

    /**
     * Predict ETA using ML model
     */
    @PostMapping("/api/v1/ml/eta/predict")
    ETAPredictionResponse predictETA(@RequestBody ETAPredictionRequest request);
}
