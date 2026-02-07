package com.logistics.billing.controller;

import com.logistics.billing.dto.BillingDtos;
import com.logistics.billing.entity.Invoice;
import com.logistics.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BillingService billingService;

    @PostMapping("/generate")
    public ResponseEntity<Invoice> generateInvoice(@RequestBody BillingDtos.GenerateInvoiceRequest request) {
        return ResponseEntity.ok(billingService.generateInvoice(request));
    }

    @GetMapping("/pending/{clientId}")
    public ResponseEntity<List<Invoice>> getPendingInvoices(@PathVariable String clientId) {
        return ResponseEntity.ok(billingService.getPendingInvoices(clientId));
    }
}
