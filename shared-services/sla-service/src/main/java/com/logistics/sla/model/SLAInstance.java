package com.logistics.sla.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_instances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAInstance extends BaseEntity {

    private String slaId;
    private String entityId; // e.g., Order ID
    private String entityType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isCompleted;
    private boolean isBreached;
}
