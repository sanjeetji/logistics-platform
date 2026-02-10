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
public class InventoryItemDTO {
    private String id;
    private String warehouseId;
    private String sku;
    private String productName;
    private String category;
    private Integer quantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String unit; // PCS, KG, LITER
    private String location; // Rack/Bin location
    private LocalDateTime lastRestocked;
    private String tenantId;
}
