package com.logistics.compliance.controller;

import com.logistics.compliance.model.ComplianceRecord;
import com.logistics.compliance.model.ComplianceStatus;
import com.logistics.compliance.service.ComplianceService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ComplianceRecord>> createComplianceRecord(
            @RequestParam String orderId,
            @RequestParam String complianceType,
            @RequestParam String requirements,
            @RequestParam(required = false) String evidence) {
        ComplianceRecord record = complianceService.createComplianceRecord(
                orderId, complianceType, requirements, evidence);
        return ResponseEntity.ok(ApiResponse.success(record, "Compliance record created"));
    }

    @PutMapping("/{recordId}/review")
    public ResponseEntity<ApiResponse<ComplianceRecord>> reviewCompliance(
            @PathVariable String recordId,
            @RequestParam ComplianceStatus status,
            @RequestParam String reviewedBy,
            @RequestParam(required = false) String notes) {
        ComplianceRecord record = complianceService.reviewCompliance(recordId, status, reviewedBy, notes);
        return ResponseEntity.ok(ApiResponse.success(record, "Compliance reviewed"));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<ComplianceRecord>>> getOrderComplianceRecords(@PathVariable String orderId) {
        List<ComplianceRecord> records = complianceService.getOrderComplianceRecords(orderId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ComplianceRecord>>> getPendingReviews() {
        List<ComplianceRecord> records = complianceService.getPendingReviews();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
