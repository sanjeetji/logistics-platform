package com.logistics.audit.service;

import com.logistics.audit.model.AuditLog;
import com.logistics.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog createAuditLog(AuditLog auditLog) {
        log.info("Creating audit log: {} by user {}", auditLog.getAction(), auditLog.getUserId());
        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getLogsByUser(String userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    public List<AuditLog> getLogsByResource(String resource, String resourceId) {
        return auditLogRepository.findByResourceAndResourceIdOrderByTimestampDesc(resource, resourceId);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
