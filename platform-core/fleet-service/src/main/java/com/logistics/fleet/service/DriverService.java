package com.logistics.fleet.service;

import com.logistics.fleet.model.Driver;
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

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Driver createDriver(Driver driver) {
        // Business logic: check duplicates, validate license, etc.
        if (driverRepository.findByPhoneNumber(driver.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Driver with this phone number already exists");
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver driverDetails) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        driver.setName(driverDetails.getName());
        driver.setPhoneNumber(driverDetails.getPhoneNumber());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        // ... update other fields as needed
        
        return driverRepository.save(driver);
    }
}
