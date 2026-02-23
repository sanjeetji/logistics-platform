package com.logistics.audit.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    private String userId;
    @Column(columnDefinition = "TEXT")
    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String entityType;

    private String action;
    private String changedBy;
    private String tenantId;
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    private LocalDateTime timestamp;
}
