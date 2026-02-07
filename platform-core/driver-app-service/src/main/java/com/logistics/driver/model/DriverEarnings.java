package com.logistics.driver.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_earnings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverEarnings extends BaseEntity {

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private String orderId;

    // Earnings breakdown
    @Column(precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceFare;

    @Column(precision = 10, scale = 2)
    private BigDecimal timeFare;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal incentives = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal tips = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netEarnings;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDateTime paidAt;

    @Column(columnDefinition = "text")
    private String notes;
}
