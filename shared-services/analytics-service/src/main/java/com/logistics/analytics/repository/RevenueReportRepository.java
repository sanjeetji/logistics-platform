package com.logistics.analytics.repository;

import com.logistics.analytics.model.RevenueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueReportRepository extends JpaRepository<RevenueReport, Long> {
    
    Optional<RevenueReport> findByPeriod(LocalDate period);
    
    List<RevenueReport> findByPeriodBetween(LocalDate start, LocalDate end);
    
    List<RevenueReport> findTop30ByOrderByPeriodDesc();
}
