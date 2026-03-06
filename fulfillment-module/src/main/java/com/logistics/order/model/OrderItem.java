package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String name;

    private String sku;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Builder.Default
    private Integer fulfilledQuantity = 0;

    private Double weight; // kg

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemFulfillmentStatus status = ItemFulfillmentStatus.PENDING;

    public enum ItemFulfillmentStatus {
        PENDING,
        FULFILLED,
        UNAVAILABLE
    }
}
