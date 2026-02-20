package com.logistics.warehouse.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String warehouseCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer capacity; // Total storage slots

    @Builder.Default
    @Column(nullable = false)
    private Integer usedCapacity = 0;

    // Operating hours (JSON: {"monday": "9:00-18:00", ...})
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> operatingHours;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    private String contactPerson;

    private String contactPhone;

    @Column(columnDefinition = "text")
    private String notes;

    // Calculate available capacity
    public Integer getAvailableCapacity() {
        return capacity - usedCapacity;
    }
}
