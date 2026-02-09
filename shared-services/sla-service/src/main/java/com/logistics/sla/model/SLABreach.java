package com.logistics.sla.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_breaches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLABreach extends BaseEntity {

    private String slaId;
    private String entityId; // e.g., Order ID
    private String entityType;
    private long actualDurationSeconds;
    private LocalDateTime breachTime;
    private String status; // DETECTED, NOTIFIED, RESOLVED
}
