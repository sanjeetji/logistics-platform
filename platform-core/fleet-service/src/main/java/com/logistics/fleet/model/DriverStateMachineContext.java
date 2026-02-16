package com.logistics.fleet.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_state_machine_context")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverStateMachineContext extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String driverEmail; // Using email as the key to identifier

    @Column(nullable = false)
    private String currentState;

    private LocalDateTime lastTransitionTime;
    private String lastEvent;
}
