package com.logistics.bff.mobile.controller;

import com.logistics.bff.mobile.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Mobile Wallet Controller
 * Handles wallet operations for mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet", description = "Wallet management for mobile app")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    @Operation(summary = "Get balance", description = "Get current wallet balance")
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String userId) {
        log.info("Fetching wallet balance for user: {}", userId);
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/topup")
    @Operation(summary = "Top up wallet", description = "Add funds to wallet")
    public ResponseEntity<Map<String, Object>> topUp(
            @RequestParam String userId,
            @RequestBody Map<String, Object> topUpData) {
        log.info("Processing wallet top-up for user: {}", userId);
        return ResponseEntity.ok(walletService.topUp(userId, topUpData));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transactions", description = "Get wallet transaction history")
    public ResponseEntity<List<Map<String, Object>>> getTransactions(
            @RequestParam String userId,
            @RequestParam(required = false) Integer limit) {
        log.info("Fetching wallet transactions for user: {}", userId);
        return ResponseEntity.ok(walletService.getTransactions(userId, limit));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Withdraw funds from wallet")
    public ResponseEntity<Map<String, Object>> withdraw(
            @RequestParam String userId,
            @RequestBody Map<String, Object> withdrawData) {
        log.info("Processing wallet withdrawal for user: {}", userId);
        return ResponseEntity.ok(walletService.withdraw(userId, withdrawData));
    }
}
