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
public class PayPalAdapter implements PaymentGateway {

    @Override
    public Map<String, Object> initializePayment(PaymentDtos.PaymentRequest request) {
        log.info("Initializing PayPal order for amount: {}", request.getAmount());
        Map<String, Object> response = new HashMap<>();
        response.put("gateway", "PAYPAL");
        response.put("orderId", UUID.randomUUID().toString().toUpperCase());
        response.put("approvalLink",
                "https://www.sandbox.paypal.com/checkoutnow?token=" + UUID.randomUUID().toString().substring(0, 10));
        response.put("amount", request.getAmount());
        response.put("currency", "USD");
        return response;
    }

    @Override
    public boolean processPayment(PaymentDtos.PaymentRequest request) {
        log.info("Processing PayPal capture for amount: {}", request.getAmount());
        return true;
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        log.info("Verifying PayPal order status: {}", transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(String transactionId, BigDecimal amount) {
        log.info("Refunding PayPal payment: {} amount: {}", transactionId, amount);
        return true;
    }

    @Override
    public List<PaymentDtos.GatewayTransactionDto> fetchRecentTransactions(LocalDateTime from, LocalDateTime to) {
        log.info("Fetching recent transactions from PayPal between {} and {}", from, to);
        return List.of(
                PaymentDtos.GatewayTransactionDto.builder()
                        .gatewayReferenceId(UUID.randomUUID().toString().toUpperCase().substring(0, 12))
                        .amount(new BigDecimal("75.00"))
                        .currency("USD")
                        .status("COMPLETED")
                        .timestamp(from.plusHours(1))
                        .build());
    }

    @Override
    public boolean transferToAccount(String accountId, BigDecimal amount, String currency) {
        log.info("Mock PayPal: Transferring {} {} to account {}", amount, currency, accountId);
        return true;
    }

    @Override
    public PaymentDtos.GatewayType getGatewayType() {
        return PaymentDtos.GatewayType.PAYPAL;
    }
}
