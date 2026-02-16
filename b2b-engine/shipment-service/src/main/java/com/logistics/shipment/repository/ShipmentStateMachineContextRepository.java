package com.logistics.shipment.repository;

import com.logistics.shipment.model.ShipmentStateMachineContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentStateMachineContextRepository extends JpaRepository<ShipmentStateMachineContext, Long> {
    Optional<ShipmentStateMachineContext> findByShipmentId(String shipmentId);

    void deleteByShipmentId(String shipmentId);
}
