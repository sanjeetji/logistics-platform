package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "geo-service")
public interface GeoServiceClient {
    @GetMapping("/api/geo/geocode")
    Object geocodeAddress(@RequestParam String address);

    @GetMapping("/api/geo/reverse")
    Object reverseGeocode(@RequestParam Double lat, @RequestParam Double lng);

    @PostMapping("/api/geo/distance")
    Object calculateDistance(@RequestBody Object request);

    @GetMapping("/api/geo/eta")
    Object getETA(@RequestParam String origin, @RequestParam String destination);
}
