package com.logistics.loyalty.controller;

import com.logistics.loyalty.model.LoyaltyProfile;
import com.logistics.loyalty.model.PointsTransaction;
import com.logistics.loyalty.service.LoyaltyService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<LoyaltyProfile> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyProfile(userId));
    }

    @PostMapping("/redeem")
    public ResponseEntity<Void> redeemPoints(@RequestBody RedeemRequest request) {
        loyaltyService.redeemPoints(request.getUserId(), request.getPoints());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PointsTransaction>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(loyaltyService.getHistory(userId));
    }

    @Data
    public static class RedeemRequest {
        private String userId;
        private Integer points;
    }
}
