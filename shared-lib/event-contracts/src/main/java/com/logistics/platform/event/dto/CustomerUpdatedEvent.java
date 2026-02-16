package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published when customer information is updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdatedEvent {
    private String customerId;
    private Map<String, Object> changes;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
