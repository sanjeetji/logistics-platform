package com.logistics.bff.mobile.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "geo-service")
public interface GeoServiceClient {
    
    @GetMapping("/api/geo/geocode")
    Object geocodeAddress(@RequestParam String address);
    
    @GetMapping("/api/geo/reverse")
    Object reverseGeocode(@RequestParam Double lat, @RequestParam Double lng);
    
    @PostMapping("/api/geo/route")
    Object getOptimizedRoute(@RequestBody Object request);
}
