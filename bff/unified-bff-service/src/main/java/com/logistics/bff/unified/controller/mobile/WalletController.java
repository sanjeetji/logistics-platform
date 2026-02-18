package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.service.mobile.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mobile Wallet Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Wallet", description = "Wallet and transaction management for mobile app")
public class WalletController {

        private final WalletService walletService;

        @GetMapping("/balance")
        @Operation(summary = "Get balance")
        public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String userId) {
                log.info("Mobile wallet balance request for user: {}", userId);
                return ResponseEntity.ok(walletService.getBalance(userId));
        }

        @GetMapping("/transactions")
        @Operation(summary = "Get transactions")
        public ResponseEntity<Object> getTransactions(@RequestParam String userId) {
                log.info("Mobile wallet transactions request for user: {}", userId);
                return ResponseEntity.ok(walletService.getTransactions(userId));
        }

        @PostMapping("/add-money")
        @Operation(summary = "Add money to wallet")
        public ResponseEntity<Map<String, Object>> addMoney(
                        @RequestParam String userId,
                        @RequestBody Map<String, Object> paymentData) {
                log.info("Mobile wallet add money request for user: {}", userId);
                return ResponseEntity.ok(walletService.addMoney(userId, paymentData));
        }
}
