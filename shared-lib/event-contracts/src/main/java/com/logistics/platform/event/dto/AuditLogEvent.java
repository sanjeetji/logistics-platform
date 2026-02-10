package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEvent implements Serializable {
    private String entityId;
    private String entityType;
    private String action;
    private String changedBy;
    private String tenantId;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
}
