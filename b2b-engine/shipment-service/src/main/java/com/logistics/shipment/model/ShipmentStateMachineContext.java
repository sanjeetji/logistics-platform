package com.logistics.shipment.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_state_machine_context")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentStateMachineContext extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String shipmentId;

    @Column(nullable = false)
    private String currentState;

    private LocalDateTime lastTransitionTime;
    private String lastEvent;
}
