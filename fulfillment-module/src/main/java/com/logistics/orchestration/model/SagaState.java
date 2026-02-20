package com.logistics.orchestration.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaState {

    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    private String currentStep;
    
    private String failureReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum SagaStatus {
        STARTED,
        INVENTORY_RESERVED,
        PAYMENT_PROCESSED,
        ORDER_CONFIRMED,
        DISPATCH_REQUESTED,
        DISPATCH_COMMAND_SENT,
        DRIVER_ASSIGNED,
        PICKED_UP,
        DELIVERED,
        FAILED,
        COMPENSATED
    }
}
