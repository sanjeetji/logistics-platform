package com.logistics.shift.service;

import com.logistics.fleet.client.MLDemandClient;
import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.VerificationStatus;
import com.logistics.fleet.repository.DriverRepository;
import com.logistics.platform.common.dto.ml.DemandPredictionRequest;
import com.logistics.platform.common.dto.ml.DemandPredictionResponse;
import com.logistics.shift.entity.ShiftAssignment;
import com.logistics.shift.entity.ShiftTemplate;
import com.logistics.shift.repository.ShiftAssignmentRepository;
import com.logistics.shift.repository.ShiftTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartShiftPlanningService {

    private final MLDemandClient demandClient;
    private final DriverRepository driverRepository;
    private final ShiftTemplateRepository templateRepository;
    private final ShiftAssignmentRepository assignmentRepository;
    private final ShiftManagementService shiftManagementService;

    @Transactional
    public List<ShiftAssignment> autoPlanShifts(String region, LocalDate date) {
        log.info("Starting smart shift planning for region: {} on date: {}", region, date);

        // 1. Fetch predicted demand
        DemandPredictionRequest demandRequest = DemandPredictionRequest.builder()
                .region(region)
                .date(date)
                .build();
        DemandPredictionResponse demandResponse = demandClient.predictDemand(demandRequest);
        int requiredDrivers = demandResponse.getPredictedDemand();
        log.info("Predicted demand: {} drivers required", requiredDrivers);

        // 2. Identify compliant (verified) drivers who don't have a shift yet
        List<Driver> eligibleDrivers = driverRepository.findAll().stream() // Simplified, should use a custom query for
                                                                           // efficiency
                .filter(d -> d.getVerificationStatus() == VerificationStatus.VERIFIED)
                .filter(d -> !assignmentRepository.existsByDriverIdAndShiftDate(d.getId(), date))
                .collect(Collectors.toList());

        log.info("Found {} eligible verified drivers for auto-assignment", eligibleDrivers.size());

        // 3. Find an active shift template (e.g., Morning Shift)
        ShiftTemplate template = templateRepository.findByActiveTrue().stream()
                .findFirst() // Just picking the first active one for this implementation
                .orElseThrow(() -> new RuntimeException("No active shift templates found for auto-planning"));

        // 4. Auto-assign drivers up to the required demand or availability limit
        List<ShiftAssignment> newAssignments = new ArrayList<>();
        int assignmentCount = Math.min(requiredDrivers, eligibleDrivers.size());

        for (int i = 0; i < assignmentCount; i++) {
            Driver driver = eligibleDrivers.get(i);
            try {
                ShiftAssignment assignment = shiftManagementService.assignShift(driver.getId(), template.getId(), date);
                newAssignments.add(assignment);
            } catch (Exception e) {
                log.error("Failed to auto-assign shift to driver {}: {}", driver.getId(), e.getMessage());
            }
        }

        log.info("Smart planning completed. Successfully assigned {} drivers.", newAssignments.size());
        return newAssignments;
    }
}
