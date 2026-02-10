package com.logistics.pricing.repository;

import com.logistics.pricing.model.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, Long> {
    
    Optional<RateCard> findByVersion(String version);

    Optional<RateCard> findByIsDefaultTrue();

    @Query("SELECT r FROM RateCard r WHERE r.active = true AND " +
           "r.effectiveFrom <= :now AND (r.effectiveTo IS NULL OR r.effectiveTo >= :now) " +
           "ORDER BY r.effectiveFrom DESC")
    Optional<RateCard> findCurrentActiveRateCard(LocalDateTime now);
}
