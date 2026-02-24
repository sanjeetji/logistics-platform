package com.logistics.payment.controller;

import com.logistics.payment.model.CODSettlement;
import com.logistics.payment.service.CODSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/cod-settlement")
@RequiredArgsConstructor
public class CODSettlementController {

    private final CODSettlementService settlementService;

    @PostMapping("/initiate")
    public ResponseEntity<CODSettlement> initiate(@RequestParam String orderId,
            @RequestParam String driverId,
            @RequestParam BigDecimal amount,
            @RequestParam String currency) {
        return ResponseEntity.ok(settlementService.initiateSettlement(orderId, driverId, amount, currency));
    }

    @PostMapping("/{orderId}/collect")
    public ResponseEntity<CODSettlement> collect(@PathVariable String orderId, @RequestParam String hubId) {
        return ResponseEntity.ok(settlementService.markAsCollected(orderId, hubId));
    }

    @PostMapping("/{orderId}/reconcile")
    public ResponseEntity<CODSettlement> reconcile(@PathVariable String orderId, @RequestParam String bankReference) {
        return ResponseEntity.ok(settlementService.reconcile(orderId, bankReference));
    }

    @GetMapping("/pending/{driverId}")
    public ResponseEntity<List<CODSettlement>> getPending(@PathVariable String driverId) {
        return ResponseEntity.ok(settlementService.getPendingCollections(driverId));
    }
}
