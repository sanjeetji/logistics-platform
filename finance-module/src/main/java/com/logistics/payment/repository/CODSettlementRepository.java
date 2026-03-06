package com.logistics.payment.repository;

import com.logistics.payment.model.CODSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CODSettlementRepository extends JpaRepository<CODSettlement, Long> {
    Optional<CODSettlement> findByOrderId(String orderId);

    List<CODSettlement> findByDriverIdAndStatus(String driverId, CODSettlement.SettlementStatus status);
}
