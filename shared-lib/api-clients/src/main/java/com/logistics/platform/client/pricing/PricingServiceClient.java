package com.logistics.platform.client.pricing;

import com.logistics.platform.common.dto.pricing.PriceEstimateRequest;
import com.logistics.platform.common.dto.pricing.PriceEstimateResponse;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "pricing-service", url = "${application.config.pricing-url}")
public interface PricingServiceClient {

    @PostMapping("/api/v1/pricing/estimate")
    ApiResponse<List<PriceEstimateResponse>> calculateEstimate(@RequestBody PriceEstimateRequest request);
}
