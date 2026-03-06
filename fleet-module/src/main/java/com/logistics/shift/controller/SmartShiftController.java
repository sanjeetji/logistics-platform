package com.logistics.shift.controller;

import com.logistics.shift.entity.ShiftAssignment;
import com.logistics.shift.service.SmartShiftPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet/shifts/smart-planning")
@RequiredArgsConstructor
public class SmartShiftController {

    private final SmartShiftPlanningService smartPlanningService;

    @PostMapping("/auto-assign")
    public ResponseEntity<List<ShiftAssignment>> autoAssignShifts(
            @RequestParam String region,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShiftAssignment> assignments = smartPlanningService.autoPlanShifts(region, date);
        return ResponseEntity.ok(assignments);
    }
}
