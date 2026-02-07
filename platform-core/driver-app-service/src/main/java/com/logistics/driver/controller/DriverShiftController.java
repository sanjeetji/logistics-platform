package com.logistics.driver.controller;

import com.logistics.driver.model.DriverShift;
import com.logistics.driver.service.DriverShiftService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverShiftController {

    private final DriverShiftService shiftService;

    @PostMapping("/{driverId}/shift/start")
    public ResponseEntity<ApiResponse<DriverShift>> startShift(@PathVariable Long driverId) {
        DriverShift shift = shiftService.startShift(driverId);
        return ResponseEntity.ok(ApiResponse.success(shift, "Shift started successfully"));
    }

    @PostMapping("/{driverId}/shift/end")
    public ResponseEntity<ApiResponse<DriverShift>> endShift(@PathVariable Long driverId) {
        DriverShift shift = shiftService.endShift(driverId);
        return ResponseEntity.ok(ApiResponse.success(shift, "Shift ended successfully"));
    }

    @GetMapping("/{driverId}/shift/active")
    public ResponseEntity<ApiResponse<DriverShift>> getActiveShift(@PathVariable Long driverId) {
        DriverShift shift = shiftService.getActiveShift(driverId);
        return ResponseEntity.ok(ApiResponse.success(shift));
    }

    @GetMapping("/{driverId}/shift/history")
    public ResponseEntity<ApiResponse<List<DriverShift>>> getShiftHistory(@PathVariable Long driverId) {
        List<DriverShift> shifts = shiftService.getShiftHistory(driverId);
        return ResponseEntity.ok(ApiResponse.success(shifts));
    }
}
