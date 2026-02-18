package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {
    @GetMapping("/api/promotions/active")
    List<Object> getActivePromotions();

    @PostMapping("/api/promotions/apply")
    Object applyPromotion(@RequestParam String promoCode, @RequestParam String userId);

    @GetMapping("/api/promotions/{promoCode}/validate")
    Object validatePromotion(@PathVariable("promoCode") String promoCode);
}
