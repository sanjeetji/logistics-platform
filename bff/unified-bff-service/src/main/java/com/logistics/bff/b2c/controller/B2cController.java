package com.logistics.bff.b2c.controller;

import com.logistics.platform.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2C BFF Controller - Business-to-Consumer API Gateway
 * 
 * Handles individual customer requests
 * Path: /api/b2c/*
 */
@RestController
@RequestMapping("/api/b2c")
@RequiredArgsConstructor
@Slf4j
public class B2cController {

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHome(
            @RequestAttribute(value = "X-Device-Type", required = false) String deviceType) {
        
        log.info("B2C Home request from device: {}", deviceType);
        
        Map<String, Object> home = Map.of(
            "type", "B2C",
            "deviceType", deviceType != null ? deviceType : "UNKNOWN",
            "features", new String[]{"Track Parcel", "Send Parcel", "Price Calculator", "Support"}
        );
        
        return ResponseEntity.ok(ApiResponse.success(home));
    }

    @GetMapping("/parcels")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getParcels(
            @RequestParam(required = false) String userId) {
        
        log.info("B2C Parcels request for user: {}", userId);
        
        Map<String, Object> parcels = Map.of(
            "type", "B2C Parcels",
            "userId", userId != null ? userId : "guest",
            "message", "B2C parcels endpoint - integrate with parcel-service"
        );
        
        return ResponseEntity.ok(ApiResponse.success(parcels));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> trackParcel(
            @PathVariable String trackingNumber) {
        
        log.info("B2C Tracking request for: {}", trackingNumber);
        
        Map<String, Object> tracking = Map.of(
            "type", "B2C Tracking",
            "trackingNumber", trackingNumber,
            "message", "B2C tracking endpoint - integrate with tracking-service"
        );
        
        return ResponseEntity.ok(ApiResponse.success(tracking));
    }
}
