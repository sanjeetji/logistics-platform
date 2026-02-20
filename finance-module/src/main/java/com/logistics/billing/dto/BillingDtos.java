package com.logistics.billing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BillingDtos {

    @Data
    @Builder
    public static class GenerateInvoiceRequest {
        private String clientId;
        private LocalDate dueDate;
        private List<InvoiceItemDto> items;
    }

    @Data
    @Builder
    public static class InvoiceItemDto {
        private String description;
        private String referenceId;
        private BigDecimal amount;
        private Integer quantity;
    }

    @Data
    @Builder
    public static class LedgerResponse {
        private String clientId;
        private BigDecimal totalBalance;
        private List<LedgerEntryDto> entries;
    }
    
    @Data
    @Builder
    public static class LedgerEntryDto {
        private String id;
        private BigDecimal amount;
        private String type;
        private String description;
        private String createdAt;
    }
}
