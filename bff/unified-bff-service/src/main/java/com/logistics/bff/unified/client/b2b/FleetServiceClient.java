package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.fleet.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {
    @GetMapping("/api/v1/fleet/drivers")
    List<DriverDTO> getDrivers(@RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "available", required = false) Boolean available);

    @GetMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO getDriver(@PathVariable("id") Long id);

    @GetMapping("/api/v1/fleet/vehicles")
    List<VehicleDTO> getVehicles(@RequestParam(name = "status", required = false) String status);

    @GetMapping("/api/v1/fleet/vehicles/{id}")
    VehicleDTO getVehicle(@PathVariable("id") Long id);

    @PostMapping("/api/v1/fleet/assign")
    void assignDriver(@RequestBody Map<String, Object> assignment);
}
