package com.logistics.dispatch.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_assignments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DispatchAssignment extends BaseEntity {

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "text")
    private String rejectionReason;

    // Assignment score (for optimization)
    private Double assignmentScore;

    // Distance from driver to pickup location (in km)
    private Double distanceToPickup;

    // Estimated time to reach pickup (in minutes)
    private Integer estimatedTimeToPickup;

    @Column(columnDefinition = "text")
    private String notes;
}
