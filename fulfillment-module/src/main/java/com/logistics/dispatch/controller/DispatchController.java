package com.logistics.dispatch.controller;

import com.logistics.dispatch.dto.DispatchRequest;
import com.logistics.dispatch.dto.DriverScore;
import com.logistics.dispatch.model.DispatchAssignment;
import com.logistics.dispatch.model.DispatchJob;
import com.logistics.dispatch.service.DispatchService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping("/find-driver")
    public ResponseEntity<ApiResponse<DriverScore>> findBestDriver(
            @Valid @RequestBody DispatchRequest request) {
        DriverScore driver = dispatchService.findBestDriver(request);
        if (driver == null) {
            return ResponseEntity.ok(ApiResponse.error("No available drivers found"));
        }
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<DispatchAssignment>> assignOrder(
            @RequestParam String orderId,
            @RequestParam Long driverId,
            @RequestParam(required = false) Long vehicleId) {
        DispatchAssignment assignment = dispatchService.assignOrderToDriver(orderId, driverId, vehicleId);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Order assigned successfully"));
    }

    @PostMapping("/auto-dispatch")
    public ResponseEntity<ApiResponse<DispatchJob>> autoDispatch(
            @Valid @RequestBody DispatchRequest request) {
        DispatchJob job = dispatchService.autoDispatch(request);
        return ResponseEntity.ok(ApiResponse.success(job, "Order auto-dispatch initiated"));
    }

    @GetMapping("/assignment/{orderId}")
    public ResponseEntity<ApiResponse<DispatchAssignment>> getAssignment(@PathVariable String orderId) {
        return dispatchService.getAssignmentByOrderId(orderId)
                .map(assignment -> ResponseEntity.ok(ApiResponse.success(assignment)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Assignment not found")));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<DispatchAssignment>> cancelAssignment(@PathVariable String orderId) {
        DispatchAssignment assignment = dispatchService.cancelAssignment(orderId);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Assignment cancelled"));
    }
}
