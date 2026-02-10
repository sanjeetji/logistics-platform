package com.logistics.payment.service;

import com.logistics.payment.model.Wallet;
import com.logistics.payment.model.WalletTransaction;
import com.logistics.payment.repository.WalletRepository;
import com.logistics.payment.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    @Transactional
    public Wallet createWallet(Long userId, Wallet.WalletType walletType) {
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .walletType(walletType)
                .build();
        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletTransaction creditWallet(Long walletId, BigDecimal amount,
            String referenceId, WalletTransaction.ReferenceType referenceType,
            String description) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.credit(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(walletId)
                .transactionType(WalletTransaction.TransactionType.CREDIT)
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
    public WalletTransaction debitWallet(Long walletId, BigDecimal amount,
            String referenceId, WalletTransaction.ReferenceType referenceType,
            String description) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.debit(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(walletId)
                .transactionType(WalletTransaction.TransactionType.DEBIT)
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

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }
}
