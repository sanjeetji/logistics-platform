package com.logistics.pricing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.pricing.PriceEstimateRequest;
import com.logistics.platform.common.dto.pricing.PriceEstimateResponse;
import com.logistics.platform.common.dto.pricing.enums.ServiceLevel;
import com.logistics.pricing.model.PriceEstimate;
import com.logistics.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/estimate")
    public ResponseEntity<ApiResponse<java.util.List<PriceEstimateResponse>>> calculateEstimate(
            @Valid @RequestBody PriceEstimateRequest request) {
        java.util.List<PriceEstimateResponse> response = pricingService.calculatePriceEstimate(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Price estimate calculated successfully"));
    }

    @GetMapping("/estimate/{estimateId}")
    public ResponseEntity<ApiResponse<PriceEstimate>> getEstimate(@PathVariable String estimateId) {
        PriceEstimate estimate = pricingService.getEstimateById(estimateId);
        return ResponseEntity.ok(ApiResponse.success(estimate));
    }

    @GetMapping("/estimate/order/{orderId}")
    public ResponseEntity<ApiResponse<PriceEstimate>> getEstimateByOrder(@PathVariable String orderId) {
        PriceEstimate estimate = pricingService.getEstimateByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(estimate));
    }
}
