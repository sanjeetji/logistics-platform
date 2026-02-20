package com.logistics.fleet.controller;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geospatial")
@RequiredArgsConstructor
public class GeospatialController {

    private final DriverService driverService;

    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<Driver>> getDriversNearby(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "5000") Double radius) {
        return ResponseEntity.ok(driverService.findDriversNearby(lat, lon, radius));
    }

    @GetMapping("/drivers/nearest-available")
    public ResponseEntity<List<Driver>> getNearestAvailableDrivers(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "10000") Double radius) {
        return ResponseEntity.ok(driverService.findNearestAvailableDrivers(lat, lon, radius));
    }
}
