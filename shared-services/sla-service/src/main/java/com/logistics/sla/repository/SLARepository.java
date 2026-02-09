package com.logistics.sla.repository;

import com.logistics.sla.model.SLA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SLARepository extends JpaRepository<SLA, Long> {
    List<SLA> findByEntityTypeAndIsActiveTrue(String entityType);
}
