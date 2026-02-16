package com.logistics.fleet.service;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.DriverStatus;
import com.logistics.fleet.repository.DriverRepository;
import com.logistics.fleet.statemachine.DriverEvent;
import com.logistics.fleet.statemachine.DriverStateMachineService;
import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverStateMachineService stateMachineService;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE);
    }

    public List<Driver> getOnlineDrivers() {
        return driverRepository.findByStatusIn(List.of(DriverStatus.ONLINE, DriverStatus.AVAILABLE));
    }

    public Optional<Driver> getDriverById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        return driverRepository.findById(id);
    }

    public Driver createDriver(Driver driver) {
        if (driverRepository.findByPhoneNumber(driver.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Driver with this phone number already exists");
        }
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.OFFLINE);
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver driverDetails) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        driver.setName(driverDetails.getName());
        driver.setPhoneNumber(driverDetails.getPhoneNumber());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        driver.setEmail(driverDetails.getEmail());

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriverStatus(Long id, DriverStatus status, String reason) {
        Driver driver = getDriverById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        DriverEvent event = mapStatusToEvent(status);
        if (event != null) {
            boolean success = stateMachineService.transitionDriver(driver.getEmail(), event, reason);
            if (!success) {
                throw new IllegalStateException(
                        "Failed to transition driver " + driver.getEmail() + " to status " + status);
            }
        } else {
            driver.setStatus(status);
            driverRepository.save(driver);
        }

        return getDriverById(id).get();
    }

    public Driver updateDriverLocation(Long id, Double latitude, Double longitude) {
        Driver driver = getDriverById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        driver.setCurrentLocation(com.logistics.fleet.utils.GeoUtils.createPoint(latitude, longitude));
        driver.setLastLocationUpdate(java.time.LocalDateTime.now());
        return driverRepository.save(driver);
    }

    public List<Driver> findDriversNearby(Double latitude, Double longitude, Double radiusInMeters) {
        org.locationtech.jts.geom.Point point = com.logistics.fleet.utils.GeoUtils.createPoint(latitude, longitude);
        return driverRepository.findDriversWithinRadius(point, radiusInMeters);
    }

    public List<Driver> findNearestAvailableDrivers(Double latitude, Double longitude, Double radiusInMeters) {
        org.locationtech.jts.geom.Point point = com.logistics.fleet.utils.GeoUtils.createPoint(latitude, longitude);
        return driverRepository.findNearestAvailableDrivers(point, radiusInMeters);
    }

    public List<Driver> findDriversInZone(String wktPolygon) {
        return driverRepository.findDriversInZone(wktPolygon);
    }

    @Transactional
    public Driver assignDriverToOrder(Long driverId, String orderId, Long vehicleId) {
        Driver driver = getDriverById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        driver.setCurrentOrderId(orderId);
        driver.setCurrentVehicleId(vehicleId);
        driverRepository.save(driver);

        boolean success = stateMachineService.transitionDriver(driver.getEmail(), DriverEvent.ASSIGN,
                "Assigned to order: " + orderId);
        if (!success) {
            throw new IllegalStateException("Failed to transition driver " + driver.getEmail() + " to ASSIGNED");
        }

        return getDriverById(driverId).get();
    }

    @Transactional
    public Driver releaseDriver(Long driverId) {
        Driver driver = getDriverById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        driver.setCurrentOrderId(null);
        driver.setCurrentVehicleId(null);
        driverRepository.save(driver);

        boolean success = stateMachineService.transitionDriver(driver.getEmail(), DriverEvent.GO_ONLINE,
                "Released from order");
        if (!success) {
            throw new IllegalStateException("Failed to transition driver " + driver.getEmail() + " to AVAILABLE");
        }

        return getDriverById(driverId).get();
    }

    private DriverEvent mapStatusToEvent(DriverStatus status) {
        return switch (status) {
            case AVAILABLE, ONLINE -> DriverEvent.GO_ONLINE;
            case ASSIGNED, ON_TRIP -> DriverEvent.ASSIGN;
            case EN_ROUTE_PICKUP -> DriverEvent.START_PICKUP;
            case AT_PICKUP -> DriverEvent.ARRIVE_PICKUP;
            case EN_ROUTE_DELIVERY -> DriverEvent.START_DELIVERY;
            case AT_DELIVERY -> DriverEvent.ARRIVE_DELIVERY;
            case ON_BREAK -> DriverEvent.TAKE_BREAK;
            case OFFLINE -> DriverEvent.GO_OFFLINE;
        };
    }

    public void deleteDriver(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        driverRepository.deleteById(id);
    }
}
