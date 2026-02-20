package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarehouseOrder extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private Long warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String customerId;

    private LocalDateTime pickingStartedAt;
    private LocalDateTime packingCompletedAt;
    private LocalDateTime shippedAt;

    public enum OrderStatus {
        PENDING,
        PICKING,
        PACKED,
        SHIPPED,
        CANCELLED
    }
}
