package com.logistics.shift.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_swap_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSwapRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "requesting_driver_id", nullable = false)
    private Long requestingDriverId;
    
    @Column(name = "target_driver_id", nullable = false)
    private Long targetDriverId;
    
    @ManyToOne
    @JoinColumn(name = "requesting_shift_id")
    private ShiftAssignment requestingShift;
    
    @ManyToOne
    @JoinColumn(name = "target_shift_id")
    private ShiftAssignment targetShift;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SwapStatus status = SwapStatus.PENDING;
    
    @Column
    private String reason;
    
    @Column(name = "approved_by")
    private Long approvedBy; // Manager ID
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejected_reason")
    private String rejectedReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum SwapStatus {
        PENDING,
        TARGET_ACCEPTED,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}
