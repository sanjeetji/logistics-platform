package com.logistics.b2b.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_escalations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SLAEscalation extends BaseEntity {

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscalationLevel level;

    @Column(nullable = false)
    private LocalDateTime escalatedAt;

    private LocalDateTime resolvedAt;

    public enum EscalationLevel {
        WARNING,
        CRITICAL
    }
}
