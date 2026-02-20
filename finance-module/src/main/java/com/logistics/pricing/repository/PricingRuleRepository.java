package com.logistics.pricing.repository;

import com.logistics.pricing.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    Optional<PricingRule> findByVehicleTypeAndActive(String vehicleType, Boolean active);

    List<PricingRule> findByActive(Boolean active);

    @Query("SELECT p FROM PricingRule p WHERE p.vehicleType = :vehicleType " +
            "AND p.active = true " +
            "AND (p.effectiveFrom IS NULL OR p.effectiveFrom <= :now) " +
            "AND (p.effectiveTo IS NULL OR p.effectiveTo >= :now)")
    List<PricingRule> findEffectiveRules(String vehicleType, LocalDateTime now);
}
