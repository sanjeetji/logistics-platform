package com.logistics.returns.dto;

import com.logistics.returns.model.ReturnReason;
import com.logistics.returns.model.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReturnDTOs {

    @Data
    @Builder
    public static class ReturnRequestDTO {
        private String orderId;
        private String customerId;
        private ReturnReason reason;
        private String description;
        private List<String> proofImages;
        private String pickupAddress;
        private Double pickupLatitude;
        private Double pickupLongitude;
    }

    @Data
    @Builder
    public static class ReturnResponseDTO {
        private String returnId;
        private String orderId;
        private ReturnStatus status;
        private ReturnReason reason;
        private BigDecimal refundAmount;
        private LocalDateTime requestedAt;
        private String message;
    }

    @Data
    public static class UpdateStatusRequest {
        private ReturnStatus status;
        private String adminNotes;
    }
}
