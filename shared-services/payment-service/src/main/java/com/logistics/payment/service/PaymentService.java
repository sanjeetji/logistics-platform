package com.logistics.payment.service;

import com.logistics.payment.dto.PaymentDtos;
import com.logistics.payment.entity.Transaction;
import com.logistics.payment.model.Wallet;
import com.logistics.payment.repository.TransactionRepository;
import com.logistics.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Wallet createWallet(Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
        }
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .walletType(Wallet.WalletType.CUSTOMER)
                .build();
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    @Transactional
    public Wallet topUp(PaymentDtos.TopUpRequest request) {
        Wallet wallet = getWallet(request.getUserId());

        // Mock Gateway interaction here (e.g. Stripe charge)
        log.info("Processing top-up of {} for user {}", request.getAmount(), request.getUserId());

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .type(Transaction.TransactionType.TOPUP)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description("Wallet Top-up")
                .build();
        transactionRepository.save(transaction);

        return wallet;
    }

    @Transactional
    public void processPayment(PaymentDtos.PaymentRequest request) {
        Wallet wallet = getWallet(request.getUserId());

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .type(Transaction.TransactionType.PAYMENT)
                .status(Transaction.TransactionStatus.SUCCESS)
                .referenceId(request.getOrderId())
                .description(request.getDescription())
                .build();
        transactionRepository.save(transaction);

        log.info("Processed payment of {} for order {}", request.getAmount(), request.getOrderId());
    }

    public List<Transaction> getHistory(Long userId) {
        Wallet wallet = getWallet(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }
}
