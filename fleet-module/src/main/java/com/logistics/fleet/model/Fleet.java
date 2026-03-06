package com.logistics.fleet.model;

import com.logistics.platform.common.dto.TenantAware;
import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "fleets", indexes = {
        @Index(name = "idx_fleet_tenant", columnList = "tenant_id"),
        @Index(name = "idx_fleet_type", columnList = "type")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Fleet extends BaseEntity implements TenantAware {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FleetType type;

    private String ownerId; // ID of the vendor or company owning the fleet

    @Column(name = "tenant_id")
    private String tenantId;

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
