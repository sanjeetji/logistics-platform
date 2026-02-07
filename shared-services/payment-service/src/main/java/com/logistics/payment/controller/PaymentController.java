package com.logistics.payment.controller;

import com.logistics.payment.dto.PaymentDtos;
import com.logistics.payment.entity.Transaction;
import com.logistics.payment.entity.Wallet;
import com.logistics.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/wallet")
    public ResponseEntity<Wallet> createWallet(@RequestParam String userId) {
        return ResponseEntity.ok(paymentService.createWallet(userId));
    }

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getWallet(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<Wallet> topUp(@RequestBody PaymentDtos.TopUpRequest request) {
        return ResponseEntity.ok(paymentService.topUp(request));
    }

    @PostMapping("/process")
    public ResponseEntity<Void> processPayment(@RequestBody PaymentDtos.PaymentRequest request) {
        paymentService.processPayment(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getHistory(userId));
    }
}
