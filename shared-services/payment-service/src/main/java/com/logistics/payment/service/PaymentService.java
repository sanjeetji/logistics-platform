package com.logistics.payment.service;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.payment.adapter.GatewayFactory;
import com.logistics.payment.adapter.PaymentGateway;
import com.logistics.payment.entity.Transaction;
import com.logistics.payment.model.Wallet;
import com.logistics.payment.repository.TransactionRepository;
import com.logistics.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final GatewayFactory gatewayFactory;

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
    public Map<String, Object> initiateTopUp(PaymentDtos.TopUpRequest request) {
        log.info("Initiating top-up of {} for user {} via {}", request.getAmount(), request.getUserId(),
                request.getGatewayType());

        PaymentGateway gateway = gatewayFactory.getGateway(request.getGatewayType());

        // Prepare request for gateway initialization
        PaymentDtos.PaymentRequest gatewayReq = PaymentDtos.PaymentRequest.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .description("Wallet Top-up")
                .gatewayType(request.getGatewayType())
                .build();

        return gateway.initializePayment(gatewayReq);
    }

    @Transactional
    public Wallet topUp(PaymentDtos.TopUpRequest request) {
        Wallet wallet = getWallet(request.getUserId());

        log.info("Processing top-up of {} for user {} via {}", request.getAmount(), request.getUserId(),
                request.getGatewayType());

        if (request.getGatewayType() != PaymentDtos.GatewayType.INTERNAL_WALLET) {
            PaymentGateway gateway = gatewayFactory.getGateway(request.getGatewayType());

            PaymentDtos.PaymentRequest gatewayReq = PaymentDtos.PaymentRequest.builder()
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .description("Wallet Top-up")
                    .gatewayType(request.getGatewayType())
                    .build();

            boolean success = gateway.processPayment(gatewayReq);
            if (!success) {
                throw new RuntimeException("Payment processing failed at gateway: " + request.getGatewayType());
            }
        }

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .type(Transaction.TransactionType.TOPUP)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description("Wallet Top-up via " + request.getGatewayType())
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
        transactionRepository.save(java.util.Objects.requireNonNull(transaction));

        log.info("Processed payment of {} for order {}", request.getAmount(), request.getOrderId());
    }

    public List<Transaction> getHistory(Long userId) {
        Wallet wallet = getWallet(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }

    @Transactional
    public boolean processPayout(PaymentDtos.PayoutRequest request) {
        log.info("Processing payout of {} {} to account {}", request.getAmount(), request.getCurrency(),
                request.getAccountId());

        PaymentGateway gateway = gatewayFactory.getGateway(request.getGatewayType());
        return gateway.transferToAccount(request.getAccountId(), request.getAmount(), request.getCurrency());
    }
}
