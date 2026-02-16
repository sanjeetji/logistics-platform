package com.logistics.order.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Persists state machine context for orders
 * Allows state machine restoration after restart
 */
@Entity
@Table(name = "order_state_machine_context")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateMachineContext extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String currentState;

    @Column(columnDefinition = "TEXT")
    private String machineContext; // Serialized state machine context

    private LocalDateTime lastTransitionTime;

    private String lastEvent;
}
