package com.logistics.pricing.controller;

import com.logistics.pricing.dto.PricingDTOs.CalculatedPrice;
import com.logistics.pricing.dto.PricingDTOs.PriceRequest;
import com.logistics.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public ResponseEntity<CalculatedPrice> calculatePrice(@RequestBody PriceRequest request) {
        return ResponseEntity.ok(pricingService.calculatePrice(request));
    }
}
