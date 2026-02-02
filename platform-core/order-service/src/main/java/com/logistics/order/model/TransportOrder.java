package com.logistics.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportOrder { // Renamed to avoid reserved keyword 'Order' in SQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId; // Internal UUID

    private String customerId; // From User Service (B2C) or integration (B2B)
    private String tenantId;   // For B2B

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderType orderType = OrderType.B2C_ON_DEMAND;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    // Origin
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLng;

    // Destination
    private String dropAddress;
    private Double dropLat;
    private Double dropLng;

    private Double weightKg;
    private Double price;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


