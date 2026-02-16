package com.logistics.wallet.controller;

import com.logistics.wallet.model.Wallet;
import com.logistics.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestParam String userId) {
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/{userId}/topup")
    public ResponseEntity<Wallet> topUp(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String referenceId = (String) request.get("referenceId");
        return ResponseEntity.ok(walletService.topUp(userId, amount, referenceId));
    }

    @PostMapping("/{userId}/deduct")
    public ResponseEntity<Wallet> deduct(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String referenceId = (String) request.get("referenceId");
        String description = (String) request.get("description");
        return ResponseEntity.ok(walletService.deduct(userId, amount, referenceId, description));
    }
}
