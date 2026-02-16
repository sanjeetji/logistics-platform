package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when inventory is updated
 * Actions: RESERVED, RELEASED, ADJUSTED, RESTOCKED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdatedEvent {
    private String skuId;
    private String warehouseId;
    private String action;
    private Integer previousQuantity;
    private Integer newQuantity;
    private Integer delta;
    private String orderId; // if related to order
    private LocalDateTime timestamp;
    private String updatedBy;
}
