package com.logistics.pricing.repository;

import com.logistics.pricing.model.PriceEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceEstimateRepository extends JpaRepository<PriceEstimate, Long> {
    
    Optional<PriceEstimate> findByEstimateId(String estimateId);
    
    Optional<PriceEstimate> findByOrderId(String orderId);
}
