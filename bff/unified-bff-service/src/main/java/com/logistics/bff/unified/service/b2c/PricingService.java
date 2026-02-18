package com.logistics.bff.unified.service.b2c;

import com.logistics.bff.unified.client.PricingServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Pricing Service
 * Business logic for pricing and promotions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PricingServiceClient pricingClient;

    /**
     * Calculate delivery price
     */
    public Map<String, Object> calculatePrice(Map<String, Object> pricingData) {
        try {
            String origin = (String) pricingData.get("origin");
            String destination = (String) pricingData.get("destination");
            String serviceLevel = (String) pricingData.getOrDefault("serviceLevel", "STANDARD");
            Double weight = ((Number) pricingData.getOrDefault("weight", 1.0)).doubleValue();
            
            log.info("Calculating price: {} -> {}, service: {}, weight: {}", 
                    origin, destination, serviceLevel, weight);
            
            // Base price calculation
            double basePrice = 100.0;
            double distanceMultiplier = 1.5;
            double weightMultiplier = weight * 10;
            double serviceLevelMultiplier = serviceLevel.equals("EXPRESS") ? 1.5 : 1.0;
            
            double totalPrice = (basePrice + weightMultiplier) * distanceMultiplier * serviceLevelMultiplier;
            
            Map<String, Object> result = new HashMap<>();
            result.put("basePrice", basePrice);
            result.put("distanceCharge", basePrice * distanceMultiplier - basePrice);
            result.put("weightCharge", weightMultiplier);
            result.put("serviceCharge", totalPrice * (serviceLevelMultiplier - 1));
            result.put("subtotal", totalPrice);
            result.put("tax", totalPrice * 0.18);
            result.put("total", totalPrice * 1.18);
            result.put("currency", "INR");
            result.put("estimatedDelivery", serviceLevel.equals("EXPRESS") ? "Same Day" : "2-3 Days");
            
            return result;
        } catch (Exception e) {
            log.error("Failed to calculate price", e);
            throw new RuntimeException("Failed to calculate price: " + e.getMessage());
        }
    }

    /**
     * Get price estimates
     */
    @Cacheable(value = "price-estimates", key = "#origin + '-' + #destination + '-' + #packageType")
    public List<Map<String, Object>> getPriceEstimates(String origin, String destination, String packageType) {
        try {
            List<Map<String, Object>> estimates = new ArrayList<>();
            
            // Standard service
            estimates.add(Map.of(
                "serviceLevel", "STANDARD",
                "price", 150.00,
                "estimatedDelivery", "2-3 Days",
                "description", "Regular delivery service"
            ));
            
            // Express service
            estimates.add(Map.of(
                "serviceLevel", "EXPRESS",
                "price", 250.00,
                "estimatedDelivery", "Same Day",
                "description", "Fast delivery service"
            ));
            
            // Premium service
            estimates.add(Map.of(
                "serviceLevel", "PREMIUM",
                "price", 350.00,
                "estimatedDelivery", "4-6 Hours",
                "description", "Premium delivery with tracking"
            ));
            
            return estimates;
        } catch (Exception e) {
            log.error("Failed to get price estimates", e);
            throw new RuntimeException("Failed to get price estimates: " + e.getMessage());
        }
    }

    /**
     * Get active promotions
     */
    @Cacheable(value = "active-promotions", key = "#category")
    public List<Map<String, Object>> getActivePromotions(String category) {
        try {
            List<Map<String, Object>> promotions = new ArrayList<>();
            
            promotions.add(Map.of(
                "id", "PROMO001",
                "code", "WELCOME20",
                "description", "20% off on first order",
                "discountType", "PERCENTAGE",
                "discountValue", 20,
                "minOrderValue", 500.00,
                "validUntil", "2024-03-31"
            ));
            
            promotions.add(Map.of(
                "id", "PROMO002",
                "code", "FLAT100",
                "description", "Flat ₹100 off on orders above ₹1000",
                "discountType", "FIXED",
                "discountValue", 100,
                "minOrderValue", 1000.00,
                "validUntil", "2024-02-29"
            ));
            
            return promotions;
        } catch (Exception e) {
            log.error("Failed to get active promotions", e);
            throw new RuntimeException("Failed to get active promotions: " + e.getMessage());
        }
    }

    /**
     * Apply promo code
     */
    public Map<String, Object> applyPromoCode(Map<String, Object> promoData) {
        try {
            String promoCode = (String) promoData.get("promoCode");
            Double orderValue = ((Number) promoData.get("orderValue")).doubleValue();
            
            log.info("Applying promo code: {} to order value: {}", promoCode, orderValue);
            
            Map<String, Object> result = new HashMap<>();
            
            // Validate and apply promo code
            if ("WELCOME20".equals(promoCode) && orderValue >= 500) {
                double discount = orderValue * 0.20;
                result.put("valid", true);
                result.put("discountAmount", discount);
                result.put("finalAmount", orderValue - discount);
                result.put("message", "Promo code applied successfully!");
            } else if ("FLAT100".equals(promoCode) && orderValue >= 1000) {
                result.put("valid", true);
                result.put("discountAmount", 100.00);
                result.put("finalAmount", orderValue - 100);
                result.put("message", "Promo code applied successfully!");
            } else {
                result.put("valid", false);
                result.put("discountAmount", 0.00);
                result.put("finalAmount", orderValue);
                result.put("message", "Invalid promo code or minimum order value not met");
            }
            
            return result;
        } catch (Exception e) {
            log.error("Failed to apply promo code", e);
            throw new RuntimeException("Failed to apply promo code: " + e.getMessage());
        }
    }
}
