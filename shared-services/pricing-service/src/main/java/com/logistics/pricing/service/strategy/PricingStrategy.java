package com.logistics.pricing.service.strategy;

import com.logistics.pricing.dto.PricingDTOs.CalculatedPrice;
import com.logistics.pricing.dto.PricingDTOs.PriceRequest;
import com.logistics.pricing.model.RateCard;

public interface PricingStrategy {
    
    boolean supports(PriceRequest request);
    
    CalculatedPrice calculate(PriceRequest request, RateCard rateCard);
}
