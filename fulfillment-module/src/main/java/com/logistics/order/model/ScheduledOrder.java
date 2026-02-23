package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_orders")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE scheduled_orders SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class ScheduledOrder extends BaseEntity {

    @Column(nullable = false)
    private String customerId;

    private String tenantId;

    @Column(columnDefinition = "text", nullable = false)
    private String orderTemplateJson; // JSON representation of the Order to be created

    @Column(nullable = false)
    private String cronExpression; // e.g., "0 0 9 * * ?" for daily at 9 AM

    private LocalDateTime nextExecutionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduledOrderStatus status;

    @Builder.Default
    private Boolean deleted = false;

    public enum ScheduledOrderStatus {
        ACTIVE,
        PAUSED,
        COMPLETED,
        CANCELLED
    }
}
