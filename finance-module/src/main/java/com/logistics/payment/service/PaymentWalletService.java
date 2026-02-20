package com.logistics.payment.service;

import com.logistics.payment.model.PaymentWallet;
import com.logistics.payment.model.PaymentTransaction;
import com.logistics.payment.repository.PaymentWalletRepository;
import com.logistics.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWalletService {

        private final PaymentWalletRepository walletRepository;
        private final PaymentTransactionRepository transactionRepository;

        @Transactional
        public PaymentWallet createWallet(Long userId, PaymentWallet.WalletType walletType) {
                PaymentWallet wallet = PaymentWallet.builder()
                                .userId(userId)
                                .walletType(walletType)
                                .build();
                return walletRepository.save(wallet);
        }

        @Transactional
        public PaymentTransaction creditWallet(Long walletId, BigDecimal amount,
                        String referenceId, PaymentTransaction.ReferenceType referenceType,
                        String description) {
                PaymentWallet wallet = walletRepository.findById(walletId)
                                .orElseThrow(() -> new RuntimeException("Wallet not found"));

                BigDecimal balanceBefore = wallet.getBalance();
                wallet.credit(amount);
                walletRepository.save(wallet);

                PaymentTransaction transaction = PaymentTransaction.builder()
                                .walletId(walletId)
                                .transactionType(PaymentTransaction.TransactionType.CREDIT)
                                .amount(amount)
                                .balanceBefore(balanceBefore)
                                .balanceAfter(wallet.getBalance())
                                .referenceId(referenceId)
                                .referenceType(referenceType)
                                .description(description)
                                .transactionDate(LocalDateTime.now())
                                .build();

                return transactionRepository.save(transaction);
        }

        @Transactional
        public PaymentTransaction debitWallet(Long walletId, BigDecimal amount,
                        String referenceId, PaymentTransaction.ReferenceType referenceType,
                        String description) {
                PaymentWallet wallet = walletRepository.findById(walletId)
                                .orElseThrow(() -> new RuntimeException("Wallet not found"));

                BigDecimal balanceBefore = wallet.getBalance();
                wallet.debit(amount);
                walletRepository.save(wallet);

                PaymentTransaction transaction = PaymentTransaction.builder()
                                .walletId(walletId)
                                .transactionType(PaymentTransaction.TransactionType.DEBIT)
                                .amount(amount)
                                .balanceBefore(balanceBefore)
                                .balanceAfter(wallet.getBalance())
                                .referenceId(referenceId)
                                .referenceType(referenceType)
                                .description(description)
                                .transactionDate(LocalDateTime.now())
                                .build();

                return transactionRepository.save(transaction);
        }

        public PaymentWallet getWalletByUserId(Long userId) {
                return walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
        }
}
