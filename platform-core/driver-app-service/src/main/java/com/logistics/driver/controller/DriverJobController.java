package com.logistics.driver.controller;

import com.logistics.driver.dto.JobActionRequest;
import com.logistics.driver.service.DriverJobService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverJobController {

    private final DriverJobService jobService;

    @PostMapping("/{driverId}/jobs/accept")
    public ResponseEntity<ApiResponse<Map<String, Object>>> acceptJob(
            @PathVariable Long driverId,
            @Valid @RequestBody JobActionRequest request) {
        Map<String, Object> result = jobService.acceptJob(driverId, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Job accepted successfully"));
    }

    @PostMapping("/{driverId}/jobs/reject")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rejectJob(
            @PathVariable Long driverId,
            @Valid @RequestBody JobActionRequest request) {
        Map<String, Object> result = jobService.rejectJob(driverId, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Job rejected"));
    }

    @PostMapping("/{driverId}/orders/{orderId}/pickup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markPickedUp(
            @PathVariable Long driverId,
            @PathVariable String orderId) {
        Map<String, Object> result = jobService.markPickedUp(driverId, orderId);
        return ResponseEntity.ok(ApiResponse.success(result, "Order marked as picked up"));
    }

    @PostMapping("/{driverId}/orders/{orderId}/deliver")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markDelivered(
            @PathVariable Long driverId,
            @PathVariable String orderId) {
        Map<String, Object> result = jobService.markDelivered(driverId, orderId);
        return ResponseEntity.ok(ApiResponse.success(result, "Order marked as delivered"));
    }
}
