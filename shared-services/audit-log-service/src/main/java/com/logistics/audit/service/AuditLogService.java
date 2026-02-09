package com.logistics.audit.service;

import com.logistics.audit.model.AuditLog;
import com.logistics.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void recordAudit(String entityId, String entityType, String action, String changedBy, String tenantId, String oldValue, String newValue) {
        AuditLog log = AuditLog.builder()
                .entityId(entityId)
                .entityType(entityType)
                .action(action)
                .changedBy(changedBy)
                .tenantId(tenantId)
                .oldValue(oldValue)
                .newValue(newValue)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    public AuditLog createAuditLog(AuditLog auditLog) {
        if (auditLog.getTimestamp() == null) {
            auditLog.setTimestamp(LocalDateTime.now());
        }
        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> getLogsByUser(String userId) {
        return auditLogRepository.findByChangedBy(userId);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    public List<AuditLog> getLogsByResource(String resourceType, String resourceId) {
        return auditLogRepository.findByEntityIdAndEntityType(resourceId, resourceType);
    }
}
