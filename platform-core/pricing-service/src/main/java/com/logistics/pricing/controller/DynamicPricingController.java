package com.logistics.pricing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.pricing.dto.PriceCalculationRequest;
import com.logistics.pricing.dto.PriceCalculationResponse;
import com.logistics.pricing.model.RateCard;
import com.logistics.pricing.model.SurgePricingRule;
import com.logistics.pricing.service.DynamicPricingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class DynamicPricingController {

    private final DynamicPricingEngine pricingEngine;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PriceCalculationResponse>> calculatePrice(
            @RequestBody PriceCalculationRequest request) {
        
        // TODO: Get current demand from order service
        Integer currentDemand = 10; // Placeholder
        
        BigDecimal finalPrice = pricingEngine.calculateDynamicPrice(
                request.getDistanceKm(),
                request.getEstimatedMinutes(),
                request.getVehicleType(),
                currentDemand
        );

        PriceCalculationResponse response = PriceCalculationResponse.builder()
                .finalPrice(finalPrice)
                .isSurgeActive(false) // TODO: Implement surge detection
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Price calculated successfully"));
    }

    @PostMapping("/rate-cards")
    public ResponseEntity<ApiResponse<RateCard>> createRateCard(@RequestBody RateCard rateCard) {
        RateCard created = pricingEngine.createRateCard(rateCard);
        return ResponseEntity.ok(ApiResponse.success(created, "Rate card created successfully"));
    }

    @PostMapping("/surge-rules")
    public ResponseEntity<ApiResponse<SurgePricingRule>> createSurgeRule(
            @RequestBody SurgePricingRule rule) {
        SurgePricingRule created = pricingEngine.createSurgeRule(rule);
        return ResponseEntity.ok(ApiResponse.success(created, "Surge pricing rule created successfully"));
    }
}
