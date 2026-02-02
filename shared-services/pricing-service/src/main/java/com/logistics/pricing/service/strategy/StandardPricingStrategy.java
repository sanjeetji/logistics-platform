package com.logistics.pricing.service.strategy;

import com.logistics.pricing.dto.PricingDTOs.CalculatedPrice;
import com.logistics.pricing.dto.PricingDTOs.PriceRequest;
import com.logistics.pricing.model.RateCard;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public boolean supports(PriceRequest request) {
        // Default strategy if no specific contract logic applies
        return request.getTenantId() == null || request.getCustomerId() == null;
    }

    @Override
    public CalculatedPrice calculate(PriceRequest request, RateCard rateCard) {
        BigDecimal basePrice = rateCard.getBasePrice();
        
        BigDecimal distanceCost = rateCard.getPricePerKm().multiply(BigDecimal.valueOf(request.getDistanceKm()));
        BigDecimal timeCost = rateCard.getPricePerMinute().multiply(BigDecimal.valueOf(request.getEstimatedTimeMinutes()));
        
        BigDecimal subTotal = basePrice.add(distanceCost).add(timeCost);
        
        // Apply minimum price logic
        if (rateCard.getMinimumPrice() != null && subTotal.compareTo(rateCard.getMinimumPrice()) < 0) {
            subTotal = rateCard.getMinimumPrice();
        }

        BigDecimal surge = BigDecimal.ONE;
        if (request.isSurgeActive()) {
            surge = BigDecimal.valueOf(1.5); // Fixed surge for now, can be dynamic
            subTotal = subTotal.multiply(surge);
        }

        List<String> rules = new ArrayList<>();
        rules.add("Base Fare: " + basePrice);
        rules.add("Distance: " + request.getDistanceKm() + "km @ " + rateCard.getPricePerKm());
        rules.add("Time: " + request.getEstimatedTimeMinutes() + "min @ " + rateCard.getPricePerMinute());
        if (request.isSurgeActive()) {
            rules.add("Surge Applied: x" + surge);
        }

        return CalculatedPrice.builder()
                .totalPrice(subTotal.setScale(2, RoundingMode.HALF_UP))
                .basePrice(basePrice)
                .distancePrice(distanceCost)
                .timePrice(timeCost)
                .surgeMultiplier(surge)
                .currency("USD") // Should come from config/rate card
                .appliedRules(rules)
                .build();
    }
}
