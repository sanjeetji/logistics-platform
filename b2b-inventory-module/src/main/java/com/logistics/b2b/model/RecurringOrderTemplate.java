package com.logistics.b2b.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "recurring_order_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RecurringOrderTemplate extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String templateId;

    @Column(nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurringFrequency frequency;

    private Integer dayOfWeek; // 1-7 for weekly
    
    private Integer dayOfMonth; // 1-31 for monthly

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    // Template for order creation (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> orderTemplate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(columnDefinition = "text")
    private String description;
}
