package com.logistics.analytics.repository;

import com.logistics.analytics.model.CarbonFootprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarbonFootprintRepository extends JpaRepository<CarbonFootprint, Long> {

    List<CarbonFootprint> findByEntityId(String entityId);

    @Query("SELECT SUM(c.totalCo2EmissionKg) FROM CarbonFootprint c")
    Double getTotalEmissions();

    @Query("SELECT SUM(c.totalCo2EmissionKg) FROM CarbonFootprint c WHERE c.calculatedAt >= :startDate")
    Double getEmissionsSince(LocalDateTime startDate);
}
