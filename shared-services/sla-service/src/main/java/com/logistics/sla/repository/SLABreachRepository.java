package com.logistics.sla.repository;

import com.logistics.sla.model.SLABreach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SLABreachRepository extends JpaRepository<SLABreach, Long> {
    List<SLABreach> findByResolvedFalse();
}
