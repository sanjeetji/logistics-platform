package com.logistics.fleet.controller;

import com.logistics.fleet.dto.DriverBehaviorEventDto;
import com.logistics.fleet.service.DriverBehaviorService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fleet/behavior")
@RequiredArgsConstructor
@Slf4j
public class DriverBehaviorController {

    private final DriverBehaviorService behaviorService;

    @PostMapping("/events")
    @PreAuthorize("hasAuthority('DRIVER_BEHAVIOR_TRACKING') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> reportBehaviorEvent(
            @Valid @RequestBody DriverBehaviorEventDto eventDto,
            @RequestHeader(value = "X-Tenant-ID", required = true) Long tenantId) {

        behaviorService.processBehaviorEvent(eventDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Behavior event recorded and processed."));
    }

    @GetMapping("/analytics/{driverExternalId}")
    @PreAuthorize("hasAuthority('ADVANCED_DRIVER_ANALYTICS') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> getDriverAnalytics(
            @PathVariable String driverExternalId,
            @RequestHeader(value = "X-Tenant-ID", required = true) Long tenantId) {

        // In a real implementation this would return aggregation of events,
        // trend lines, and comparative scores.
        return ResponseEntity.ok(ApiResponse.success(null, "Advanced analytics for driver " + driverExternalId));
    }
}
