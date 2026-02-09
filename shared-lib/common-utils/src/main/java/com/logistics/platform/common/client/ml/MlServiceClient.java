package com.logistics.platform.common.client.ml;

import com.logistics.platform.common.dto.ml.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ml-service", url = "${ml.service.url:http://ml-service:8092}")
public interface MlServiceClient {

    @PostMapping("/api/v1/ml/predict/demand")
    DemandPredictionResponse predictDemand(@RequestBody DemandPredictionRequest request);

    @PostMapping("/api/v1/ml/predict/delivery-time")
    DeliveryTimePredictionResponse predictDeliveryTime(@RequestBody DeliveryTimePredictionRequest request);

    @PostMapping("/api/v1/ml/optimize/route")
    RouteOptimizationResponse optimizeRoute(@RequestBody RouteOptimizationRequest request);

    @PostMapping("/api/v1/ml/pricing/calculate")
    PricingResponse calculatePricing(@RequestBody PricingRequest request);
}
