package com.logistics.dispatch.strategy;

import com.logistics.dispatch.dto.DriverScore;
import com.logistics.dispatch.engine.DispatchScoringEngine;
import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("STANDARD_DISPATCH")
@RequiredArgsConstructor
public class ScoringDispatchStrategy implements DispatchStrategy {

    private final DispatchScoringEngine scoringEngine;

    @Override
    public boolean dispatch(TransportOrderDto order, DispatchJob job) {
        log.info("Executing Standard Scoring Dispatch for Order: {}", order.getOrderId());

        try {
            // Mock candidate fetching for now (moved from DispatchService)
            List<DriverLocationDto> candidates = getCandidateDrivers(order);

            if (candidates.isEmpty()) {
                log.warn("No available drivers found for order: {}", order.getOrderId());
                job.setLastErrorMessage("No candidates found");
                return false;
            }

            List<DriverScore> scoredDrivers = scoringEngine.scoreDrivers(order, candidates);

            if (scoredDrivers.isEmpty()) {
                log.warn("No suitable drivers found after scoring for order: {}", order.getOrderId());
                job.setLastErrorMessage("No suitable drivers after scoring");
                return false;
            }

            DriverScore bestDriver = scoredDrivers.get(0);
            log.info("Best driver found: {} with score: {}", bestDriver.getDriverId(), bestDriver.getScore());

            job.setMatchedDriverId(String.valueOf(bestDriver.getDriverId()));
            job.setStatus(com.logistics.dispatch.model.DispatchStatus.ASSIGNED);

            return true;

        } catch (Exception e) {
            log.error("Error in scoring dispatch", e);
            job.setLastErrorMessage("Scoring Error: " + e.getMessage());
            return false;
        }
    }

    private List<DriverLocationDto> getCandidateDrivers(TransportOrderDto order) {
        // This logic was previously in DispatchService.getCandidateDrivers
        // For now, retaining the mock implementation.
        List<DriverLocationDto> candidates = new ArrayList<>();

        DriverLocationDto driver1 = new DriverLocationDto();
        driver1.setDriverId("101");
        driver1.setLat(order.getPickupLat() + 0.01);
        driver1.setLng(order.getPickupLng() + 0.01);
        driver1.setVehicleType("VAN");
        candidates.add(driver1);

        DriverLocationDto driver2 = new DriverLocationDto();
        driver2.setDriverId("102");
        driver2.setLat(order.getPickupLat() + 0.05);
        driver2.setLng(order.getPickupLng() + 0.05);
        driver2.setVehicleType("TRUCK");
        candidates.add(driver2);

        return candidates;
    }
}
