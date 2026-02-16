package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a billing invoice is generated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingGeneratedEvent {
    private String invoiceId;
    private String customerId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime generatedAt;
    private LocalDateTime dueDate;
}
