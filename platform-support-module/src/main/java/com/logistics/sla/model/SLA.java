package com.logistics.sla.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "slas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLA extends BaseEntity {

    private String name; // e.g., "Order Delivery"
    private String description;
    private String entityType; // e.g., "ORDER", "PARCEL"
    private String startEvent; // e.g., "ORDER_CREATED"
    private String endEvent; // e.g., "ORDER_DELIVERED"
    private long maxDurationSeconds; // Time allowed to complete process
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private boolean isActive;
}
