package com.logistics.b2b.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sla_configs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SLAConfig extends BaseEntity {

    private Long clientId; // Client-specific rule if not null, otherwise global

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private Integer targetDurationMinutes;

    @Column(nullable = false)
    private Integer atRiskThresholdMinutes;
}
