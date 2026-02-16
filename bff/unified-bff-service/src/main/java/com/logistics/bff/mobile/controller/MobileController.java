package com.logistics.bff.mobile.controller;

import com.logistics.platform.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mobile BFF Controller - Mobile App API Gateway
 * 
 * Handles mobile app requests (driver app, customer app)
 * Path: /api/mobile/*
 */
@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
@Slf4j
public class MobileController {

    @GetMapping("/driver/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDriverDashboard(
            @RequestParam String driverId,
            @RequestAttribute(value = "X-Device-Type", required = false) String deviceType) {
        
        log.info("Mobile Driver Dashboard request for driver: {}, device: {}", driverId, deviceType);
        
        Map<String, Object> dashboard = Map.of(
            "type", "Mobile Driver",
            "driverId", driverId,
            "deviceType", deviceType != null ? deviceType : "MOBILE",
            "features", new String[]{"Active Deliveries", "Route", "Earnings", "Profile"}
        );
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/driver/deliveries")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDriverDeliveries(
            @RequestParam String driverId) {
        
        log.info("Mobile Driver Deliveries request for driver: {}", driverId);
        
        Map<String, Object> deliveries = Map.of(
            "type", "Mobile Driver Deliveries",
            "driverId", driverId,
            "message", "Mobile driver deliveries endpoint - integrate with dispatch-service"
        );
        
        return ResponseEntity.ok(ApiResponse.success(deliveries));
    }

    @GetMapping("/customer/orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerOrders(
            @RequestParam String customerId,
            @RequestAttribute(value = "X-Device-Type", required = false) String deviceType) {
        
        log.info("Mobile Customer Orders request for customer: {}, device: {}", customerId, deviceType);
        
        Map<String, Object> orders = Map.of(
            "type", "Mobile Customer Orders",
            "customerId", customerId,
            "deviceType", deviceType != null ? deviceType : "MOBILE",
            "message", "Mobile customer orders endpoint - integrate with order-service"
        );
        
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateLocation(
            @RequestBody Map<String, Object> locationData) {
        
        log.info("Mobile Location update: {}", locationData);
        
        Map<String, Object> response = Map.of(
            "type", "Mobile Location Update",
            "status", "received",
            "message", "Location update endpoint - integrate with location-hub-service"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
