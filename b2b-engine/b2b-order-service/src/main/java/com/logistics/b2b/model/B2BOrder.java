package com.logistics.b2b.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "b2b_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class B2BOrder extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(unique = true)
    private String erpOrderId;

    @Column(nullable = false)
    private Long clientId; // Enterprise client

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OrderType orderType = OrderType.SINGLE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false)
    private LocalDateTime slaDeadline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private SLAStatus slaStatus = SLAStatus.ON_TIME;

    private LocalDateTime scheduledPickupTime;

    private LocalDateTime scheduledDeliveryTime;

    private LocalDateTime slaPausedAt;

    private Long slaRemainingMinutes; // Minutes remaining when paused

    // Custom metadata for client-specific fields
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private B2BOrderStatus status = B2BOrderStatus.SCHEDULED;

    private String assignedRouteId;

    @Column(columnDefinition = "text")
    private String notes;
}
