package com.logistics.fleet.controller;

import com.logistics.fleet.mapper.VehicleMapper;
import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.service.VehicleService;
import com.logistics.platform.common.dto.fleet.VehicleDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    @GetMapping
    public ApiResponse<List<VehicleDto>> getAllVehicles() {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles().stream()
                .map(vehicleMapper::toDto)
                .toList();
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/active")
    public ApiResponse<List<VehicleDto>> getActiveVehicles() {
        List<VehicleDto> vehicles = vehicleService.getActiveVehicles().stream()
                .map(vehicleMapper::toDto)
                .toList();
        return ApiResponse.success(vehicles);
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleDto> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(vehicleMapper::toDto)
                .map(ApiResponse::success)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @PostMapping
    public ApiResponse<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
        return ApiResponse.success(vehicleMapper.toDto(createdVehicle));
    }
}
