package com.logistics.exception.repository;

import com.logistics.exception.model.ExceptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionRepository extends JpaRepository<ExceptionRecord, String> {
    List<ExceptionRecord> findByStatus(ExceptionRecord.ExceptionStatus status);

    List<ExceptionRecord> findBySeverity(String severity);

    List<ExceptionRecord> findByServiceName(String serviceName);
}
