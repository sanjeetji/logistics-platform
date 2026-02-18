package com.logistics.bff.unified.client.b2c;

import com.logistics.platform.dto.fleet.DriverDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {
    @GetMapping("/api/v1/drivers/{id}")
    DriverDTO getDriverById(@PathVariable("id") Long id);
}
