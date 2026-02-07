package com.logistics.driver.controller;

import com.logistics.driver.model.DriverEarnings;
import com.logistics.driver.service.DriverEarningsService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverEarningsController {

    private final DriverEarningsService earningsService;

    @GetMapping("/{driverId}/earnings")
    public ResponseEntity<ApiResponse<List<DriverEarnings>>> getEarnings(@PathVariable Long driverId) {
        List<DriverEarnings> earnings = earningsService.getDriverEarnings(driverId);
        return ResponseEntity.ok(ApiResponse.success(earnings));
    }

    @GetMapping("/{driverId}/earnings/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEarningsSummary(@PathVariable Long driverId) {
        BigDecimal pending = earningsService.getPendingEarnings(driverId);
        BigDecimal paid = earningsService.getTotalPaidEarnings(driverId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("pendingEarnings", pending);
        summary.put("paidEarnings", paid);
        summary.put("totalEarnings", pending.add(paid));

        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
