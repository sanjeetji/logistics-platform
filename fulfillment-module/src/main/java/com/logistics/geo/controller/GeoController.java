package com.logistics.geo.controller;

import com.logistics.geo.dto.DistanceRequest;
import com.logistics.geo.dto.DistanceResponse;
import com.logistics.geo.dto.GeoCoordinates;
import com.logistics.geo.service.GeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;

    @PostMapping("/distance")
    public ResponseEntity<DistanceResponse> calculateDistance(@RequestBody DistanceRequest request) {
        return ResponseEntity.ok(geoService.calculateDistance(request));
    }

    @GetMapping("/geocode")
    public ResponseEntity<GeoCoordinates> geocode(@RequestParam String address) {
        return ResponseEntity.ok(geoService.geocode(address));
    }
}
