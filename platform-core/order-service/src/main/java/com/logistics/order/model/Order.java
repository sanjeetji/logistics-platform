package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
                @Index(name = "idx_order_order_id", columnList = "orderId"),
                @Index(name = "idx_order_tenant_status", columnList = "tenantId, status"),
                @Index(name = "idx_order_driver_status", columnList = "driverId, status"),
                @Index(name = "idx_order_customer", columnList = "customerId"),
                @Index(name = "idx_order_created_at", columnList = "createdAt") // Inherited from BaseEntity
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class Order extends BaseEntity {

        @Column(nullable = false, unique = true, updatable = false)
        private String orderId; // Public UUID

        @Column(nullable = false)
        private String customerId; // Linked to User Service

        private String tenantId; // B2B specific

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private OrderType type;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private OrderStatus status;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "address", column = @Column(name = "pickup_address")),
                        @AttributeOverride(name = "latitude", column = @Column(name = "pickup_lat")),
                        @AttributeOverride(name = "longitude", column = @Column(name = "pickup_lng")),
                        @AttributeOverride(name = "contactName", column = @Column(name = "pickup_contact_name")),
                        @AttributeOverride(name = "contactPhone", column = @Column(name = "pickup_contact_phone")),
                        @AttributeOverride(name = "instructions", column = @Column(name = "pickup_instructions"))
        })
        private OrderLocation pickupLocation;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "address", column = @Column(name = "drop_address")),
                        @AttributeOverride(name = "latitude", column = @Column(name = "drop_lat")),
                        @AttributeOverride(name = "longitude", column = @Column(name = "drop_lng")),
                        @AttributeOverride(name = "contactName", column = @Column(name = "drop_contact_name")),
                        @AttributeOverride(name = "contactPhone", column = @Column(name = "drop_contact_phone")),
                        @AttributeOverride(name = "instructions", column = @Column(name = "drop_instructions"))
        })
        private OrderLocation dropLocation;

        private Double weightKg;
        private BigDecimal price;

        @Column(columnDefinition = "text")
        private String metadata; // JSON for extra properties (SLA, VehicleType, etc.)

        // Assignment fields
        private String driverId; // Assigned driver
        private String vehicleId; // Assigned vehicle
        private LocalDateTime assignedAt; // When driver was assigned

        // Timing fields
        private LocalDateTime estimatedPickupTime;
        private LocalDateTime estimatedDeliveryTime;
        private LocalDateTime actualPickupTime;
        private LocalDateTime actualDeliveryTime;

        // Scheduling fields
        private LocalDateTime scheduledTime;
        private String timeSlot; // e.g., "09:00 - 12:00"

        // Delivery Preferences
        private String deliveryInstructions;
        private Boolean contactlessDelivery = false;
        private String safeDropLocation;
        private LocalDateTime preferredDeliveryTimeStart;
        private LocalDateTime preferredDeliveryTimeEnd;
        private String safeDropPhotoUrl; // Photo proof for safe drop deliveries

        // Cancellation
        @Column(columnDefinition = "text")
        private String cancellationReason;
        private LocalDateTime cancelledAt;

        @Builder.Default
        private Boolean deleted = false;

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private java.util.List<OrderStop> stops = new java.util.ArrayList<>();
}
