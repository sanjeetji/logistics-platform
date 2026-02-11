package com.logistics.fleet.service;

import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.model.VehicleStatus;
import com.logistics.fleet.model.VehicleType;
import com.logistics.fleet.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findByActive(true);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatusAndActive(VehicleStatus.AVAILABLE, true);
    }

    public List<Vehicle> getVehiclesByType(VehicleType type) {
        return vehicleRepository.findByTypeAndActive(type, true);
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getVehicleByLicensePlate(String licensePlate) {
        if (licensePlate == null) {
            throw new IllegalArgumentException("License plate must not be null");
        }
        return vehicleRepository.findByLicensePlate(licensePlate);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null");
        }
        if (vehicleRepository.findByLicensePlate(vehicle.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException("Vehicle with this license plate already exists");
        }
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicleStatus(Long id, VehicleStatus status) {
        if (id == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle assignVehicleToDriver(Long vehicleId, Long driverId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Vehicle is not available for assignment");
        }

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle assignVehicleToOrder(Long vehicleId, String orderId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setCurrentOrderId(orderId);
        vehicle.setStatus(VehicleStatus.IN_USE);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle releaseVehicle(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setCurrentOrderId(null);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        if (id == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
        vehicle.setConditions(vehicleDetails.getConditions());
        vehicle.setType(vehicleDetails.getType());
        vehicle.setCapacityKg(vehicleDetails.getCapacityKg());
        vehicle.setVolumeCubicMeter(vehicleDetails.getVolumeCubicMeter());
        vehicle.setActive(vehicleDetails.isActive());

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }
}
