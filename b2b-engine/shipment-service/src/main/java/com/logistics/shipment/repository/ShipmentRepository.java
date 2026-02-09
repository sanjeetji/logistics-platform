package com.logistics.shipment.repository;

import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByShipmentId(String shipmentId);

    List<Shipment> findByDriverId(String driverId);

    List<Shipment> findByStatus(ShipmentStatus status);
}
