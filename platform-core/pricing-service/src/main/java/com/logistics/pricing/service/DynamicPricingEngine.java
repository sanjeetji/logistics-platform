package com.logistics.pricing.service;

import com.logistics.pricing.model.RateCard;
import com.logistics.pricing.model.SurgePricingRule;
import com.logistics.pricing.repository.RateCardRepository;
import com.logistics.pricing.repository.SurgePricingRuleRepository;
import com.logistics.platform.client.rules.RulesEngineClient;
import com.logistics.platform.common.dto.rules.RuleFacts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingEngine {

    private final RateCardRepository rateCardRepository;
    private final SurgePricingRuleRepository surgePricingRuleRepository;
    private final RulesEngineClient rulesEngineClient;

    public BigDecimal calculateDynamicPrice(Double distanceKm, Integer estimatedMinutes,
            String vehicleType, Integer currentDemand, String orderType) {

        // Get active rate card
        RateCard rateCard = rateCardRepository.findCurrentActiveRateCard(LocalDateTime.now())
                .orElseGet(() -> rateCardRepository.findByIsDefaultTrue()
                        .orElseThrow(() -> new RuntimeException("No active rate card found")));

        // Calculate base price
        BigDecimal basePrice = calculateBasePrice(rateCard, distanceKm, estimatedMinutes, vehicleType);

        // Call Rules Engine for adjustments
        RuleFacts.PricingFact fact = RuleFacts.PricingFact.builder()
                .orderType(orderType != null ? orderType : "B2C")
                .distanceKm(distanceKm)
                .baseRate(basePrice)
                .build();

        try {
            fact = rulesEngineClient.evaluatePricing(fact);
        } catch (Exception e) {
            log.error("Error calling rules engine, falling back to base price", e);
        }

        // Apply rules engine results
        BigDecimal finalPrice = fact.getCalculatedRate() != null ? fact.getCalculatedRate() : basePrice;
        finalPrice = finalPrice.multiply(BigDecimal.valueOf(fact.getSurgeMultiplier())).setScale(2,
                RoundingMode.HALF_UP);

        // Ensure minimum fare
        if (rateCard.getMinimumFare() != null && finalPrice.compareTo(rateCard.getMinimumFare()) < 0) {
            finalPrice = rateCard.getMinimumFare();
        }

        log.info("Calculated price: base={}, surge={}, final={} (RulesEngine: {})",
                basePrice, fact.getSurgeMultiplier(), finalPrice, fact.getPricingModel());
        return finalPrice;
    }

    private BigDecimal calculateBasePrice(RateCard rateCard, Double distanceKm,
            Integer estimatedMinutes, String vehicleType) {
        BigDecimal price = rateCard.getBaseRate();

        // Add distance cost
        BigDecimal distanceCost = rateCard.getPerKmRate()
                .multiply(BigDecimal.valueOf(distanceKm));
        price = price.add(distanceCost);

        // Add time cost
        if (rateCard.getPerMinuteRate() != null && estimatedMinutes != null) {
            BigDecimal timeCost = rateCard.getPerMinuteRate()
                    .multiply(BigDecimal.valueOf(estimatedMinutes));
            price = price.add(timeCost);
        }

        // Apply vehicle type multiplier
        if (rateCard.getVehicleTypeMultipliers() != null && vehicleType != null) {
            BigDecimal vehicleMultiplier = rateCard.getVehicleTypeMultipliers()
                    .getOrDefault(vehicleType, BigDecimal.ONE);
            price = price.multiply(vehicleMultiplier);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public RateCard createRateCard(RateCard rateCard) {
        if (rateCard.getIsDefault()) {
            rateCardRepository.findByIsDefaultTrue().ifPresent(existing -> {
                existing.setIsDefault(false);
                rateCardRepository.save(existing);
            });
        }
        return rateCardRepository.save(rateCard);
    }

    public SurgePricingRule createSurgeRule(SurgePricingRule rule) {
        return surgePricingRuleRepository.save(Objects.requireNonNull(rule, "Surge rule must not be null"));
    }
}
