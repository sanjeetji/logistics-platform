package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items", uniqueConstraints = @UniqueConstraint(columnNames = { "warehouse_id", "sku" }))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends BaseEntity {

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private String sku; // Stock Keeping Unit

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Builder.Default
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    private String unitOfMeasure; // kg, liters, pieces, etc.

    @Column(nullable = false)
    private Integer reorderLevel; // Trigger for low stock alert

    private Integer reorderQuantity;

    private LocalDateTime lastRestocked;

    @Column(columnDefinition = "text")
    private String notes;

    // Calculate available quantity
    public Integer getAvailableQuantity() {
        int qty = quantity != null ? quantity : 0;
        int reserved = reservedQuantity != null ? reservedQuantity : 0;
        return qty - reserved;
    }

    // Check if low stock
    public Boolean isLowStock() {
        int qty = quantity != null ? quantity : 0;
        int level = reorderLevel != null ? reorderLevel : 0;
        return qty <= level;
    }
}
