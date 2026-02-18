package com.logistics.bff.unified.service.b2c;

import com.logistics.bff.unified.client.b2c.PricingServiceClient;
import com.logistics.bff.unified.client.b2c.PromotionServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pricing Service
 * Business logic for pricing and promotions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PricingServiceClient pricingClient;
    private final PromotionServiceClient promotionClient;

    /**
     * Calculate delivery price
     */
    public Map<String, Object> calculatePrice(Map<String, Object> pricingData) {
        log.info("Calculating price for delivery inquiry");
        try {
            Double price = pricingClient.calculatePrice(
                    (String) pricingData.get("pickupAddress"),
                    (String) pricingData.get("deliveryAddress"),
                    (Double) pricingData.get("weight"));
            return Map.of("price", price, "currency", "INR");
        } catch (Exception e) {
            log.error("Pricing calculation failed", e);
            return Map.of("error", "Failed to calculate price", "mockPrice", 250.0);
        }
    }

    /**
     * Get price estimates
     */
    @Cacheable(value = "price-estimates", key = "#origin + '-' + #destination")
    public List<Map<String, Object>> getPriceEstimates(String origin, String destination, Double weight) {
        log.info("Estimating prices from {} to {}", origin, destination);
        List<Map<String, Object>> estimates = new ArrayList<>();

        estimates.add(Map.of(
                "serviceLevel", "STANDARD",
                "price", 150.00,
                "estimatedDelivery", "2-3 Days"));

        estimates.add(Map.of(
                "serviceLevel", "EXPRESS",
                "price", 300.00,
                "estimatedDelivery", "Same Day"));

        return estimates;
    }

    /**
     * Get active promotions
     */
    @Cacheable(value = "active-promotions")
    public List<Object> getActivePromotions() {
        try {
            return promotionClient.getActivePromotions();
        } catch (Exception e) {
            log.error("Failed to fetch promotions", e);
            return new ArrayList<>();
        }
    }

    /**
     * Apply promo code
     */
    public Map<String, Object> applyPromoCode(String promoCode, String userId) {
        log.info("Applying promo code: {} for user: {}", promoCode, userId);
        try {
            Object result = promotionClient.applyPromotion(promoCode, userId);
            return Map.of("success", true, "result", result);
        } catch (Exception e) {
            log.error("Promo code application failed", e);
            return Map.of("success", false, "message", "Invalid promo code");
        }
    }
}
