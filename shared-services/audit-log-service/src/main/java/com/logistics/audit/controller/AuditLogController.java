package com.logistics.audit.controller;

import com.logistics.audit.model.AuditLog;
import com.logistics.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/log")
    public ResponseEntity<AuditLog> createLog(@RequestBody AuditLog auditLog) {
        return ResponseEntity.ok(auditLogService.createAuditLog(auditLog));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }

    @GetMapping("/logs/action/{action}")
    public ResponseEntity<List<AuditLog>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action));
    }

    @GetMapping("/logs/date-range")
    public ResponseEntity<List<AuditLog>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditLogService.getLogsByDateRange(start, end));
    }

    @GetMapping("/logs/resource/{resource}/{resourceId}")
    public ResponseEntity<List<AuditLog>> getLogsByResource(
            @PathVariable String resource,
            @PathVariable String resourceId) {
        return ResponseEntity.ok(auditLogService.getLogsByResource(resource, resourceId));
    }
}
