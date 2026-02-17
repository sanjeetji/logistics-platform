package com.logistics.sla.repository;

import com.logistics.sla.model.SLAInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SLAInstanceRepository extends JpaRepository<SLAInstance, Long> {
    Optional<SLAInstance> findBySlaIdAndEntityId(String slaId, String entityId);

    Optional<SLAInstance> findByEntityIdAndIsCompletedFalse(String entityId);

    List<SLAInstance> findByIsCompletedFalse();
}
