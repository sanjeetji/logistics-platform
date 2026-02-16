package com.logistics.analytics.repository;

import com.logistics.analytics.model.DeliveryPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPerformanceRepository extends JpaRepository<DeliveryPerformance, Long> {
}
