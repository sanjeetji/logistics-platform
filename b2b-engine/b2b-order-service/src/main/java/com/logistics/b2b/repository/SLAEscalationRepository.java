package com.logistics.b2b.repository;

import com.logistics.b2b.model.SLAEscalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SLAEscalationRepository extends JpaRepository<SLAEscalation, Long> {
    List<SLAEscalation> findByOrderId(String orderId);
}
