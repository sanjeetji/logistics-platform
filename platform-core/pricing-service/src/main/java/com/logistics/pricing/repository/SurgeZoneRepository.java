package com.logistics.pricing.repository;

import com.logistics.pricing.model.SurgeZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurgeZoneRepository extends JpaRepository<SurgeZone, Long> {
    
    List<SurgeZone> findByActive(Boolean active);
    
    @Query("SELECT s FROM SurgeZone s WHERE s.active = true " +
           "AND (s.activeFrom IS NULL OR s.activeFrom <= :now) " +
           "AND (s.activeTo IS NULL OR s.activeTo >= :now)")
    List<SurgeZone> findActiveSurgeZones(LocalDateTime now);
}
