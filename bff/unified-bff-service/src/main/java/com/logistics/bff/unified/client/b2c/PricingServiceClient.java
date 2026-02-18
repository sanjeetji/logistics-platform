package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pricing-service")
public interface PricingServiceClient {
    
    @PostMapping("/api/v1/pricing/quote")
    Double getQuote(@RequestBody Object request);
    
    @GetMapping("/api/v1/pricing/tiers")
    Object getPricingTiers();
    
    @PostMapping("/api/v1/pricing/calculate")
    Double calculatePrice(@RequestBody Object request);
    
    @PostMapping("/api/v1/pricing/calculate-simple")
    Double calculatePrice(@RequestParam String pickupAddress,
                         @RequestParam String deliveryAddress,
                         @RequestParam Double weight);
}
