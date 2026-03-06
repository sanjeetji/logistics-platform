package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bin_inventory", uniqueConstraints = @UniqueConstraint(columnNames = { "bin_id", "inventory_item_id" }))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BinInventory extends BaseEntity {

    @Column(name = "bin_id", nullable = false)
    private Long binId;

    @Column(name = "inventory_item_id", nullable = false)
    private Long inventoryItemId;

    @Column(nullable = false)
    private Integer quantity;

    @Builder.Default
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}
