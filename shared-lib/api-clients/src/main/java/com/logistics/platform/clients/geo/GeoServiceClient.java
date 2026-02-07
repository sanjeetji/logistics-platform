package com.logistics.platform.clients.geo;

import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "geo-service", path = "/api/v1/geo")
public interface GeoServiceClient {

    @GetMapping("/drivers/nearby")
    ApiResponse<List<DriverLocationDto>> findDriversNearby(
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            @RequestParam("radiusKm") Double radiusKm);
}
