package com.logistics.platform.api.fleet;

import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.fleet.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "fleet-service", path = "/api/v1")
public interface FleetClient {

    @GetMapping("/drivers/{id}")
    DriverDto getDriverById(@PathVariable("id") Long id);

    @GetMapping("/drivers")
    List<DriverDto> getAllDrivers();

    @GetMapping("/vehicles/{id}")
    VehicleDto getVehicleById(@PathVariable("id") Long id);
}
