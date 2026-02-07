package com.logistics.driver.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_shifts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverShift extends BaseEntity {

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private LocalDateTime shiftStart;

    private LocalDateTime shiftEnd;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer completedOrders = 0;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private Double totalDistanceKm = 0.0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ShiftStatus status = ShiftStatus.ACTIVE;

    @Column(columnDefinition = "text")
    private String notes;
}
