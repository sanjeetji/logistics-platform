package com.logistics.analytics.controller;

import com.logistics.analytics.service.ControlTowerService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/control-tower")
@RequiredArgsConstructor
public class ControlTowerController {

    private final ControlTowerService controlTowerService;

    /**
     * Main endpoint for the Unified Control Tower BI Dashboard.
     * Returns highly aggregated, multi-module real-time metrics.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ControlTowerService.ControlTowerDashboard>> getDashboard(
            @RequestHeader("X-Tenant-Id") String tenantId) {

        ControlTowerService.ControlTowerDashboard dashboard = controlTowerService.getAggregatedDashboard(tenantId);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
