package com.logistics.pricing.service;

import com.logistics.pricing.model.RateCard;
import com.logistics.pricing.model.SurgePricingRule;
import com.logistics.pricing.repository.RateCardRepository;
import com.logistics.pricing.repository.SurgePricingRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPricingEngine {

    private final RateCardRepository rateCardRepository;
    private final SurgePricingRuleRepository surgePricingRuleRepository;

    public BigDecimal calculateDynamicPrice(Double distanceKm, Integer estimatedMinutes,
            String vehicleType, Integer currentDemand) {

        // Get active rate card
        RateCard rateCard = rateCardRepository.findCurrentActiveRateCard(LocalDateTime.now())
                .orElseGet(() -> rateCardRepository.findByIsDefaultTrue()
                        .orElseThrow(() -> new RuntimeException("No active rate card found")));

        // Calculate base price
        BigDecimal basePrice = calculateBasePrice(rateCard, distanceKm, estimatedMinutes, vehicleType);

        // Apply surge pricing
        BigDecimal surgeMultiplier = calculateSurgeMultiplier(currentDemand);

        BigDecimal finalPrice = basePrice.multiply(surgeMultiplier).setScale(2, RoundingMode.HALF_UP);

        // Ensure minimum fare
        if (rateCard.getMinimumFare() != null && finalPrice.compareTo(rateCard.getMinimumFare()) < 0) {
            finalPrice = rateCard.getMinimumFare();
        }

        log.info("Calculated price: base={}, surge={}, final={}", basePrice, surgeMultiplier, finalPrice);
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

    private BigDecimal calculateSurgeMultiplier(Integer currentDemand) {
        BigDecimal maxMultiplier = BigDecimal.ONE;

        // Check time-based surge
        LocalTime now = LocalTime.now();
        List<SurgePricingRule> timeRules = surgePricingRuleRepository.findActiveTimeBasedRules(now);
        for (SurgePricingRule rule : timeRules) {
            if (rule.getMultiplier().compareTo(maxMultiplier) > 0) {
                maxMultiplier = rule.getMultiplier();
                log.debug("Applied time-based surge: {} ({})", rule.getName(), maxMultiplier);
            }
        }

        // Check demand-based surge
        if (currentDemand != null) {
            List<SurgePricingRule> demandRules = surgePricingRuleRepository.findActiveDemandBasedRules(currentDemand);
            for (SurgePricingRule rule : demandRules) {
                if (rule.getMultiplier().compareTo(maxMultiplier) > 0) {
                    maxMultiplier = rule.getMultiplier();
                    log.debug("Applied demand-based surge: {} ({})", rule.getName(), maxMultiplier);
                }
            }
        }

        return maxMultiplier;
    }

    public RateCard createRateCard(RateCard rateCard) {
        // If this is set as default, unset all other defaults
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
