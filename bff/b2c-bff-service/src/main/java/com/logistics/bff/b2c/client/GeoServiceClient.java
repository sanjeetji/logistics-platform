package com.logistics.bff.b2c.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "geo-service")
public interface GeoServiceClient {

    @GetMapping("/api/geo/geocode")
    Object geocodeAddress(@RequestParam String address);

    @GetMapping("/api/geo/reverse")
    Object reverseGeocode(@RequestParam Double lat, @RequestParam Double lng);

    @PostMapping("/api/geo/distance")
    Object calculateDistance(@RequestBody Object request);

    @GetMapping("/api/geo/eta")
    Object calculateETA(@RequestParam String from, @RequestParam String to);
}
