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

import com.logistics.pricing.service.ContractPricingService;
import com.logistics.pricing.service.ContractPricingService.ContractPriceResult;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingController {

    private final DynamicPricingEngine pricingEngine;
    private final OrderClient orderClient;
    private final ContractPricingService contractPricingService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PriceCalculationResponse>> calculatePrice(
            @RequestBody PriceCalculationRequest request) {

        Integer currentDemand = 0;
        try {
            currentDemand = orderClient.getDemand();
        } catch (Exception e) {
            log.error("Failed to fetch demand from order service, using default 0", e);
        }

        // 1. Check for Contract Override
        Optional<ContractPriceResult> contractOpt = Optional.empty();
        if (request.getClientId() != null && !request.getClientId().isBlank()) {
            contractOpt = contractPricingService.calculateContractPrice(
                    request.getClientId(), request.getVehicleType(), request.getDistanceKm(),
                    request.getEstimatedMinutes());
        }

        if (contractOpt.isPresent() && contractOpt.get().isOverride()) {
            ContractPriceResult contract = contractOpt.get();
            PriceCalculationResponse response = PriceCalculationResponse.builder()
                    .finalPrice(contract.getFinalPrice())
                    .isSurgeActive(false)
                    .rateCardVersion("CONTRACT: " + contract.getContractName())
                    .build();
            return ResponseEntity.ok(ApiResponse.success(response, "Contract price applied successfully"));
        }

        // 2. Standard Dynamic Calculation
        BigDecimal finalPrice = pricingEngine.calculateDynamicPrice(
                request.getDistanceKm(),
                request.getEstimatedMinutes(),
                request.getVehicleType(),
                currentDemand,
                request.getOrderType());

        // Simple surge detection logic
        boolean isSurgeActive = currentDemand > 10;
        String rateCardVersion = "STANDARD_DYNAMIC";

        // 3. Apply Contract Discount if applicable
        if (contractOpt.isPresent() && !contractOpt.get().isOverride()
                && contractOpt.get().getDiscountPercentage() != null) {
            BigDecimal discountMultiplier = BigDecimal.ONE
                    .subtract(contractOpt.get().getDiscountPercentage().divide(new BigDecimal("100")));
            finalPrice = finalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
            rateCardVersion = "CONTRACT_DISCOUNT: " + contractOpt.get().getContractName();
        }

        PriceCalculationResponse response = PriceCalculationResponse.builder()
                .finalPrice(finalPrice)
                .isSurgeActive(isSurgeActive)
                .rateCardVersion(rateCardVersion)
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
