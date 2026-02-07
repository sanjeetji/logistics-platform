package com.logistics.driver.service;

import com.logistics.driver.model.DriverShift;
import com.logistics.driver.model.ShiftStatus;
import com.logistics.driver.repository.DriverShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for driver shift management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverShiftService {

    private final DriverShiftRepository shiftRepository;

    /**
     * Start new shift
     */
    @Transactional
    public DriverShift startShift(Long driverId) {
        log.info("Starting shift for driver: {}", driverId);

        // Check if there's already an active shift
        shiftRepository.findLatestShiftByStatus(driverId, ShiftStatus.ACTIVE)
                .ifPresent(shift -> {
                    throw new RuntimeException("Driver already has an active shift");
                });

        DriverShift shift = DriverShift.builder()
                .driverId(driverId)
                .shiftStart(LocalDateTime.now())
                .build();

        return shiftRepository.save(shift);
    }

    /**
     * End current shift
     */
    @Transactional
    public DriverShift endShift(Long driverId) {
        log.info("Ending shift for driver: {}", driverId);

        DriverShift shift = shiftRepository.findLatestShiftByStatus(driverId, ShiftStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active shift found for driver"));

        shift.setShiftEnd(LocalDateTime.now());
        shift.setStatus(ShiftStatus.COMPLETED);

        return shiftRepository.save(shift);
    }

    /**
     * Get active shift
     */
    public DriverShift getActiveShift(Long driverId) {
        return shiftRepository.findLatestShiftByStatus(driverId, ShiftStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active shift found"));
    }

    /**
     * Update shift stats
     */
    @Transactional
    public void updateShiftStats(Long driverId, BigDecimal earnings, Double distanceKm, boolean orderCompleted) {
        shiftRepository.findLatestShiftByStatus(driverId, ShiftStatus.ACTIVE)
                .ifPresent(shift -> {
                    shift.setTotalOrders(shift.getTotalOrders() + 1);
                    if (orderCompleted) {
                        shift.setCompletedOrders(shift.getCompletedOrders() + 1);
                    }
                    shift.setTotalEarnings(shift.getTotalEarnings().add(earnings));
                    shift.setTotalDistanceKm(shift.getTotalDistanceKm() + distanceKm);
                    shiftRepository.save(shift);
                });
    }

    /**
     * Get shift history
     */
    public List<DriverShift> getShiftHistory(Long driverId) {
        return shiftRepository.findByDriverId(driverId);
    }
}
