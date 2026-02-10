package com.logistics.pricing.repository;

import com.logistics.pricing.model.SurgePricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SurgePricingRuleRepository extends JpaRepository<SurgePricingRule, Long> {
    
    List<SurgePricingRule> findByActiveTrue();

    @Query("SELECT s FROM SurgePricingRule s WHERE s.active = true AND s.surgeType = 'TIME_BASED' " +
           "AND s.startTime <= :currentTime AND s.endTime >= :currentTime ORDER BY s.priority DESC")
    List<SurgePricingRule> findActiveTimeBasedRules(LocalTime currentTime);

    @Query("SELECT s FROM SurgePricingRule s WHERE s.active = true AND s.surgeType = 'DEMAND_BASED' " +
           "AND s.demandThreshold <= :currentDemand ORDER BY s.priority DESC")
    List<SurgePricingRule> findActiveDemandBasedRules(Integer currentDemand);
}
