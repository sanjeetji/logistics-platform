package com.logistics.fleet.controller;

import com.logistics.fleet.mapper.DriverMapper;
import com.logistics.fleet.model.Driver;
import com.logistics.fleet.service.DriverService;
import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @GetMapping
    public ApiResponse<List<DriverDto>> getAllDrivers() {
        List<DriverDto> drivers = driverService.getAllDrivers().stream()
                .map(driverMapper::toDto)
                .toList();
        return ApiResponse.success(drivers);
    }

    @GetMapping("/available")
    public ApiResponse<List<DriverDto>> getAvailableDrivers() {
        List<DriverDto> drivers = driverService.getAvailableDrivers().stream()
                .map(driverMapper::toDto)
                .toList();
        return ApiResponse.success(drivers);
    }

    @GetMapping("/{id}")
    public ApiResponse<DriverDto> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id)
                .map(driverMapper::toDto)
                .map(ApiResponse::success)
                .orElseThrow(() -> new RuntimeException("Driver not found")); // Should specific exception
    }

    @PostMapping
    public ApiResponse<DriverDto> createDriver(@RequestBody DriverDto driverDto) {
        Driver driver = driverMapper.toEntity(driverDto);
        Driver createdDriver = driverService.createDriver(driver);
        return ApiResponse.success(driverMapper.toDto(createdDriver));
    }

    @PutMapping("/{id}")
    public ApiResponse<DriverDto> updateDriver(@PathVariable Long id, @RequestBody DriverDto driverDto) {
        Driver driver = driverMapper.toEntity(driverDto);
        Driver updatedDriver = driverService.updateDriver(id, driver);
        return ApiResponse.success(driverMapper.toDto(updatedDriver));
    }
}
