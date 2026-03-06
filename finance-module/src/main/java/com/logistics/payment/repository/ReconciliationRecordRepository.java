package com.logistics.payment.repository;

import com.logistics.payment.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long> {
}
