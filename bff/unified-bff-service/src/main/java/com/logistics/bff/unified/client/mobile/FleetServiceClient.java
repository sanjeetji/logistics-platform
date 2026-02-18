package com.logistics.bff.unified.client.mobile;

import com.logistics.platform.dto.fleet.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {
    @GetMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO getDriverById(@PathVariable("id") Long id);

    @PutMapping("/api/v1/fleet/drivers/{id}")
    DriverDTO updateDriver(@PathVariable("id") Long id, @RequestBody DriverDTO driver);

    @PostMapping("/api/v1/fleet/drivers/{id}/availability")
    void updateAvailability(@PathVariable("id") Long id, @RequestBody Map<String, Object> availability);
}
