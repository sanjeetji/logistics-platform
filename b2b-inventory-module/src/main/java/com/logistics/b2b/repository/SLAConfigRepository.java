package com.logistics.b2b.repository;

import com.logistics.b2b.model.OrderType;
import com.logistics.b2b.model.Priority;
import com.logistics.b2b.model.SLAConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SLAConfigRepository extends JpaRepository<SLAConfig, Long> {

    Optional<SLAConfig> findByClientIdAndOrderTypeAndPriority(Long clientId, OrderType orderType, Priority priority);

    Optional<SLAConfig> findByClientIdIsNullAndOrderTypeAndPriority(OrderType orderType, Priority priority);
}
