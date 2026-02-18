package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {
    
    @GetMapping("/api/promotions/active")
    Object getActivePromotions(@RequestParam String customerId);
    
    @PostMapping("/api/promotions/apply")
    Object applyPromoCode(@RequestBody Object request);
    
    @GetMapping("/api/promotions/{promoCode}/validate")
    Object validatePromoCode(@PathVariable String promoCode);
}
