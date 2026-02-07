package com.logistics.payout.controller;

import com.logistics.payout.dto.PayoutDTOs;
import com.logistics.payout.model.PayoutRequest;
import com.logistics.payout.service.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @GetMapping("/wallet/{driverId}")
    public ResponseEntity<PayoutDTOs.WalletDTO> getWallet(@PathVariable String driverId) {
        return ResponseEntity.ok(payoutService.getWalletDetails(driverId));
    }

    @PostMapping("/earnings")
    public ResponseEntity<Void> addEarning(@RequestBody PayoutDTOs.EarningRequest request) {
        payoutService.addEarning(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<PayoutDTOs.PayoutResponse> requestWithdrawal(@RequestBody PayoutDTOs.WithdrawalRequest request) {
        PayoutRequest payout = payoutService.requestWithdrawal(request);
        
        return ResponseEntity.ok(PayoutDTOs.PayoutResponse.builder()
                .payoutId(payout.getPayoutId())
                .driverId(payout.getDriverId())
                .amount(payout.getAmount())
                .status(payout.getStatus())
                .requestedAt(payout.getRequestedAt())
                .build());
    }
}
