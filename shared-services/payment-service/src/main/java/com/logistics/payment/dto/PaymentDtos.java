package com.logistics.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class PaymentDtos {

    @Data
    @Builder
    public static class TopUpRequest {
        private Long userId;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class PaymentRequest {
        private Long userId;
        private BigDecimal amount;
        private String orderId;
        private String description;
    }

    @Data
    @Builder
    public static class WalletResponse {
        private Long id;
        private Long userId;
        private BigDecimal balance;
    }
}
