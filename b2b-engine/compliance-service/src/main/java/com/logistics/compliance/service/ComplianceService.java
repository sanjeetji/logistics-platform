package com.logistics.compliance.service;

import com.logistics.compliance.model.ComplianceRecord;
import com.logistics.compliance.model.ComplianceStatus;
import com.logistics.compliance.repository.ComplianceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for compliance tracking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceService {

    private final ComplianceRecordRepository complianceRepository;

    /**
     * Create compliance record
     */
    @Transactional
    public ComplianceRecord createComplianceRecord(String orderId, String complianceType,
            String requirements, String evidence) {
        log.info("Creating compliance record for order: {} (type: {})", orderId, complianceType);

        ComplianceRecord record = ComplianceRecord.builder()
                .recordId("COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(orderId)
                .complianceType(complianceType)
                .requirements(requirements)
                .evidence(evidence)
                .status(ComplianceStatus.PENDING_REVIEW)
                .build();

        return complianceRepository.save(record);
    }

    /**
     * Review compliance record
     */
    @Transactional
    public ComplianceRecord reviewCompliance(String recordId, ComplianceStatus status,
            String reviewedBy, String notes) {
        ComplianceRecord record = complianceRepository.findByRecordId(recordId)
                .orElseThrow(() -> new RuntimeException("Compliance record not found: " + recordId));

        record.setStatus(status);
        record.setReviewedBy(reviewedBy);
        record.setReviewedAt(LocalDateTime.now());
        record.setNotes(notes);

        if (status == ComplianceStatus.NON_COMPLIANT) {
            record.setNonComplianceReason(notes);
        }

        return complianceRepository.save(record);
    }

    /**
     * Get order compliance records
     */
    public List<ComplianceRecord> getOrderComplianceRecords(String orderId) {
        return complianceRepository.findByOrderId(orderId);
    }

    /**
     * Get pending compliance reviews
     */
    public List<ComplianceRecord> getPendingReviews() {
        return complianceRepository.findByStatus(ComplianceStatus.PENDING_REVIEW);
    }
}
