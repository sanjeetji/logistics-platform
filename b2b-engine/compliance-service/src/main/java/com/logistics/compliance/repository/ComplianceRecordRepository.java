package com.logistics.compliance.repository;

import com.logistics.compliance.model.ComplianceRecord;
import com.logistics.compliance.model.ComplianceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    Optional<ComplianceRecord> findByRecordId(String recordId);

    List<ComplianceRecord> findByOrderId(String orderId);

    List<ComplianceRecord> findByStatus(ComplianceStatus status);

    List<ComplianceRecord> findByComplianceType(String complianceType);
}
