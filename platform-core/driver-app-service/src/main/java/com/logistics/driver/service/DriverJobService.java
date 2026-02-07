package com.logistics.driver.service;

import com.logistics.driver.dto.JobActionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for driver job acceptance/rejection
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverJobService {

    private final RestTemplate restTemplate;
    private final DriverShiftService shiftService;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    /**
     * Accept job
     */
    @Transactional
    public Map<String, Object> acceptJob(Long driverId, JobActionRequest request) {
        log.info("Driver {} accepting job: {}", driverId, request.getOrderId());

        // Verify driver has active shift
        shiftService.getActiveShift(driverId);

        // Call order-service to update order status (mock for now)
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("driverId", driverId);
        updateRequest.put("status", "ASSIGNED");

        log.info("Updating order {} to ASSIGNED", request.getOrderId());
        
        // Mock response
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", request.getOrderId());
        response.put("status", "ASSIGNED");
        response.put("message", "Job accepted successfully");

        return response;
    }

    /**
     * Reject job
     */
    @Transactional
    public Map<String, Object> rejectJob(Long driverId, JobActionRequest request) {
        log.info("Driver {} rejecting job: {} with reason: {}", driverId, request.getOrderId(), request.getReason());

        // Log rejection reason for analytics
        // In production, this would update driver metrics and find another driver

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", request.getOrderId());
        response.put("status", "REJECTED");
        response.put("message", "Job rejected");

        return response;
    }

    /**
     * Mark order as picked up
     */
    @Transactional
    public Map<String, Object> markPickedUp(Long driverId, String orderId) {
        log.info("Driver {} marking order {} as picked up", driverId, orderId);

        // Call order-service (mock)
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "PICKED_UP");
        response.put("message", "Order marked as picked up");

        return response;
    }

    /**
     * Mark order as delivered
     */
    @Transactional
    public Map<String, Object> markDelivered(Long driverId, String orderId) {
        log.info("Driver {} marking order {} as delivered", driverId, orderId);

        // Call order-service (mock)
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "DELIVERED");
        response.put("message", "Order marked as delivered");

        return response;
    }
}
