package com.logistics.payment.controller;

import com.logistics.payment.entity.ReconciliationRecord;
import com.logistics.payment.entity.Transaction;
import com.logistics.payment.model.PaymentWallet;
import com.logistics.payment.service.PaymentService;
import com.logistics.payment.service.ReconciliationService;
import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ReconciliationService reconciliationService;

    @PostMapping("/wallet")
    public ResponseEntity<PaymentWallet> createWallet(@RequestParam Long userId) {
        return ResponseEntity.ok(paymentService.createWallet(userId));
    }

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<PaymentWallet> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getWallet(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<PaymentWallet> topUp(@RequestBody PaymentDtos.TopUpRequest request) {
        return ResponseEntity.ok(paymentService.topUp(request));
    }

    @PostMapping("/topup/initiate")
    public ResponseEntity<Map<String, Object>> initiateTopUp(@RequestBody PaymentDtos.TopUpRequest request) {
        return ResponseEntity.ok(paymentService.initiateTopUp(request));
    }

    @PostMapping("/process")
    public ResponseEntity<Void> processPayment(@RequestBody PaymentDtos.PaymentRequest request) {
        paymentService.processPayment(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getHistory(userId));
    }

    @PostMapping("/reconcile")
    public ResponseEntity<ReconciliationRecord> reconcile(
            @RequestParam PaymentDtos.GatewayType gatewayType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reconciliationService.reconcile(gatewayType, from, to));
    }

    @PostMapping("/payout")
    public ResponseEntity<ApiResponse<Boolean>> processPayout(@RequestBody PaymentDtos.PayoutRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(paymentService.processPayout(request), "Payout processed successfully"));
    }
}
