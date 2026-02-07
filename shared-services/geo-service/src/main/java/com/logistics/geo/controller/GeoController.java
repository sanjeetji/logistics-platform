package com.logistics.geo.controller;

import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
@Slf4j
public class GeoController {

    @GetMapping("/drivers/nearby")
    public ResponseEntity<ApiResponse<List<DriverLocationDto>>> findDriversNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Double radiusKm) {

        log.info("Searching for drivers within {}km of {}, {}", radiusKm, lat, lng);

        // Placeholder: Return a mock driver for now
        List<DriverLocationDto> drivers = new ArrayList<>();
        drivers.add(DriverLocationDto.builder()
                .driverId("driver-123")
                .lat(lat + 0.001)
                .lng(lng + 0.001)
                .vehicleType("BIKE")
                .distanceKm(0.5)
                .build());

        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
}
