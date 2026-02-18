package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pricing-service")
public interface PricingServiceClient {
    @PostMapping("/api/v1/pricing/quote")
    Object getQuote(@RequestBody Object request);

    @GetMapping("/api/v1/pricing/tiers")
    Object getPricingTiers();

    @PostMapping("/api/v1/pricing/calculate")
    Double calculatePrice(@RequestParam String pickupAddress,
            @RequestParam String deliveryAddress,
            @RequestParam Double weight);

    @PostMapping("/api/v1/pricing/calculate-simple")
    Double calculateSimplePrice(@RequestParam Double distance, @RequestParam Double weight);
}
