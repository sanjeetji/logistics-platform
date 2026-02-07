package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryTransaction extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Long itemId; // Reference to InventoryItem

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Integer quantity;

    private Integer quantityBefore;

    private Integer quantityAfter;

    private String orderId; // If related to order

    @Column(columnDefinition = "text")
    private String reason;

    private String performedBy; // User/system who performed transaction

    @Column(columnDefinition = "text")
    private String notes;
}
