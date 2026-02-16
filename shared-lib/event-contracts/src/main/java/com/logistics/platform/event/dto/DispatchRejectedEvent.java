package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a driver rejects an order assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchRejectedEvent {
    private String orderId;
    private String driverId;
    private String reason;
    private LocalDateTime rejectedAt;
}
