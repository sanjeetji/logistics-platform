package com.logistics.fleet.service;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.DriverStatus;
import com.logistics.fleet.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.ONLINE);
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
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setName(driverDetails.getName());
        driver.setPhoneNumber(driverDetails.getPhoneNumber());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        driver.setEmail(driverDetails.getEmail());

        return driverRepository.save(driver);
    }

    public Driver updateDriverStatus(Long id, DriverStatus status) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    public Driver updateDriverLocation(Long id, Double latitude, Double longitude) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        driver.setLastLocationUpdate(java.time.LocalDateTime.now());
        return driverRepository.save(driver);
    }

    public Driver assignDriverToOrder(Long driverId, String orderId, Long vehicleId) {
        if (driverId == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getStatus() != DriverStatus.ONLINE && driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver is not available for assignment");
        }

        driver.setCurrentOrderId(orderId);
        driver.setCurrentVehicleId(vehicleId);
        driver.setStatus(DriverStatus.ON_TRIP);
        return driverRepository.save(driver);
    }

    public Driver releaseDriver(Long driverId) {
        if (driverId == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setCurrentOrderId(null);
        driver.setCurrentVehicleId(null);
        driver.setStatus(DriverStatus.ONLINE);
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID must not be null");
        }
        driverRepository.deleteById(id);
    }
}
