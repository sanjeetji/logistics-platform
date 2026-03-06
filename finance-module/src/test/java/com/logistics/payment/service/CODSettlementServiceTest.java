package com.logistics.payment.service;

import com.logistics.payment.model.CODSettlement;
import com.logistics.payment.repository.CODSettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CODSettlementServiceTest {

    @Mock
    private CODSettlementRepository settlementRepository;

    @InjectMocks
    private CODSettlementService settlementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitiateSettlement() {
        // Arrange
        String orderId = "ORD123";
        String driverId = "DRV001";
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "USD";

        when(settlementRepository.save(any(CODSettlement.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CODSettlement result = settlementService.initiateSettlement(orderId, driverId, amount, currency);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(CODSettlement.SettlementStatus.PENDING_COLLECTION, result.getStatus());
        verify(settlementRepository, times(1)).save(any());
    }

    @Test
    void testMarkAsCollected() {
        // Arrange
        String orderId = "ORD123";
        String hubId = "HUB99";
        CODSettlement settlement = CODSettlement.builder()
                .orderId(orderId)
                .status(CODSettlement.SettlementStatus.PENDING_COLLECTION)
                .build();

        when(settlementRepository.findByOrderId(orderId)).thenReturn(Optional.of(settlement));
        when(settlementRepository.save(any(CODSettlement.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CODSettlement result = settlementService.markAsCollected(orderId, hubId);

        // Assert
        assertEquals(CODSettlement.SettlementStatus.COLLECTED, result.getStatus());
        assertEquals(hubId, result.getHubId());
        assertNotNull(result.getCollectedAt());
    }

    @Test
    void testReconcile() {
        // Arrange
        String orderId = "ORD123";
        String bankRef = "BANK-XYZ-001";
        CODSettlement settlement = CODSettlement.builder()
                .orderId(orderId)
                .status(CODSettlement.SettlementStatus.COLLECTED)
                .build();

        when(settlementRepository.findByOrderId(orderId)).thenReturn(Optional.of(settlement));
        when(settlementRepository.save(any(CODSettlement.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CODSettlement result = settlementService.reconcile(orderId, bankRef);

        // Assert
        assertEquals(CODSettlement.SettlementStatus.RECONCILED, result.getStatus());
        assertEquals(bankRef, result.getBankReference());
        assertNotNull(result.getReconciledAt());
    }
}
