package com.logistics.analytics.repository;

import com.logistics.analytics.model.DashboardMetric;
import com.logistics.analytics.model.MetricType;
import com.logistics.analytics.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardMetricRepository extends JpaRepository<DashboardMetric, Long> {
    
    List<DashboardMetric> findByMetricType(MetricType metricType);
    
    List<DashboardMetric> findByMetricTypeAndPeriod(MetricType metricType, Period period);
    
    List<DashboardMetric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<DashboardMetric> findByMetricTypeAndTimestampBetween(MetricType metricType, 
                                                               LocalDateTime start, 
                                                               LocalDateTime end);
}
