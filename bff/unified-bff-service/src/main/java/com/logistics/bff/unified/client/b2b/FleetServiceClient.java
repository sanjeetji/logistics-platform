package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.fleet.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {
    
    @GetMapping("/api/v1/fleet/drivers")
    List<DriverDTO> getDrivers(@RequestParam(required = false) String status,
                               @RequestParam(required = false) Boolean isAvailable);
    
    @GetMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO getDriver(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/fleet/vehicles")
    List<VehicleDTO> getVehicles(@RequestParam(required = false) String status,
                                 @RequestParam(required = false) String type);
    
    @GetMapping("/api/v1/fleet/vehicles/{id}")
    VehicleDTO getVehicle(@PathVariable("id") Long id);
    
    @PostMapping("/api/v1/fleet/assign")
    Object assignDriver(@RequestBody Object assignmentData);
}
