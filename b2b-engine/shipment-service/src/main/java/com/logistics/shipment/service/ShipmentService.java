package com.logistics.shipment.service;

import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Shipment createShipment(Shipment shipment) {
        shipment.setShipmentId(UUID.randomUUID().toString());
        shipment.setStatus(ShipmentStatus.CREATED);
        return shipmentRepository.save(shipment);
    }

    public Shipment getShipment(String shipmentId) {
        return shipmentRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));
    }

    public List<Shipment> getShipmentsByDriver(String driverId) {
        return shipmentRepository.findByDriverId(driverId);
    }

    @Transactional
    public Shipment updateStatus(String shipmentId, ShipmentStatus status) {
        Shipment shipment = getShipment(shipmentId);
        shipment.setStatus(status);
        if (status == ShipmentStatus.IN_TRANSIT) {
            shipment.setStartTime(LocalDateTime.now());
        } else if (status == ShipmentStatus.COMPLETED) {
            shipment.setEndTime(LocalDateTime.now());
        }
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment assignDriver(String shipmentId, String driverId, String vehicleId) {
        Shipment shipment = getShipment(shipmentId);
        shipment.setDriverId(driverId);
        shipment.setVehicleId(vehicleId);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        return shipmentRepository.save(shipment);
    }
}
