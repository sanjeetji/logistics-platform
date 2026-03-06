package com.logistics.payment.service;

import com.logistics.payment.adapter.GatewayFactory;
import com.logistics.payment.adapter.PaymentGateway;
import com.logistics.payment.entity.Discrepancy;
import com.logistics.payment.entity.ReconciliationRecord;
import com.logistics.payment.entity.Transaction;
import com.logistics.payment.repository.DiscrepancyRepository;
import com.logistics.payment.repository.ReconciliationRecordRepository;
import com.logistics.payment.repository.TransactionRepository;
import com.logistics.platform.common.dto.payment.PaymentDtos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final ReconciliationRecordRepository recordRepository;
    private final DiscrepancyRepository discrepancyRepository;
    private final TransactionRepository transactionRepository;
    private final GatewayFactory gatewayFactory;

    @Transactional
    public ReconciliationRecord reconcile(PaymentDtos.GatewayType gatewayType, LocalDateTime from, LocalDateTime to) {
        log.info("Starting reconciliation for {} from {} to {}", gatewayType, from, to);

        ReconciliationRecord record = ReconciliationRecord.builder()
                .gatewayType(gatewayType)
                .rangeFrom(from)
                .rangeTo(to)
                .status(ReconciliationRecord.ReconciliationStatus.PENDING)
                .build();
        record = recordRepository.save(record);

        try {
            PaymentGateway gateway = gatewayFactory.getGateway(gatewayType);
            List<PaymentDtos.GatewayTransactionDto> gatewayTxns = gateway.fetchRecentTransactions(from, to);

            List<Transaction> localTxns = transactionRepository.findByCreatedAtBetween(from, to);
            Map<String, Transaction> localTxnMap = localTxns.stream()
                    .filter(t -> t.getReferenceId() != null)
                    .collect(Collectors.toMap(Transaction::getReferenceId, Function.identity(), (a, b) -> a));

            int processed = 0;
            int discrepancies = 0;

            for (PaymentDtos.GatewayTransactionDto gTxn : gatewayTxns) {
                processed++;
                Transaction lTxn = localTxnMap.get(gTxn.getGatewayReferenceId());

                if (lTxn == null) {
                    createDiscrepancy(record.getId(), null, gTxn.getGatewayReferenceId(),
                            Discrepancy.DiscrepancyType.MISSING_LOCAL,
                            "Transaction found in gateway but missing locally");
                    discrepancies++;
                } else {
                    if (lTxn.getAmount().compareTo(gTxn.getAmount()) != 0) {
                        createDiscrepancy(record.getId(), lTxn.getId(), gTxn.getGatewayReferenceId(),
                                Discrepancy.DiscrepancyType.AMOUNT_MISMATCH,
                                String.format("Amount mismatch: Local=%s, Gateway=%s", lTxn.getAmount(),
                                        gTxn.getAmount()));
                        discrepancies++;
                    }
                }
            }

            // Check for local transactions missing in gateway report
            Map<String, PaymentDtos.GatewayTransactionDto> gatewayTxnMap = gatewayTxns.stream()
                    .collect(Collectors.toMap(PaymentDtos.GatewayTransactionDto::getGatewayReferenceId,
                            Function.identity(), (a, b) -> a));

            for (Transaction lTxn : localTxns) {
                if (lTxn.getReferenceId() != null && !gatewayTxnMap.containsKey(lTxn.getReferenceId())) {
                    createDiscrepancy(record.getId(), lTxn.getId(), null,
                            Discrepancy.DiscrepancyType.MISSING_GATEWAY,
                            "Local transaction missing in gateway report");
                    discrepancies++;
                }
            }

            record.setTotalProcessed(processed);
            record.setTotalDiscrepancies(discrepancies);
            record.setStatus(ReconciliationRecord.ReconciliationStatus.COMPLETED);
            record.setCompletedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Reconciliation failed for {}", gatewayType, e);
            record.setStatus(ReconciliationRecord.ReconciliationStatus.FAILED);
        }

        return recordRepository.save(record);
    }

    private void createDiscrepancy(Long recId, Long txnId, String gatewayRef, Discrepancy.DiscrepancyType type,
            String details) {
        Discrepancy discrepancy = Discrepancy.builder()
                .reconciliationId(recId)
                .transactionId(txnId)
                .gatewayReferenceId(gatewayRef)
                .type(type)
                .details(details)
                .build();
        discrepancyRepository.save(discrepancy);
    }
}
