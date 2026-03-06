package com.logistics.exception.controller;

import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.service.ExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionService exceptionService;

    @GetMapping
    public ResponseEntity<List<ExceptionRecord>> getAllExceptions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String serviceName) {

        if (status != null) {
            return ResponseEntity
                    .ok(exceptionService.getExceptionsByStatus(ExceptionRecord.ExceptionStatus.valueOf(status)));
        }
        if (severity != null) {
            return ResponseEntity.ok(exceptionService.getExceptionsBySeverity(severity));
        }
        if (serviceName != null) {
            return ResponseEntity.ok(exceptionService.getExceptionsByService(serviceName));
        }
        return ResponseEntity.ok(exceptionService.getAllExceptions());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ExceptionRecord> resolveException(
            @PathVariable String id,
            @RequestParam String resolvedBy,
            @RequestParam String notes) {
        return ResponseEntity.ok(exceptionService.resolveException(id, resolvedBy, notes));
    }
}
