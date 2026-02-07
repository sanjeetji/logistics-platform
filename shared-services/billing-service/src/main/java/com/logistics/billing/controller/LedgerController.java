package com.logistics.billing.controller;

import com.logistics.billing.dto.BillingDtos;
import com.logistics.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final BillingService billingService;

    @GetMapping("/{clientId}")
    public ResponseEntity<BillingDtos.LedgerResponse> getClientLedger(@PathVariable String clientId) {
        return ResponseEntity.ok(billingService.getClientLedger(clientId));
    }
}
