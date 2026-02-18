package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.PaymentManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2B Payments Controller
 * Handles payment operations for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Payments", description = "Payment management for B2B clients")
public class B2BPaymentsController {

    private final PaymentManagementService paymentService;

    @GetMapping("/invoices")
    @Operation(summary = "Get invoices", description = "Get all invoices for client")
    public ResponseEntity<List<Map<String, Object>>> getInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching invoices - status: {}, period: {} to {}", status, startDate, endDate);
        return ResponseEntity.ok(paymentService.getInvoices(status, startDate, endDate));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transactions", description = "Get payment transaction history")
    public ResponseEntity<List<Map<String, Object>>> getTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching transactions for period: {} to {}", startDate, endDate);
        return ResponseEntity.ok(paymentService.getTransactions(startDate, endDate));
    }

    @PostMapping("/process")
    @Operation(summary = "Process payment", description = "Process payment for invoice")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> paymentData) {
        log.info("Processing payment");
        return ResponseEntity.ok(paymentService.processPayment(paymentData));
    }
}
