package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
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

        // Cancellation
        @Column(columnDefinition = "text")
        private String cancellationReason;
        private LocalDateTime cancelledAt;

        @Builder.Default
        private Boolean deleted = false;
}
