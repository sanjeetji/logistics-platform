package com.logistics.payout.service;

import com.logistics.payout.dto.PayoutDTOs;
import com.logistics.payout.model.PayoutRequest;
import com.logistics.payout.model.PayoutTransaction;
import com.logistics.payout.model.PayoutWallet;
import com.logistics.payout.repository.PayoutWalletRepository;
import com.logistics.payout.repository.PayoutTransactionRepository;
import com.logistics.payout.repository.PayoutRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

        private final PayoutWalletRepository walletRepository;
        private final PayoutTransactionRepository transactionRepository;
        private final PayoutRequestRepository payoutRequestRepository;

        @Transactional
        public PayoutWallet getOrCreateWallet(String driverId) {
                return walletRepository.findByDriverId(driverId)
                                .orElseGet(() -> walletRepository
                                                .save(java.util.Objects.requireNonNull(PayoutWallet.builder()
                                                                .driverId(driverId)
                                                                .balance(BigDecimal.ZERO)
                                                                .currency("USD")
                                                                .build())));
        }

        @Transactional
        public void addEarning(PayoutDTOs.EarningRequest request) {
                PayoutWallet wallet = getOrCreateWallet(request.getDriverId());

                log.info("Adding earning of {} to driver {}", request.getAmount(), request.getDriverId());

                // Update Balance
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                walletRepository.save(wallet);

                // Record Transaction
                PayoutTransaction transaction = PayoutTransaction.builder()
                                .walletId(wallet.getDriverId()) // Use driverId as wallet identifier for simplicity
                                .amount(request.getAmount())
                                .type(PayoutTransaction.TransactionType.CREDIT)
                                .description(request.getDescription())
                                .referenceId(request.getOrderId())
                                .build();
                transactionRepository.save(Objects.requireNonNull(transaction, "Transaction must not be null"));
        }

        @Transactional
        public PayoutRequest requestWithdrawal(PayoutDTOs.WithdrawalRequest request) {
                PayoutWallet wallet = getOrCreateWallet(request.getDriverId());

                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                        throw new RuntimeException("Insufficient funds. Current balance: " + wallet.getBalance());
                }

                log.info("Processing withdrawal request of {} for driver {}", request.getAmount(),
                                request.getDriverId());

                // Deduct Balance Immediately (Hold funds)
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                walletRepository.save(wallet);

                // Record Debit Transaction
                PayoutTransaction transaction = PayoutTransaction.builder()
                                .walletId(wallet.getDriverId())
                                .amount(request.getAmount())
                                .type(PayoutTransaction.TransactionType.DEBIT)
                                .description("Payout Request")
                                .build();
                transactionRepository.save(Objects.requireNonNull(transaction, "Transaction must not be null"));

                // Create Payout Request
                PayoutRequest payout = PayoutRequest.builder()
                                .payoutId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                                .driverId(request.getDriverId())
                                .amount(request.getAmount())
                                .status(PayoutRequest.PayoutStatus.PENDING)
                                .build();

                // Auto-Approve small amounts (Mock Banking integration)
                if (request.getAmount().compareTo(new BigDecimal("500")) < 0) {
                        payout.setStatus(PayoutRequest.PayoutStatus.APPROVED);
                }

                return payoutRequestRepository.save(Objects.requireNonNull(payout, "Payout must not be null"));
        }

        public PayoutDTOs.WalletDTO getWalletDetails(String driverId) {
                PayoutWallet wallet = getOrCreateWallet(driverId);
                List<PayoutTransaction> transactions = transactionRepository
                                .findByWalletIdOrderByCreatedAtDesc(driverId);

                List<PayoutDTOs.TransactionDTO> transactionDTOs = transactions.stream()
                                .map(t -> PayoutDTOs.TransactionDTO.builder()
                                                .id(String.valueOf(t.getId()))
                                                .amount(t.getAmount())
                                                .type(t.getType())
                                                .description(t.getDescription())
                                                .referenceId(t.getReferenceId())
                                                .createdAt(t.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                return PayoutDTOs.WalletDTO.builder()
                                .driverId(wallet.getDriverId())
                                .balance(wallet.getBalance())
                                .currency(wallet.getCurrency())
                                .recentTransactions(transactionDTOs)
                                .build();
        }
}
