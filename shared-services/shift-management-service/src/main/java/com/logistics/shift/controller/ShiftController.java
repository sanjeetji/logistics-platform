package com.logistics.shift.controller;

import com.logistics.shift.entity.*;
import com.logistics.shift.service.ShiftManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Tag(name = "Shift Management", description = "Driver shift scheduling and attendance")
public class ShiftController {
    
    private final ShiftManagementService shiftService;
    
    // Templates
    @PostMapping("/templates")
    @Operation(summary = "Create shift template")
    public ResponseEntity<ShiftTemplate> createTemplate(@RequestBody ShiftTemplate template) {
        return ResponseEntity.ok(shiftService.createTemplate(template));
    }
    
    @GetMapping("/templates")
    @Operation(summary = "Get all active templates")
    public ResponseEntity<List<ShiftTemplate>> getTemplates() {
        return ResponseEntity.ok(shiftService.getAllActiveTemplates());
    }
    
    // Assignments
    @PostMapping("/assign")
    @Operation(summary = "Assign shift to driver")
    public ResponseEntity<ShiftAssignment> assignShift(
            @RequestParam Long driverId,
            @RequestParam Long templateId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(shiftService.assignShift(driverId, templateId, date));
    }
    
    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get driver shifts")
    public ResponseEntity<List<ShiftAssignment>> getDriverShifts(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(shiftService.getDriverShifts(driverId, startDate, endDate));
    }
    
    // Attendance
    @PostMapping("/{assignmentId}/check-in")
    @Operation(summary = "Check in to shift")
    public ResponseEntity<ShiftAssignment> checkIn(
            @PathVariable Long assignmentId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        return ResponseEntity.ok(shiftService.checkIn(assignmentId, latitude, longitude));
    }
    
    @PostMapping("/{assignmentId}/check-out")
    @Operation(summary = "Check out from shift")
    public ResponseEntity<ShiftAssignment> checkOut(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(shiftService.checkOut(assignmentId));
    }
    
    // Shift Swapping
    @PostMapping("/swap/request")
    @Operation(summary = "Request shift swap")
    public ResponseEntity<ShiftSwapRequest> requestSwap(
            @RequestParam Long requestingDriverId,
            @RequestParam Long targetDriverId,
            @RequestParam Long requestingShiftId,
            @RequestParam Long targetShiftId,
            @RequestParam String reason) {
        return ResponseEntity.ok(shiftService.requestSwap(requestingDriverId, targetDriverId, requestingShiftId, targetShiftId, reason));
    }
    
    @PostMapping("/swap/{swapId}/approve")
    @Operation(summary = "Approve shift swap")
    public ResponseEntity<ShiftSwapRequest> approveSwap(
            @PathVariable Long swapId,
            @RequestParam Long managerId) {
        return ResponseEntity.ok(shiftService.approveSwap(swapId, managerId));
    }
}
