package com.logistics.payment.adapter;

import com.logistics.platform.common.dto.payment.PaymentDtos;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentGateway {

    /**
     * Initialize a payment (e.g., create a Stripe PaymentIntent or Razorpay Order)
     */
    Map<String, Object> initializePayment(PaymentDtos.PaymentRequest request);

    /**
     * Process a payment directly (e.g., for direct charges if supported)
     */
    boolean processPayment(PaymentDtos.PaymentRequest request);

    /**
     * Verify a payment status (e.g., after a redirect or webhook)
     */
    boolean verifyPayment(String transactionId);

    /**
     * Refund a payment
     */
    boolean refundPayment(String transactionId, BigDecimal amount);

    /**
     * Fetch recent transactions from the gateway for reconciliation
     */
    List<PaymentDtos.GatewayTransactionDto> fetchRecentTransactions(LocalDateTime from, LocalDateTime to);

    /**
     * Transfer funds to a specific account (e.g., driver payout)
     */
    boolean transferToAccount(String accountId, BigDecimal amount, String currency);

    /**
     * Get the gateway type this adapter handles
     */
    PaymentDtos.GatewayType getGatewayType();
}
