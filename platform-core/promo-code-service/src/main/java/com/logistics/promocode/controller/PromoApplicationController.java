package com.logistics.promocode.controller;

import com.logistics.promocode.service.PromoCodeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/promos/apply")
@RequiredArgsConstructor
public class PromoApplicationController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<BigDecimal> applyPromoCode(@RequestBody ApplyPromoRequest request) {
        BigDecimal discount = promoCodeService.applyPromoCode(
                request.getCode(),
                request.getUserId(),
                request.getOrderId(),
                request.getOrderValue()
        );
        return ResponseEntity.ok(discount);
    }

    @PostMapping("/validate")
    public ResponseEntity<BigDecimal> validatePromoCode(@RequestBody ApplyPromoRequest request) {
        BigDecimal discount = promoCodeService.validatePromoCode(
                request.getCode(),
                request.getUserId(),
                request.getOrderValue()
        );
        return ResponseEntity.ok(discount);
    }

    @Data
    public static class ApplyPromoRequest {
        private String code;
        private String userId;
        private String orderId; // Optional for validation
        private BigDecimal orderValue;
    }
}
