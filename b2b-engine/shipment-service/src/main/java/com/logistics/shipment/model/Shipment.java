package com.logistics.shipment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE shipments SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class Shipment extends BaseEntity {

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
}
