package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistory extends BaseEntity {

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String changedBy; // User ID or "SYSTEM"

    @Column(columnDefinition = "text")
    private String reason; // For cancellations or manual changes

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "text")
    private String notes;
}
