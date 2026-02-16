package com.logistics.shift.service;

import com.logistics.shift.entity.*;
import com.logistics.shift.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftManagementService {
    
    private final ShiftTemplateRepository templateRepository;
    private final ShiftAssignmentRepository assignmentRepository;
    private final ShiftSwapRequestRepository swapRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // Template Management
    public ShiftTemplate createTemplate(ShiftTemplate template) {
        ShiftTemplate saved = templateRepository.save(template);
        log.info("Created shift template: {}", saved.getName());
        return saved;
    }
    
    public List<ShiftTemplate> getAllActiveTemplates() {
        return templateRepository.findByActiveTrue();
    }
    
    // Shift Assignment
    @Transactional
    public ShiftAssignment assignShift(Long driverId, Long templateId, LocalDate date) {
        ShiftTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        
        // Check if driver already has a shift on this date
        if (assignmentRepository.existsByDriverIdAndShiftDate(driverId, date)) {
            throw new IllegalStateException("Driver already has a shift on this date");
        }
        
        ShiftAssignment assignment = ShiftAssignment.builder()
                .driverId(driverId)
                .shiftTemplate(template)
                .shiftDate(date)
                .status(ShiftAssignment.ShiftStatus.SCHEDULED)
                .build();
        
        assignment = assignmentRepository.save(assignment);
        log.info("Assigned shift {} to driver {} on {}", templateId, driverId, date);
        
        kafkaTemplate.send("shift-assigned", driverId.toString(), assignment);
        return assignment;
    }
    
    public List<ShiftAssignment> getDriverShifts(Long driverId, LocalDate startDate, LocalDate endDate) {
        return assignmentRepository.findByDriverIdAndShiftDateBetween(driverId, startDate, endDate);
    }
    
    // Attendance
    @Transactional
    public ShiftAssignment checkIn(Long assignmentId, Double latitude, Double longitude) {
        ShiftAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        assignment.setCheckInTime(LocalDateTime.now());
        assignment.setCheckInLatitude(latitude);
        assignment.setCheckInLongitude(longitude);
        assignment.setLocationVerified(true);
        assignment.setStatus(ShiftAssignment.ShiftStatus.IN_PROGRESS);
        
        assignment = assignmentRepository.save(assignment);
        log.info("Driver {} checked in to shift {}", assignment.getDriverId(), assignmentId);
        
        kafkaTemplate.send("shift-check-in", assignment.getDriverId().toString(), assignment);
        return assignment;
    }
    
    @Transactional
    public ShiftAssignment checkOut(Long assignmentId) {
        ShiftAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        assignment.setCheckOutTime(LocalDateTime.now());
        assignment.setStatus(ShiftAssignment.ShiftStatus.COMPLETED);
        
        assignment = assignmentRepository.save(assignment);
        log.info("Driver {} checked out from shift {}", assignment.getDriverId(), assignmentId);
        
        kafkaTemplate.send("shift-check-out", assignment.getDriverId().toString(), assignment);
        return assignment;
    }
    
    // Shift Swapping
    @Transactional
    public ShiftSwapRequest requestSwap(Long requestingDriverId, Long targetDriverId, Long requestingShiftId, Long targetShiftId, String reason) {
        ShiftAssignment requestingShift = assignmentRepository.findById(requestingShiftId)
                .orElseThrow(() -> new IllegalArgumentException("Requesting shift not found"));
        ShiftAssignment targetShift = assignmentRepository.findById(targetShiftId)
                .orElseThrow(() -> new IllegalArgumentException("Target shift not found"));
        
        ShiftSwapRequest swapRequest = ShiftSwapRequest.builder()
                .requestingDriverId(requestingDriverId)
                .targetDriverId(targetDriverId)
                .requestingShift(requestingShift)
                .targetShift(targetShift)
                .reason(reason)
                .status(ShiftSwapRequest.SwapStatus.PENDING)
                .build();
        
        swapRequest = swapRepository.save(swapRequest);
        log.info("Created swap request from driver {} to driver {}", requestingDriverId, targetDriverId);
        
        kafkaTemplate.send("shift-swap-requested", targetDriverId.toString(), swapRequest);
        return swapRequest;
    }
    
    @Transactional
    public ShiftSwapRequest approveSwap(Long swapId, Long managerId) {
        ShiftSwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));
        
        // Swap the drivers
        ShiftAssignment requestingShift = swap.getRequestingShift();
        ShiftAssignment targetShift = swap.getTargetShift();
        
        Long tempDriverId = requestingShift.getDriverId();
        requestingShift.setDriverId(targetShift.getDriverId());
        targetShift.setDriverId(tempDriverId);
        
        assignmentRepository.save(requestingShift);
        assignmentRepository.save(targetShift);
        
        swap.setStatus(ShiftSwapRequest.SwapStatus.APPROVED);
        swap.setApprovedBy(managerId);
        swap.setApprovedAt(LocalDateTime.now());
        
        swap = swapRepository.save(swap);
        log.info("Approved swap request {}", swapId);
        
        kafkaTemplate.send("shift-swap-approved", swap.getRequestingDriverId().toString(), swap);
        return swap;
    }
}
