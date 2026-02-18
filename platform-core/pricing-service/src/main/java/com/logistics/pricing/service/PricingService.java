package com.logistics.pricing.service;

import com.logistics.platform.common.dto.pricing.PriceEstimateRequest;
import com.logistics.platform.common.dto.pricing.PriceEstimateResponse;
import com.logistics.pricing.model.PriceEstimate;
import com.logistics.pricing.model.PricingRule;
import com.logistics.pricing.repository.PriceEstimateRepository;
import com.logistics.pricing.repository.PricingRuleRepository;
import com.logistics.pricing.client.MLPriceClient;
import com.logistics.platform.common.dto.pricing.enums.ServiceLevel;
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
        private final PriceEstimateRepository priceEstimateRepository;
        private final DistanceService distanceService;
        private final ServiceabilityService serviceabilityService;
        private final SurgeFactorService surgeFactorService;
        private final MLPriceClient mlPriceClient;

        private static final BigDecimal SERVICE_FEE_PERCENTAGE = new BigDecimal("0.05"); // 5%
        private static final int ESTIMATE_VALIDITY_MINUTES = 10;

        /**
         * Calculate price estimate for a delivery
         */
        @Transactional
        public List<PriceEstimateResponse> calculatePriceEstimate(PriceEstimateRequest request) {
                log.info("Calculating price estimate for vehicle type: {}", request.getVehicleType());

                // 0. Check Serviceability
                if (!serviceabilityService.isServiceable(request.getPickupLatitude(), request.getPickupLongitude())) {
                        throw new IllegalArgumentException("Pickup location is not serviceable");
                }
                if (!serviceabilityService.isServiceable(request.getDropLatitude(), request.getDropLongitude())) {
                        throw new IllegalArgumentException("Drop location is not serviceable");
                }

                // 1. Calculate distance
                double distance = distanceService.calculateDistance(
                                request.getPickupLatitude(), request.getPickupLongitude(),
                                request.getDropLatitude(), request.getDropLongitude());

                // 2. Estimate time
                int estimatedTime = distanceService.estimateTime(
                                request.getPickupLatitude(),
                                request.getPickupLongitude(),
                                request.getDropLatitude(),
                                request.getDropLongitude(),
                                request.getVehicleType());

                // 3. Get pricing rules
                LocalDateTime now = LocalDateTime.now();
                List<PricingRule> rules = pricingRuleRepository.findEffectiveRules(request.getVehicleType(), now);

                if (rules.isEmpty()) {
                        throw new RuntimeException(
                                        "No pricing rules found for vehicle type: " + request.getVehicleType());
                }

                // Resolve Zones
                String fromZoneId = serviceabilityService
                                .findServiceableZone(request.getPickupLatitude(), request.getPickupLongitude())
                                .map(com.logistics.pricing.model.ServiceableZone::getZoneName)
                                .orElse(null);
                String toZoneId = serviceabilityService
                                .findServiceableZone(request.getDropLatitude(), request.getDropLongitude())
                                .map(com.logistics.pricing.model.ServiceableZone::getZoneName)
                                .orElse(null);

                log.info("Resolved Zones: From={}, To={}", fromZoneId, toZoneId);

                // Filter by requested service level if provided
                List<ServiceLevel> targetLevels;
                if (request.getServiceLevel() != null) {
                        targetLevels = List.of(request.getServiceLevel());
                } else {
                        // Processing all available service levels in the rules
                        targetLevels = rules.stream()
                                        .map(PricingRule::getServiceLevel)
                                        .distinct()
                                        .toList();
                }

                List<PriceEstimateResponse> responses = new java.util.ArrayList<>();

                // Get Surge Multiplier from ML Service
                double surgeMultiplier = 1.0;
                try {
                        // Determine Time of Day
                        String timeOfDay = getTimeOfDay(now);
                        // Mock Demand (Random between 10 and 200 for demo)
                        int currentDemand = (int) (Math.random() * 190) + 10;

                        MLPriceClient.PriceRequest mlRequest = MLPriceClient.PriceRequest.builder()
                                        .region("DEFAULT") // Could use zone ID
                                        .distanceKm(distance)
                                        .vehicleType(request.getVehicleType())
                                        .timeOfDay(timeOfDay)
                                        .currentDemand(currentDemand)
                                        .build();

                        MLPriceClient.PriceResponse mlResponse = mlPriceClient.calculatePrice(mlRequest);
                        if (mlResponse != null && mlResponse.getSurgeMultiplier() != null) {
                                surgeMultiplier = mlResponse.getSurgeMultiplier();
                                log.info("ML Surge Applied: {}", surgeMultiplier);
                        }
                } catch (Exception e) {
                        log.error("ML Service failed, falling back to default surge: {}", e.getMessage());
                        surgeMultiplier = surgeFactorService.getSurgeMultiplier(
                                        request.getPickupLatitude(),
                                        request.getPickupLongitude());
                }

                for (ServiceLevel level : targetLevels) {
                        // Find best rule for this level
                        PricingRule bestRule = findBestRule(rules, level, fromZoneId, toZoneId);
                        if (bestRule != null) {
                                responses.add(calculateEstimateForRule(bestRule, request, distance, estimatedTime,
                                                surgeMultiplier));
                        }
                }

                if (responses.isEmpty()) {
                        throw new RuntimeException("No suitable pricing rule found for the requested criteria.");
                }

                // Handle Currency Conversion if requested
                if (request.getTargetCurrency() != null && !request.getTargetCurrency().equalsIgnoreCase("INR")) {
                        return responses.stream()
                                        .map(r -> convertCurrency(r, request.getTargetCurrency()))
                                        .toList();
                }

                return responses;
        }

        private String getTimeOfDay(LocalDateTime dateTime) {
                int hour = dateTime.getHour();
                if (hour >= 6 && hour < 12)
                        return "MORNING";
                if (hour >= 12 && hour < 17)
                        return "AFTERNOON";
                if (hour >= 17 && hour < 21)
                        return "EVENING";
                return "NIGHT";
        }

        private PricingRule findBestRule(List<PricingRule> rules, ServiceLevel level,
                        String fromZoneId, String toZoneId) {
                return rules.stream()
                                .filter(r -> r.getServiceLevel() == level)
                                // Filter matches
                                .filter(r -> (r.getFromZoneId() == null || r.getFromZoneId().equals(fromZoneId)))
                                .filter(r -> (r.getToZoneId() == null || r.getToZoneId().equals(toZoneId)))
                                // Sort by specificity and priority
                                .sorted((r1, r2) -> {
                                        // 1. Specificity score: Both > From/To > Global
                                        int s1 = getSpecificityScore(r1);
                                        int s2 = getSpecificityScore(r2);
                                        if (s1 != s2)
                                                return Integer.compare(s2, s1); // Higher specificity first

                                        // 2. Priority
                                        return Integer.compare(r2.getPriority(), r1.getPriority()); // Higher priority
                                                                                                    // first
                                })
                                .findFirst()
                                .orElse(null);
        }

        private int getSpecificityScore(PricingRule rule) {
                int score = 0;
                if (rule.getFromZoneId() != null)
                        score++;
                if (rule.getToZoneId() != null)
                        score++;
                return score;
        }

        private PriceEstimateResponse calculateEstimateForRule(PricingRule rule, PriceEstimateRequest request,
                        double distance, int estimatedTime, double surgeMultiplier) {
                // 4. Calculate base components
                BigDecimal baseFare = rule.getBaseFare();
                BigDecimal distanceFare = rule.getPerKmRate().multiply(BigDecimal.valueOf(distance));
                BigDecimal timeFare = rule.getPerMinuteRate() != null
                                ? rule.getPerMinuteRate().multiply(BigDecimal.valueOf(estimatedTime))
                                : BigDecimal.ZERO;

                // 4.1 Calculate weight fare (Volumetric or Actual)
                BigDecimal weightFare = BigDecimal.ZERO;
                if (rule.getAdditionalWeightRate() != null
                                && rule.getAdditionalWeightRate().compareTo(BigDecimal.ZERO) > 0) {
                        double length = request.getLength() != null ? request.getLength() : 0.0;
                        double width = request.getWidth() != null ? request.getWidth() : 0.0;
                        double height = request.getHeight() != null ? request.getHeight() : 0.0;
                        double weight = request.getWeight() != null ? request.getWeight() : 0.0;

                        int divisor = rule.getVolumetricDivisor() != null ? rule.getVolumetricDivisor() : 5000;
                        double volumetricWeight = (length * width * height) / divisor;
                        double chargeableWeight = Math.max(weight, volumetricWeight);

                        // Add new volumetric weight calculation if volume is provided
                        if (request.getVolume() != null) {
                                // Assuming request.getVolume() is in m^3, convert to cm^3 for volumetric weight
                                // calculation
                                double volumeInCm3 = request.getVolume() * 1_000_000;
                                double volumetricWeightFromVolume = volumeInCm3 / divisor;
                                chargeableWeight = Math.max(chargeableWeight, volumetricWeightFromVolume);
                        }

                        double baseWeight = rule.getBaseWeightKg() != null ? rule.getBaseWeightKg().doubleValue() : 0.0;

                        if (chargeableWeight > baseWeight) {
                                double extraWeight = chargeableWeight - baseWeight;
                                weightFare = rule.getAdditionalWeightRate().multiply(BigDecimal.valueOf(extraWeight))
                                                .setScale(2, RoundingMode.HALF_UP);
                        }
                }

                // 6. Calculate surge fare
                BigDecimal basePrice = baseFare.add(distanceFare).add(timeFare).add(weightFare);

                // Apply Delivery Type Multiplier
                double deliveryTypeMultiplier = 1.0;
                if (request.getDeliveryType() != null) {
                        switch (request.getDeliveryType()) {
                                case EXPRESS:
                                        deliveryTypeMultiplier = 1.5;
                                        break;
                                case ECONOMY:
                                        deliveryTypeMultiplier = 0.85;
                                        break;
                                case STANDARD:
                                default:
                                        deliveryTypeMultiplier = 1.0;
                        }
                }
                basePrice = basePrice.multiply(BigDecimal.valueOf(deliveryTypeMultiplier)).setScale(2,
                                RoundingMode.HALF_UP);

                BigDecimal surgeFare = basePrice.multiply(BigDecimal.valueOf(surgeMultiplier - 1.0))
                                .setScale(2, RoundingMode.HALF_UP);

                // 7. Calculate service fee
                BigDecimal serviceFee = basePrice.multiply(SERVICE_FEE_PERCENTAGE)
                                .setScale(2, RoundingMode.HALF_UP);

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
                                .serviceLevel(rule.getServiceLevel() != null ? rule.getServiceLevel().name()
                                                : "STANDARD")
                                .distance(distance)
                                .estimatedTime(estimatedTime)
                                .baseFare(baseFare)
                                .distanceFare(distanceFare)
                                .timeFare(timeFare)
                                .surgeFare(surgeFare)
                                .serviceFee(serviceFee)
                                .totalPrice(totalPrice)
                                .currency("INR")
                                .validUntil(LocalDateTime.now().plusMinutes(ESTIMATE_VALIDITY_MINUTES))
                                .build();

                priceEstimateRepository.save(Objects.requireNonNull(estimate, "Price estimate must not be null"));

                log.info("Price estimate calculated: {} INR for {} km (Service Level: {}, Surge: {})",
                                totalPrice, distance, rule.getServiceLevel(), surgeMultiplier);

                // 11. Build response
                return PriceEstimateResponse.builder()
                                .estimateId(estimateId)
                                .estimateId(estimateId)
                                .vehicleType(request.getVehicleType())
                                .serviceLevel(rule.getServiceLevel())
                                .deliveryType(request.getDeliveryType() != null ? request.getDeliveryType()
                                                : PriceEstimateRequest.DeliveryType.STANDARD)
                                .distance(distance)
                                .estimatedTime(estimatedTime)
                                .breakdown(PriceEstimateResponse.PriceBreakdown.builder()
                                                .baseFare(baseFare)
                                                .distanceFare(distanceFare)
                                                .timeFare(timeFare)
                                                .weightFare(weightFare)
                                                .surgeFare(surgeFare)
                                                .serviceFee(serviceFee)
                                                .build())
                                .totalPrice(totalPrice)
                                .currency("INR")
                                .validUntil(estimate.getValidUntil())
                                .surgeMultiplier(surgeMultiplier)
                                .build();
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
                                .serviceLevel(original.getServiceLevel())
                                .distance(original.getDistance())
                                .estimatedTime(original.getEstimatedTime())
                                .totalPrice(original.getTotalPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP))
                                .currency(targetCurrency.toUpperCase())
                                .validUntil(original.getValidUntil())
                                .surgeMultiplier(original.getSurgeMultiplier())
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
                                                .weightFare(
                                                                original.getBreakdown().getWeightFare().multiply(rate)
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
