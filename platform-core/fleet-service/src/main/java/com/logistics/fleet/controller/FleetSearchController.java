package com.logistics.fleet.controller;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.service.DriverService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet/search")
@RequiredArgsConstructor
public class FleetSearchController {

    private final DriverService driverService;

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<Driver>>> searchNearby(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "5000") Double radius) {

        List<Driver> drivers = driverService.findDriversNearby(lat, lon, radius);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/nearest-available")
    public ResponseEntity<ApiResponse<List<Driver>>> searchNearestAvailable(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "10000") Double radius) {

        List<Driver> drivers = driverService.findNearestAvailableDrivers(lat, lon, radius);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @PostMapping("/zone")
    public ResponseEntity<ApiResponse<List<Driver>>> searchInZone(@RequestBody String wktPolygon) {
        List<Driver> drivers = driverService.findDriversInZone(wktPolygon);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
}
