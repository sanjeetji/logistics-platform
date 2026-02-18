package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.client.b2b.PaymentServiceClient;
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
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Payments", description = "Payment management for B2B clients")
public class B2BPaymentsController {

        private final PaymentServiceClient paymentClient;

        @GetMapping("/history")
        @Operation(summary = "Get payment history")
        public ResponseEntity<List<Object>> getPaymentHistory(
                        @RequestParam String tenantId,
                        @RequestParam(required = false) String status) {
                log.info("B2B payment history request for tenant: {}", tenantId);
                return ResponseEntity.ok(paymentClient.getPaymentsByTenant(tenantId));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get payment details")
        public ResponseEntity<Object> getPaymentDetails(@PathVariable String id) {
                log.info("B2B payment details request: {}", id);
                return ResponseEntity.ok(paymentClient.getPaymentById(id));
        }

        @PostMapping("/refund")
        @Operation(summary = "Process refund")
        public ResponseEntity<Map<String, Object>> processRefund(@RequestBody Map<String, Object> refundData) {
                log.info("B2B refund processing request");
                return ResponseEntity
                                .ok(Map.of("status", "PROCESSING", "refundId", "REF-" + System.currentTimeMillis()));
        }
}
