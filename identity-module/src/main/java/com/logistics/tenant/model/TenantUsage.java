package com.logistics.tenant.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tenant_usage")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUsage extends BaseEntity {

    @Column(nullable = false)
    private String tenantId;

    @Column(name = "usage_month", nullable = false)
    private LocalDate month; // First day of the month

    @Builder.Default
    private int ordersCreated = 0;

    @Builder.Default
    private int activeDrivers = 0;

    @Builder.Default
    private int activeVehicles = 0;

    @Builder.Default
    private long apiCallsCount = 0;

    public void incrementOrders() {
        this.ordersCreated++;
    }

    public void incrementApiCalls() {
        this.apiCallsCount++;
    }
}
