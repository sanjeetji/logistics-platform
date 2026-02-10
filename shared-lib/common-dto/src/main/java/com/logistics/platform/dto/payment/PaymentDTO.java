package com.logistics.platform.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String id;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED
    private String paymentMethod; // CASH, CARD, WALLET, UPI
    private String transactionId;
    private String gatewayResponse;
    private LocalDateTime paidAt;
    private String tenantId;
    private LocalDateTime createdAt;
}
