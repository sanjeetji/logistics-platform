package com.logistics.masterdata.controller;

import com.logistics.masterdata.model.City;
import com.logistics.masterdata.model.ServiceZone;
import com.logistics.masterdata.model.VehicleType;
import com.logistics.masterdata.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master-data")
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService masterDataService;

    // City Endpoints
    @GetMapping("/cities")
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(masterDataService.getAllCities());
    }

    @GetMapping("/cities/country/{country}")
    public ResponseEntity<List<City>> getCitiesByCountry(@PathVariable String country) {
        return ResponseEntity.ok(masterDataService.getCitiesByCountry(country));
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody City city) {
        return ResponseEntity.ok(masterDataService.createCity(city));
    }

    // Zone Endpoints
    @GetMapping("/zones/{cityId}")
    public ResponseEntity<List<ServiceZone>> getZonesByCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(masterDataService.getZonesByCity(cityId));
    }

    @PostMapping("/zones")
    public ResponseEntity<ServiceZone> createZone(@RequestBody ServiceZone zone) {
        return ResponseEntity.ok(masterDataService.createZone(zone));
    }

    // Vehicle Type Endpoints
    @GetMapping("/vehicle-types")
    public ResponseEntity<List<VehicleType>> getAllVehicleTypes() {
        return ResponseEntity.ok(masterDataService.getAllVehicleTypes());
    }

    @GetMapping("/vehicle-types/active")
    public ResponseEntity<List<VehicleType>> getActiveVehicleTypes() {
        return ResponseEntity.ok(masterDataService.getActiveVehicleTypes());
    }

    @PostMapping("/vehicle-types")
    public ResponseEntity<VehicleType> createVehicleType(@RequestBody VehicleType vehicleType) {
        return ResponseEntity.ok(masterDataService.createVehicleType(vehicleType));
    }
}
