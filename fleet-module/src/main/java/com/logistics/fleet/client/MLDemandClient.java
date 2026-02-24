package com.logistics.fleet.client;

import com.logistics.platform.common.dto.ml.DemandPredictionRequest;
import com.logistics.platform.common.dto.ml.DemandPredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ml-service-demand", url = "${ml.service.url}")
public interface MLDemandClient {

    @PostMapping("/api/v1/ml/predict/demand")
    DemandPredictionResponse predictDemand(@RequestBody DemandPredictionRequest request);
}
