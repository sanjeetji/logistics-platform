package com.logistics.payout.dto;

import com.logistics.payout.model.PayoutRequest;
import com.logistics.payout.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PayoutDTOs {

    @Data
    @Builder
    public static class WalletDTO {
        private String driverId;
        private BigDecimal balance;
        private String currency;
        private List<TransactionDTO> recentTransactions;
    }

    @Data
    @Builder
    public static class TransactionDTO {
        private String id;
        private BigDecimal amount;
        private Transaction.TransactionType type; // CREDIT/DEBIT
        private String description;
        private String referenceId;
        private LocalDateTime createdAt;
    }

    @Data
    public static class EarningRequest {
        private String driverId;
        private BigDecimal amount;
        private String orderId; // Reference
        private String description;
    }

    @Data
    public static class WithdrawalRequest {
        private String driverId;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class PayoutResponse {
        private String payoutId;
        private String driverId;
        private BigDecimal amount;
        private PayoutRequest.PayoutStatus status;
        private LocalDateTime requestedAt;
    }
}
