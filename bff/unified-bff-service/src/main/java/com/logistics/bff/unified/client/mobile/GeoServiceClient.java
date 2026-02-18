package com.logistics.bff.unified.client.mobile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geo-service")
public interface GeoServiceClient {
    @GetMapping("/api/geo/geocode")
    Object geocodeAddress(@RequestParam String address);

    @GetMapping("/api/geo/reverse")
    Object reverseGeocode(@RequestParam Double lat, @RequestParam Double lng);

    @PostMapping("/api/geo/route")
    Object getOptimizedRoute(@RequestBody Object request);
}
