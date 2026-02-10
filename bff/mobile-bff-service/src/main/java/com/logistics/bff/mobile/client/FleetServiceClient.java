package com.logistics.bff.mobile.client;

import com.logistics.platform.dto.fleet.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {
    
    @GetMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO getDriver(@PathVariable("id") Long id);
    
    @PutMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO updateDriver(@PathVariable("id") Long id, @RequestBody DriverDTO driver);
    
    @PostMapping("/api/v1/fleet/drivers/{id}/availability")
    Object updateAvailability(@PathVariable("id") Long id, @RequestBody Object availabilityData);
}
