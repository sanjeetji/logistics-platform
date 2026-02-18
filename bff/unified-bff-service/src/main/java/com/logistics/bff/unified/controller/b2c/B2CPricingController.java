package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.service.b2c.PricingService;
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
 */
@RestController
@RequestMapping("/api/v1/bff/b2c")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Pricing", description = "Pricing and promotions for B2C customers")
public class B2CPricingController {

        private final PricingService pricingService;

        @PostMapping("/pricing/calculate")
        @Operation(summary = "Calculate price")
        public ResponseEntity<Map<String, Object>> calculatePrice(@RequestBody Map<String, Object> pricingData) {
                log.info("B2C pricing calculation request received");
                return ResponseEntity.ok(pricingService.calculatePrice(pricingData));
        }

        @GetMapping("/pricing/estimates")
        @Operation(summary = "Get estimates")
        public ResponseEntity<List<Map<String, Object>>> getPriceEstimates(
                        @RequestParam String origin,
                        @RequestParam String destination,
                        @RequestParam(required = false) Double weight) {
                log.info("B2C price estimation request: {} -> {}", origin, destination);
                return ResponseEntity.ok(pricingService.getPriceEstimates(origin, destination, weight));
        }

        @GetMapping("/promotions/active")
        @Operation(summary = "Get promotions")
        public ResponseEntity<List<Object>> getActivePromotions() {
                log.info("B2C active promotions request");
                return ResponseEntity.ok(pricingService.getActivePromotions());
        }

        @PostMapping("/promotions/apply")
        @Operation(summary = "Apply promo code")
        public ResponseEntity<Map<String, Object>> applyPromoCode(@RequestBody Map<String, String> promoData) {
                log.info("B2C promo application request: {}", promoData.get("promoCode"));
                return ResponseEntity
                                .ok(pricingService.applyPromoCode(promoData.get("promoCode"), promoData.get("userId")));
        }
}
