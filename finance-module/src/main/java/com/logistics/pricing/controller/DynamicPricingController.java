package com.logistics.pricing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.api.order.OrderClient;
import com.logistics.pricing.dto.PriceCalculationRequest;
import com.logistics.pricing.dto.PriceCalculationResponse;
import com.logistics.pricing.model.RateCard;
import com.logistics.pricing.model.SurgePricingRule;
import com.logistics.pricing.service.DynamicPricingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingController {

    private final DynamicPricingEngine pricingEngine;
    private final OrderClient orderClient;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PriceCalculationResponse>> calculatePrice(
            @RequestBody PriceCalculationRequest request) {

        Integer currentDemand = 0;
        try {
            currentDemand = orderClient.getDemand();
        } catch (Exception e) {
            log.error("Failed to fetch demand from order service, using default 0", e);
        }

        BigDecimal finalPrice = pricingEngine.calculateDynamicPrice(
                request.getDistanceKm(),
                request.getEstimatedMinutes(),
                request.getVehicleType(),
                currentDemand,
                request.getOrderType());

        // Simple surge detection logic: if demand > 10, consider it surge
        boolean isSurgeActive = currentDemand > 10;

        PriceCalculationResponse response = PriceCalculationResponse.builder()
                .finalPrice(finalPrice)
                .isSurgeActive(isSurgeActive)
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
