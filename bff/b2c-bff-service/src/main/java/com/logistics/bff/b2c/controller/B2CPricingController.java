package com.logistics.bff.b2c.controller;

import com.logistics.bff.b2c.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2C Pricing Controller
 * Handles pricing and promotions for B2C customers
 */
@RestController
@RequestMapping("/api/v1/bff/b2c")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Pricing", description = "Pricing and promotions for B2C customers")
public class B2CPricingController {

    private final PricingService pricingService;

    @PostMapping("/pricing/calculate")
    @Operation(summary = "Calculate price", description = "Calculate delivery price based on details")
    public ResponseEntity<Map<String, Object>> calculatePrice(@RequestBody Map<String, Object> pricingData) {
        log.info("Calculating price for delivery");
        return ResponseEntity.ok(pricingService.calculatePrice(pricingData));
    }

    @GetMapping("/pricing/estimates")
    @Operation(summary = "Get estimates", description = "Get price estimates for different service levels")
    public ResponseEntity<List<Map<String, Object>>> getPriceEstimates(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) String packageType) {
        log.info("Getting price estimates from {} to {}", origin, destination);
        return ResponseEntity.ok(pricingService.getPriceEstimates(origin, destination, packageType));
    }

    @GetMapping("/promotions/active")
    @Operation(summary = "Get promotions", description = "Get all active promotions")
    public ResponseEntity<List<Map<String, Object>>> getActivePromotions(
            @RequestParam(required = false) String category) {
        log.info("Fetching active promotions for category: {}", category);
        return ResponseEntity.ok(pricingService.getActivePromotions(category));
    }

    @PostMapping("/promotions/apply")
    @Operation(summary = "Apply promo code", description = "Apply promotional code to order")
    public ResponseEntity<Map<String, Object>> applyPromoCode(@RequestBody Map<String, Object> promoData) {
        log.info("Applying promo code");
        return ResponseEntity.ok(pricingService.applyPromoCode(promoData));
    }
}
