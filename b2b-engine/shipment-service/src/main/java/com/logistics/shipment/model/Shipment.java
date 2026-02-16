package com.logistics.shipment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipment_id", columnList = "shipmentId"),
        @Index(name = "idx_shipment_tenant_status", columnList = "tenant_id, status"),
        @Index(name = "idx_shipment_driver", columnList = "driverId"),
        @Index(name = "idx_shipment_vehicle", columnList = "vehicleId")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE shipments SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@EntityListeners(com.logistics.platform.utils.tenant.TenantListener.class)
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Shipment extends BaseEntity implements com.logistics.platform.common.dto.TenantAware {

    @Column(nullable = false, unique = true, updatable = false)
    private String shipmentId;

    @ElementCollection
    private List<String> orderIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String vehicleId;
    private String driverId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String startLocation;
    private String endLocation;

    @Builder.Default
    private Boolean deleted = false;

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
