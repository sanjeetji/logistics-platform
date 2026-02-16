package com.logistics.wallet.service;

import com.logistics.platform.event.dto.WalletCreditedEvent;
import com.logistics.platform.event.dto.WalletDebitedEvent;
import com.logistics.wallet.model.TransactionStatus;
import com.logistics.wallet.model.TransactionType;
import com.logistics.wallet.model.Wallet;
import com.logistics.wallet.model.WalletTransaction;
import com.logistics.wallet.repository.WalletRepository;
import com.logistics.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final KafkaOperations<String, Object> kafkaTemplate;

    private static final String TOPIC_WALLET_EVENTS = "wallet-events";

    @Transactional
    public Wallet createWallet(String userId) {
        log.info("Creating wallet for user: {}", userId);
        Optional<Wallet> existing = walletRepository.findByUserId(userId);
        if (existing.isPresent()) {
            log.warn("Wallet already exists for user: {}", userId);
            return existing.get();
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency("USD")
                .active(true)
                .build();

        return walletRepository.save(wallet);
    }

    public Wallet getWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    @Transactional
    public Wallet topUp(String userId, BigDecimal amount, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive");
        }

        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getWalletId())
                .userId(userId)
                .amount(amount)
                .type(TransactionType.TOPUP)
                .status(TransactionStatus.COMPLETED)
                .referenceId(referenceId)
                .description("Wallet Top-up")
                .build();
        transactionRepository.save(transaction);

        WalletCreditedEvent event = WalletCreditedEvent.builder()
                .userId(userId)
                .walletId(wallet.getWalletId())
                .amount(amount)
                .transactionId(transaction.getTransactionId())
                .referenceId(referenceId)
                .type(TransactionType.TOPUP.name())
                .build();

        kafkaTemplate.send(TOPIC_WALLET_EVENTS, userId, event);
        log.info("Wallet topped up for user: {}. New balance: {}", userId, wallet.getBalance());

        return wallet;
    }

    @Transactional
    public Wallet deduct(String userId, BigDecimal amount, String referenceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }

        Wallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getWalletId())
                .userId(userId)
                .amount(amount.negate()) // store as negative for deduction
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .referenceId(referenceId)
                .description(description)
                .build();
        transactionRepository.save(transaction);

        WalletDebitedEvent event = WalletDebitedEvent.builder()
                .userId(userId)
                .walletId(wallet.getWalletId())
                .amount(amount)
                .transactionId(transaction.getTransactionId())
                .referenceId(referenceId)
                .type(TransactionType.PAYMENT.name())
                .build();
        
        kafkaTemplate.send(TOPIC_WALLET_EVENTS, userId, event);
        log.info("Wallet deducted for user: {}. New balance: {}", userId, wallet.getBalance());

        return wallet;
    }
}
