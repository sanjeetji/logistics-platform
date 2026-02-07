package com.logistics.analytics.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "revenue_reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RevenueReport extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String reportId;

    @Column(nullable = false)
    private LocalDate period;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRevenue;

    @Column(precision = 19, scale = 2)
    private BigDecimal b2cRevenue;

    @Column(precision = 19, scale = 2)
    private BigDecimal b2bRevenue;

    @Column(nullable = false)
    private Integer orderCount;

    @Column(precision = 19, scale = 2)
    private BigDecimal averageOrderValue;

    private Integer b2cOrderCount;
    
    private Integer b2bOrderCount;
}
