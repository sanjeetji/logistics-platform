package com.logistics.payment.adapter.impl;

import com.logistics.payment.adapter.PaymentGateway;
import com.logistics.platform.common.dto.payment.PaymentDtos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class StripeAdapter implements PaymentGateway {

    @Override
    public Map<String, Object> initializePayment(PaymentDtos.PaymentRequest request) {
        log.info("Initializing Stripe payment intent for amount: {}", request.getAmount());
        Map<String, Object> response = new HashMap<>();
        response.put("gateway", "STRIPE");
        response.put("clientSecret", "pi_" + UUID.randomUUID().toString().substring(0, 12) + "_secret_"
                + UUID.randomUUID().toString().substring(0, 10));
        response.put("publishableKey", "pk_test_sample_key");
        response.put("amount", request.getAmount());
        return response;
    }

    @Override
    public boolean processPayment(PaymentDtos.PaymentRequest request) {
        log.info("Directly processing Stripe payment for amount: {}", request.getAmount());
        // Mock success
        return true;
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        log.info("Verifying Stripe payment for transaction: {}", transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(String transactionId, BigDecimal amount) {
        log.info("Refunding Stripe payment: {} amount: {}", transactionId, amount);
        return true;
    }

    @Override
    public List<PaymentDtos.GatewayTransactionDto> fetchRecentTransactions(LocalDateTime from, LocalDateTime to) {
        log.info("Fetching recent transactions from Stripe between {} and {}", from, to);
        // Mocking 2 transactions
        return List.of(
                PaymentDtos.GatewayTransactionDto.builder()
                        .gatewayReferenceId("ch_" + UUID.randomUUID().toString().substring(0, 8))
                        .amount(new BigDecimal("100.00"))
                        .currency("USD")
                        .status("succeeded")
                        .timestamp(from.plusHours(1))
                        .build(),
                PaymentDtos.GatewayTransactionDto.builder()
                        .gatewayReferenceId("ch_" + UUID.randomUUID().toString().substring(0, 8))
                        .amount(new BigDecimal("250.00"))
                        .currency("USD")
                        .status("succeeded")
                        .timestamp(from.plusHours(2))
                        .build());
    }

    @Override
    public boolean transferToAccount(String accountId, BigDecimal amount, String currency) {
        log.info("Mock Stripe: Transferring {} {} to account {}", amount, currency, accountId);
        return true;
    }

    @Override
    public PaymentDtos.GatewayType getGatewayType() {
        return PaymentDtos.GatewayType.STRIPE;
    }
}
