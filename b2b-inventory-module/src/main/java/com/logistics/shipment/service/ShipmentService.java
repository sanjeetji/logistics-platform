package com.logistics.shipment.service;

import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import com.logistics.shipment.statemachine.ShipmentEvent;
import com.logistics.shipment.statemachine.ShipmentStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStateMachineService stateMachineService;

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
    public Shipment updateStatus(String shipmentId, ShipmentStatus status, String reason) {
        Shipment shipment = getShipment(shipmentId);

        ShipmentEvent event = mapStatusToEvent(status);
        if (event != null) {
            boolean success = stateMachineService.transitionShipment(shipmentId, event, reason);
            if (!success) {
                throw new IllegalStateException("Failed to transition shipment " + shipmentId + " to status " + status);
            }
        } else {
            // Fallback for direct updates if no event mapping exists (should be avoided)
            shipment.setStatus(status);
            shipmentRepository.save(shipment);
        }

        // Additional logic for specific statuses (can also be moved to State Machine
        // actions)
        if (status == ShipmentStatus.IN_TRANSIT) {
            shipment.setStartTime(LocalDateTime.now());
            shipmentRepository.save(shipment);
        } else if (status == ShipmentStatus.DELIVERED || status == ShipmentStatus.COMPLETED) {
            shipment.setEndTime(LocalDateTime.now());
            shipmentRepository.save(shipment);
        }

        return getShipment(shipmentId);
    }

    @Transactional
    public Shipment assignDriver(String shipmentId, String driverId, String vehicleId) {
        Shipment shipment = getShipment(shipmentId);
        shipment.setDriverId(driverId);
        shipment.setVehicleId(vehicleId);
        shipmentRepository.save(shipment);

        boolean success = stateMachineService.transitionShipment(shipmentId, ShipmentEvent.ASSIGN, "Driver assigned");
        if (!success) {
            throw new IllegalStateException("Failed to transition shipment " + shipmentId + " to ASSIGNED");
        }

        return getShipment(shipmentId);
    }

    private ShipmentEvent mapStatusToEvent(ShipmentStatus status) {
        return switch (status) {
            case ASSIGNED -> ShipmentEvent.ASSIGN;
            case PICKED_UP -> ShipmentEvent.PICKUP;
            case IN_TRANSIT -> ShipmentEvent.START_TRANSIT;
            case AT_HUB -> ShipmentEvent.ARRIVE_HUB;
            case OUT_FOR_DELIVERY -> ShipmentEvent.OUT_FOR_DELIVERY;
            case DELIVERED, COMPLETED -> ShipmentEvent.DELIVER;
            case RETURNED -> ShipmentEvent.RETURN;
            case CANCELLED -> ShipmentEvent.CANCEL;
            default -> null;
        };
    }
}
