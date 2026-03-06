package com.logistics.billing.service;

import com.logistics.billing.entity.Invoice;
import com.logistics.billing.entity.LedgerEntry;
import com.logistics.billing.repository.InvoiceRepository;
import com.logistics.billing.repository.LedgerEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillingCycleTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunMonthlyBillingCycle_WithNegativeBalance_GeneratesInvoice() {
        // Arrange
        String clientId1 = "CLIENT-001";

        LedgerEntry entry1 = LedgerEntry.builder().clientId(clientId1).build();

        when(ledgerEntryRepository.findAll()).thenReturn(Arrays.asList(entry1));

        // Client owes $450 (negative balance)
        when(ledgerEntryRepository.calculateTotalBalance(clientId1))
                .thenReturn(new BigDecimal("-450.00"));

        Invoice savedInvoice = Invoice.builder().invoiceNumber("INV-123").id("inv-id").build();
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        // Act
        billingService.runMonthlyBillingCycle();

        // Assert
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());

        Invoice generatedInvoice = invoiceCaptor.getValue();
        assertEquals("CLIENT-001", generatedInvoice.getClientId());

        // Ensure total amount matches the absolute value of the debt
        assertEquals(0, new BigDecimal("450.00").compareTo(generatedInvoice.getTotalAmount()));
        assertEquals(1, generatedInvoice.getItems().size());
        assertEquals("Monthly Logistics & Platform Services", generatedInvoice.getItems().get(0).getDescription());

        // Verify ledger was debited for the new invoice
        verify(ledgerEntryRepository).save(any(LedgerEntry.class));
    }

    @Test
    void testRunMonthlyBillingCycle_WithPositiveBalance_DoesNotGenerateInvoice() {
        // Arrange
        String clientId2 = "CLIENT-002";

        LedgerEntry entry2 = LedgerEntry.builder().clientId(clientId2).build();

        when(ledgerEntryRepository.findAll()).thenReturn(Arrays.asList(entry2));

        // Client is in the green, $100 balance (positive) -> no billing needed
        when(ledgerEntryRepository.calculateTotalBalance(clientId2))
                .thenReturn(new BigDecimal("100.00"));

        // Act
        billingService.runMonthlyBillingCycle();

        // Assert
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }
}
