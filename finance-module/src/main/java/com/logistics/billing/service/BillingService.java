package com.logistics.billing.service;

import com.logistics.billing.dto.BillingDtos;
import com.logistics.billing.entity.Invoice;
import com.logistics.billing.entity.InvoiceItem;
import com.logistics.billing.entity.LedgerEntry;
import com.logistics.billing.repository.InvoiceRepository;
import com.logistics.billing.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

        private final InvoiceRepository invoiceRepository;
        private final LedgerEntryRepository ledgerEntryRepository;

        @Transactional
        public Invoice generateInvoice(BillingDtos.GenerateInvoiceRequest request) {
                Invoice invoice = Invoice.builder()
                                .clientId(request.getClientId())
                                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                                .dueDate(request.getDueDate() != null ? request.getDueDate()
                                                : LocalDate.now().plusDays(30))
                                .status(Invoice.InvoiceStatus.ISSUED)
                                .build();

                List<InvoiceItem> items = request.getItems().stream().map(dto -> InvoiceItem.builder()
                                .invoice(invoice)
                                .description(dto.getDescription())
                                .referenceId(dto.getReferenceId())
                                .amount(dto.getAmount())
                                .quantity(dto.getQuantity())
                                .build()).collect(Collectors.toList());

                invoice.setItems(items);

                BigDecimal totalAmount = items.stream()
                                .map(item -> {
                                        BigDecimal amt = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                                        int qty = item.getQuantity() != null ? item.getQuantity() : 0;
                                        return amt.multiply(BigDecimal.valueOf(qty));
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                invoice.setTotalAmount(totalAmount);

                Invoice savedInvoice = invoiceRepository.save(invoice);

                // Debit the ledger
                LedgerEntry entry = LedgerEntry.builder()
                                .clientId(request.getClientId())
                                .amount(totalAmount.negate()) // Debit is negative
                                .type(LedgerEntry.EntryType.DEBIT)
                                .description("Invoice Generated: " + savedInvoice.getInvoiceNumber())
                                .referenceId(savedInvoice.getId())
                                .build();
                ledgerEntryRepository.save(entry);

                return savedInvoice;
        }

        public List<Invoice> getPendingInvoices(String clientId) {
                return invoiceRepository.findByClientId(clientId).stream()
                                .filter(inv -> inv.getStatus() == Invoice.InvoiceStatus.ISSUED
                                                || inv.getStatus() == Invoice.InvoiceStatus.OVERDUE)
                                .collect(Collectors.toList());
        }

        public BillingDtos.LedgerResponse getClientLedger(String clientId) {
                List<LedgerEntry> entries = ledgerEntryRepository.findByClientIdOrderByCreatedAtDesc(clientId);
                BigDecimal balance = ledgerEntryRepository.calculateTotalBalance(clientId);

                List<BillingDtos.LedgerEntryDto> entryDtos = entries.stream()
                                .map(e -> BillingDtos.LedgerEntryDto.builder()
                                                .id(e.getId())
                                                .amount(e.getAmount())
                                                .type(e.getType().name())
                                                .description(e.getDescription())
                                                .createdAt(e.getCreatedAt().toString())
                                                .build())
                                .collect(Collectors.toList());

                return BillingDtos.LedgerResponse.builder()
                                .clientId(clientId)
                                .totalBalance(balance)
                                .entries(entryDtos)
                                .build();
        }
}
