package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_bins")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseBin extends BaseEntity {

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false, unique = true)
    private String binCode; // e.g., "A-01-05" (Zone-Aisle-Shelf)

    @Column(nullable = false)
    private String zone; // e.g., "A", "B", "C"

    @Column(nullable = false)
    private String aisle;

    @Column(nullable = false)
    private String shelf;

    @Enumerated(EnumType.STRING)
    private BinType binType;

    private Integer capacity; // Max items

    @Builder.Default
    private Integer currentOccupancy = 0;

    @Builder.Default
    private Boolean active = true;

    public enum BinType {
        STANDARD,
        COLD_STORAGE,
        HAZARDOUS,
        FRAGILE,
        BULK
    }

    public boolean hasCapacity() {
        return capacity == null || currentOccupancy < capacity;
    }
}
