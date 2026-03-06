package com.logistics.pricing.repository;

import com.logistics.pricing.model.EnterpriseContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EnterpriseContractRepository extends JpaRepository<EnterpriseContract, Long> {

    @Query("SELECT c FROM EnterpriseContract c WHERE c.clientId = :clientId AND c.isActive = true " +
            "AND :targetDate BETWEEN c.validFrom AND c.validTo")
    Optional<EnterpriseContract> findActiveContractByClientIdAndDate(
            @Param("clientId") String clientId,
            @Param("targetDate") LocalDateTime targetDate);
}
