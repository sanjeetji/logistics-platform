package com.logistics.billing.controller;

import com.logistics.billing.dto.BillingDtos;
import com.logistics.billing.entity.Invoice;
import com.logistics.billing.repository.InvoiceRepository;
import com.logistics.billing.service.BillingService;
import com.logistics.billing.service.InvoiceGeneratorService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BillingService billingService;
    private final InvoiceGeneratorService invoiceGeneratorService;
    private final InvoiceRepository invoiceRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> generateInvoice(
            @RequestBody BillingDtos.GenerateInvoiceRequest request) {
        Invoice invoice = billingService.generateInvoice(request);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice generated"));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));

        byte[] pdfBytes = invoiceGeneratorService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice_" + invoice.getInvoiceNumber() + ".pdf")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(pdfBytes);
    }

    @GetMapping("/pending/{clientId}")
    public ResponseEntity<List<Invoice>> getPendingInvoices(@PathVariable String clientId) {
        return ResponseEntity.ok(billingService.getPendingInvoices(clientId));
    }
}
