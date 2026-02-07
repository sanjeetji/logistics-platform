package com.logistics.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class PaymentDtos {

    @Data
    @Builder
    public static class TopUpRequest {
        private String userId;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class PaymentRequest {
        private String userId;
        private BigDecimal amount;
        private String orderId;
        private String description;
    }

    @Data
    @Builder
    public static class WalletResponse {
        private String id;
        private String userId;
        private BigDecimal balance;
        private String currency;
    }
}
