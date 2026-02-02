package com.logistics.pricing.service;

import com.logistics.pricing.dto.PricingDTOs.CalculatedPrice;
import com.logistics.pricing.dto.PricingDTOs.PriceRequest;
import com.logistics.pricing.model.RateCard;
import com.logistics.pricing.repository.RateCardRepository;
import com.logistics.pricing.service.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final RateCardRepository rateCardRepository;
    private final List<PricingStrategy> strategies;

    public CalculatedPrice calculatePrice(PriceRequest request) {
        // 1. Resolve Rate Card
        RateCard rateCard = resolveRateCard(request);

        // 2. Select Strategy
        PricingStrategy strategy = strategies.stream()
                .filter(s -> s.supports(request))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pricing strategy found"));

        // 3. Calculate
        return strategy.calculate(request, rateCard);
    }

    private RateCard resolveRateCard(PriceRequest request) {
        // Attempt to find specific contract rate card first
        if (request.getTenantId() != null) {
            return rateCardRepository.findByTenantIdAndVehicleTypeAndType(
                    request.getTenantId(), 
                    request.getVehicleType(), 
                    RateCard.PricingType.CONTRACT
            ).orElseGet(() -> getDefaultRateCard(request.getVehicleType()));
        }
        
        return getDefaultRateCard(request.getVehicleType());
    }

    private RateCard getDefaultRateCard(String vehicleType) {
        return rateCardRepository.findByTypeAndVehicleType(RateCard.PricingType.STANDARD, vehicleType)
                .orElseThrow(() -> new RuntimeException("No rate card found for vehicle: " + vehicleType));
    }
}
