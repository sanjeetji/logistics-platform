package com.logistics.pricing.service;

import com.logistics.pricing.dto.PriceEstimateRequest;
import com.logistics.pricing.dto.PriceEstimateResponse;
import com.logistics.pricing.model.PriceEstimate;
import com.logistics.pricing.model.PricingRule;
import com.logistics.pricing.model.SurgeZone;
import com.logistics.pricing.repository.PriceEstimateRepository;
import com.logistics.pricing.repository.PricingRuleRepository;
import com.logistics.pricing.repository.SurgeZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Core pricing calculation service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

        private final PricingRuleRepository pricingRuleRepository;
        private final SurgeZoneRepository surgeZoneRepository;
        private final PriceEstimateRepository priceEstimateRepository;
        private final DistanceService distanceService;

        private static final BigDecimal SERVICE_FEE_PERCENTAGE = new BigDecimal("0.05"); // 5%
        private static final int ESTIMATE_VALIDITY_MINUTES = 10;

        /**
         * Calculate price estimate for a delivery
         */
        @Transactional
        public PriceEstimateResponse calculatePriceEstimate(PriceEstimateRequest request) {
                log.info("Calculating price estimate for vehicle type: {}", request.getVehicleType());

                // 1. Calculate distance
                double distance = distanceService.calculateDistance(
                                request.getPickupLatitude(), request.getPickupLongitude(),
                                request.getDropLatitude(), request.getDropLongitude());

                // 2. Estimate time
                int estimatedTime = distanceService.estimateTime(distance);

                // 3. Get pricing rule
                LocalDateTime now = LocalDateTime.now();
                PricingRule rule = pricingRuleRepository.findEffectiveRule(request.getVehicleType(), now)
                                .orElseThrow(() -> new RuntimeException(
                                                "No pricing rule found for vehicle type: " + request.getVehicleType()));

                // 4. Calculate base components
                BigDecimal baseFare = rule.getBaseFare();
                BigDecimal distanceFare = rule.getPerKmRate().multiply(BigDecimal.valueOf(distance));
                BigDecimal timeFare = rule.getPerMinuteRate() != null
                                ? rule.getPerMinuteRate().multiply(BigDecimal.valueOf(estimatedTime))
                                : BigDecimal.ZERO;

                // 5. Calculate surge multiplier
                double surgeMultiplier = calculateSurgeMultiplier(
                                request.getPickupLatitude(),
                                request.getPickupLongitude());

                // 6. Calculate surge fare
                BigDecimal basePrice = baseFare.add(distanceFare).add(timeFare);
                BigDecimal surgeFare = basePrice.multiply(BigDecimal.valueOf(surgeMultiplier - 1.0));

                // 7. Calculate service fee
                BigDecimal serviceFee = basePrice.multiply(SERVICE_FEE_PERCENTAGE);

                // 8. Calculate total
                BigDecimal totalPrice = basePrice.add(surgeFare).add(serviceFee);

                // 9. Apply minimum/maximum fare constraints
                if (rule.getMinimumFare() != null && totalPrice.compareTo(rule.getMinimumFare()) < 0) {
                        totalPrice = rule.getMinimumFare();
                }
                if (rule.getMaximumFare() != null && totalPrice.compareTo(rule.getMaximumFare()) > 0) {
                        totalPrice = rule.getMaximumFare();
                }

                // Round to 2 decimal places
                totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

                // 10. Save estimate
                String estimateId = UUID.randomUUID().toString();
                PriceEstimate estimate = PriceEstimate.builder()
                                .estimateId(estimateId)
                                .orderId(request.getOrderId())
                                .vehicleType(request.getVehicleType())
                                .distance(distance)
                                .estimatedTime(estimatedTime)
                                .baseFare(baseFare)
                                .distanceFare(distanceFare)
                                .timeFare(timeFare)
                                .surgeFare(surgeFare)
                                .serviceFee(serviceFee)
                                .totalPrice(totalPrice)
                                .currency("INR")
                                .validUntil(now.plusMinutes(ESTIMATE_VALIDITY_MINUTES))
                                .build();

                priceEstimateRepository.save(Objects.requireNonNull(estimate, "Price estimate must not be null"));

                log.info("Price estimate calculated: {} INR for {} km", totalPrice, distance);

                // 11. Build response
                PriceEstimateResponse response = PriceEstimateResponse.builder()
                                .estimateId(estimateId)
                                .vehicleType(request.getVehicleType())
                                .distance(distance)
                                .estimatedTime(estimatedTime)
                                .breakdown(PriceEstimateResponse.PriceBreakdown.builder()
                                                .baseFare(baseFare)
                                                .distanceFare(distanceFare)
                                                .timeFare(timeFare)
                                                .surgeFare(surgeFare)
                                                .serviceFee(serviceFee)
                                                .build())
                                .totalPrice(totalPrice)
                                .currency("INR")
                                .validUntil(estimate.getValidUntil())
                                .build();

                // 12. Handle Currency Conversion if requested
                if (request.getTargetCurrency() != null && !request.getTargetCurrency().equalsIgnoreCase("INR")) {
                        return convertCurrency(response, request.getTargetCurrency());
                }

                return response;
        }

        private PriceEstimateResponse convertCurrency(PriceEstimateResponse original, String targetCurrency) {
                BigDecimal rate = getExchangeRate(targetCurrency);
                if (rate == null) {
                        log.warn("Exchange rate not found for currency: {}. Returning INR.", targetCurrency);
                        return original;
                }

                return PriceEstimateResponse.builder()
                                .estimateId(original.getEstimateId())
                                .vehicleType(original.getVehicleType())
                                .distance(original.getDistance())
                                .estimatedTime(original.getEstimatedTime())
                                .totalPrice(original.getTotalPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP))
                                .currency(targetCurrency.toUpperCase())
                                .validUntil(original.getValidUntil())
                                .breakdown(PriceEstimateResponse.PriceBreakdown.builder()
                                                .baseFare(
                                                                original.getBreakdown().getBaseFare().multiply(rate)
                                                                                .setScale(2, RoundingMode.HALF_UP))
                                                .distanceFare(original.getBreakdown().getDistanceFare().multiply(rate)
                                                                .setScale(2,
                                                                                RoundingMode.HALF_UP))
                                                .timeFare(
                                                                original.getBreakdown().getTimeFare().multiply(rate)
                                                                                .setScale(2, RoundingMode.HALF_UP))
                                                .surgeFare(
                                                                original.getBreakdown().getSurgeFare().multiply(rate)
                                                                                .setScale(2, RoundingMode.HALF_UP))
                                                .serviceFee(original.getBreakdown().getServiceFee().multiply(rate)
                                                                .setScale(2,
                                                                                RoundingMode.HALF_UP))
                                                .build())
                                .build();
        }

        private BigDecimal getExchangeRate(String currency) {
                // Mock Exchange Rates (Base: INR)
                return switch (currency.toUpperCase()) {
                        case "USD" -> new BigDecimal("0.012");
                        case "EUR" -> new BigDecimal("0.011");
                        case "GBP" -> new BigDecimal("0.0095");
                        case "JPY" -> new BigDecimal("1.76");
                        default -> null;
                };
        }

        /**
         * Calculate surge multiplier based on location
         */
        private double calculateSurgeMultiplier(double latitude, double longitude) {
                LocalDateTime now = LocalDateTime.now();
                List<SurgeZone> activeZones = surgeZoneRepository.findActiveSurgeZones(now);

                double maxSurge = 1.0; // Default: no surge

                for (SurgeZone zone : activeZones) {
                        double distance = distanceService.calculateDistance(
                                        latitude, longitude,
                                        zone.getLatitude(), zone.getLongitude());

                        if (distance <= zone.getRadiusKm()) {
                                log.info("Location is in surge zone: {} with multiplier: {}",
                                                zone.getZoneName(), zone.getSurgeMultiplier());
                                maxSurge = Math.max(maxSurge, zone.getSurgeMultiplier());
                        }
                }

                return maxSurge;
        }

        /**
         * Get estimate by ID
         */
        public PriceEstimate getEstimateById(String estimateId) {
                return priceEstimateRepository.findByEstimateId(estimateId)
                                .orElseThrow(() -> new RuntimeException("Estimate not found: " + estimateId));
        }

        /**
         * Get estimate by order ID
         */
        public PriceEstimate getEstimateByOrderId(String orderId) {
                return priceEstimateRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("No estimate found for order: " + orderId));
        }
}
