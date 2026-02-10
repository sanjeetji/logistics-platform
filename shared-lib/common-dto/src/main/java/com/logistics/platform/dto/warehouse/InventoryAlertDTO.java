package com.logistics.platform.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertDTO {
    private String id;
    private String warehouseId;
    private String inventoryItemId;
    private String sku;
    private String productName;
    private String alertType; // LOW_STOCK, OUT_OF_STOCK, OVERSTOCK, EXPIRY_SOON
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private Integer currentQuantity;
    private Integer thresholdQuantity;
    private String message;
    private Boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String tenantId;
}
