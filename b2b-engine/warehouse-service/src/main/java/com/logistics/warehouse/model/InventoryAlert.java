package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlert extends BaseEntity {

    @Column(nullable = false)
    private Long inventoryItemId;

    @Column(nullable = false)
    private String productSku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Column(nullable = false)
    private Integer currentQuantity;

    private Integer thresholdQuantity;

    @Column(nullable = false)
    @Builder.Default
    private Boolean acknowledged = false;

    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;

    public enum AlertType {
        LOW_STOCK,
        OUT_OF_STOCK,
        EXPIRING_SOON,
        EXPIRED
    }
}
