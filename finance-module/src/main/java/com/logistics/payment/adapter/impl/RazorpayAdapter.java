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
public class RazorpayAdapter implements PaymentGateway {

    @Override
    public Map<String, Object> initializePayment(PaymentDtos.PaymentRequest request) {
        log.info("Initializing Razorpay order for amount: {}", request.getAmount());
        Map<String, Object> response = new HashMap<>();
        response.put("gateway", "RAZORPAY");
        response.put("orderId", "order_" + UUID.randomUUID().toString().substring(0, 14));
        response.put("keyId", "rzp_test_sample_key");
        response.put("amount", request.getAmount().multiply(new BigDecimal(100))); // Amount in paise
        response.put("currency", "INR");
        return response;
    }

    @Override
    public boolean processPayment(PaymentDtos.PaymentRequest request) {
        log.info("Processing Razorpay payment for amount: {}", request.getAmount());
        return true;
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        log.info("Verifying Razorpay payment: {}", transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(String transactionId, BigDecimal amount) {
        log.info("Refunding Razorpay payment: {} amount: {}", transactionId, amount);
        return true;
    }

    @Override
    public List<PaymentDtos.GatewayTransactionDto> fetchRecentTransactions(LocalDateTime from, LocalDateTime to) {
        log.info("Fetching recent transactions from Razorpay between {} and {}", from, to);
        return List.of(
                PaymentDtos.GatewayTransactionDto.builder()
                        .gatewayReferenceId("pay_" + UUID.randomUUID().toString().substring(0, 10))
                        .amount(new BigDecimal("500.00"))
                        .currency("INR")
                        .status("captured")
                        .timestamp(from.plusMinutes(30))
                        .build());
    }

    @Override
    public boolean transferToAccount(String accountId, BigDecimal amount, String currency) {
        log.info("Mock Razorpay: Transferring {} {} to account {}", amount, currency, accountId);
        return true;
    }

    @Override
    public PaymentDtos.GatewayType getGatewayType() {
        return PaymentDtos.GatewayType.RAZORPAY;
    }
}
