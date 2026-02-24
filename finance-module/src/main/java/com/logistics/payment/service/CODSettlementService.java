package com.logistics.payment.service;

import com.logistics.payment.model.CODSettlement;
import com.logistics.payment.repository.CODSettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CODSettlementService {

    private final CODSettlementRepository settlementRepository;

    /**
     * Initiate a new settlement record (usually called after order delivery)
     */
    @Transactional
    public CODSettlement initiateSettlement(String orderId, String driverId, BigDecimal amount, String currency) {
        log.info("Initiating COD settlement for order: {}, driver: {}, amount: {}", orderId, driverId, amount);

        CODSettlement settlement = CODSettlement.builder()
                .orderId(orderId)
                .driverId(driverId)
                .amount(amount)
                .currency(currency)
                .status(CODSettlement.SettlementStatus.PENDING_COLLECTION)
                .build();

        return settlementRepository.save(settlement);
    }

    /**
     * Mark cash as collected from driver at a hub
     */
    @Transactional
    public CODSettlement markAsCollected(String orderId, String hubId) {
        log.info("Marking COD as collected for order: {} at hub: {}", orderId, hubId);

        CODSettlement settlement = settlementRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Settlement record not found for order: " + orderId));

        if (settlement.getStatus() != CODSettlement.SettlementStatus.PENDING_COLLECTION) {
            throw new RuntimeException("Invalid settlement status for collection: " + settlement.getStatus());
        }

        settlement.setStatus(CODSettlement.SettlementStatus.COLLECTED);
        settlement.setHubId(hubId);
        settlement.setCollectedAt(LocalDateTime.now());

        return settlementRepository.save(settlement);
    }

    /**
     * Perform final reconciliation with bank deposit
     */
    @Transactional
    public CODSettlement reconcile(String orderId, String bankReference) {
        log.info("Reconciling COD settlement for order: {} with bank ref: {}", orderId, bankReference);

        CODSettlement settlement = settlementRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Settlement record not found for order: " + orderId));

        settlement.setStatus(CODSettlement.SettlementStatus.RECONCILED);
        settlement.setBankReference(bankReference);
        settlement.setReconciledAt(LocalDateTime.now());

        return settlementRepository.save(settlement);
    }

    /**
     * Get pending collections for a driver
     */
    public List<CODSettlement> getPendingCollections(String driverId) {
        return settlementRepository.findByDriverIdAndStatus(driverId,
                CODSettlement.SettlementStatus.PENDING_COLLECTION);
    }
}
