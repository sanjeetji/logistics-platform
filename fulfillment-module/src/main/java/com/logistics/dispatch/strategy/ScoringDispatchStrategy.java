package com.logistics.dispatch.strategy;

import com.logistics.dispatch.dto.DriverScore;
import com.logistics.dispatch.engine.DispatchScoringEngine;
import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("STANDARD_DISPATCH")
@RequiredArgsConstructor
public class ScoringDispatchStrategy implements DispatchStrategy {

    private final DispatchScoringEngine scoringEngine;
    private final com.logistics.platform.api.fleet.FleetClient fleetClient;

    @Override
    public boolean dispatch(TransportOrderDto order, DispatchJob job) {
        log.info("Executing Real-time Scoring Dispatch for Order: {}", order.getOrderId());

        try {
            List<DriverLocationDto> candidates = getCandidateDrivers(order);

            if (candidates.isEmpty()) {
                log.warn("No available drivers found within radius for order: {}", order.getOrderId());
                job.setLastErrorMessage("No candidates found in proximity");
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
            log.error("Error in real-time scoring dispatch", e);
            job.setLastErrorMessage("Dispatch Error: " + e.getMessage());
            return false;
        }
    }

    private List<DriverLocationDto> getCandidateDrivers(TransportOrderDto order) {
        log.info("Fetching real-time candidates for order {} near {}, {}", order.getOrderId(), order.getPickupLat(),
                order.getPickupLng());

        try {
            var response = fleetClient.findNearestAvailableDrivers(order.getPickupLat(), order.getPickupLng(), 10000.0);

            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .map(dto -> DriverLocationDto.builder()
                                .driverId(String.valueOf(dto.getId()))
                                .lat(dto.getCurrentLatitude())
                                .lng(dto.getCurrentLongitude())
                                .vehicleType(dto.getVehicleType())
                                .build())
                        .collect(java.util.stream.Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to fetch candidates from fleet-service: {}", e.getMessage());
        }

        return java.util.Collections.emptyList();
    }
}
