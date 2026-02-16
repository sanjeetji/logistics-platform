package com.logistics.platform.common.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class PaymentDtos {

    public enum GatewayType {
        STRIPE,
        RAZORPAY,
        PAYPAL,
        INTERNAL_WALLET
    }

    @Data
    @Builder
    public static class TopUpRequest {
        private Long userId;
        private BigDecimal amount;
        private GatewayType gatewayType;
        private Map<String, Object> gatewayMetadata;
    }

    @Data
    @Builder
    public static class PaymentRequest {
        private Long userId;
        private BigDecimal amount;
        private String orderId;
        private String description;
        private GatewayType gatewayType;
    }

    @Data
    @Builder
    public static class WalletResponse {
        private Long id;
        private Long userId;
        private BigDecimal balance;
    }

    @Data
    @Builder
    public static class GatewayTransactionDto {
        private String gatewayReferenceId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    public static class PayoutRequest {
        private String accountId;
        private BigDecimal amount;
        private String currency;
        private GatewayType gatewayType;
    }
}
