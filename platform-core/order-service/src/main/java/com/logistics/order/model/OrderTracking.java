package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTracking extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderId;

    private String driverId;
    private String vehicleId;

    @Column(nullable = false)
    private Double currentLatitude;

    @Column(nullable = false)
    private Double currentLongitude;

    private LocalDateTime estimatedDeliveryTime;

    private Double distanceRemainingKm;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private OrderStatus currentStatus;

    @Column(columnDefinition = "text")
    private String notes;
}
