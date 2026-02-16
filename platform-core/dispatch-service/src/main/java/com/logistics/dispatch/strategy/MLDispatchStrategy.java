package com.logistics.dispatch.strategy;

import com.logistics.dispatch.client.MLServiceClient;
import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ML_DISPATCH")
@RequiredArgsConstructor
@Slf4j
public class MLDispatchStrategy implements DispatchStrategy {

    private final MLServiceClient mlServiceClient;
    // Potentially inject a service to find candidates (e.g., FleetService or
    // LocationHub)
    // For now, mocking candidate retrieval as per DispatchService example or using
    // a shared method if available.
    // Ideally, we should reuse candidate finding logic. Let's assume we can get
    // candidates.

    @Override
    public boolean dispatch(TransportOrderDto order, DispatchJob job) {
        log.info("Executing ML Dispatch Strategy for order: {}", order.getOrderId());

        try {
            // 1. Get Candidates (Mocking for now, as DispatchService logic is private)
            // In a real scenario, we'd inject a CandidateService.
            List<MLServiceClient.DriverCandidate> candidates = getMockCandidates(order);

            if (candidates.isEmpty()) {
                log.warn("No candidates found for ML dispatch.");
                return false;
            }

            // 2. Call ML Service
            MLServiceClient.DriverMatchingRequest request = MLServiceClient.DriverMatchingRequest.builder()
                    .orderId(order.getOrderId())
                    .pickupLat(order.getPickupLat())
                    .pickupLng(order.getPickupLng())
                    .requiredVehicle("VAN") // Example, should come from order
                    .candidates(candidates)
                    .build();

            MLServiceClient.DriverMatchingResponse response = mlServiceClient.getDriverMatch(request);

            if (response != null && response.getRankedDrivers() != null && !response.getRankedDrivers().isEmpty()) {
                MLServiceClient.ScoredDriver bestDriver = response.getRankedDrivers().get(0);
                log.info("ML Service selected driver {} with score {}", bestDriver.getDriverId(),
                        bestDriver.getScore());

                job.setMatchedDriverId(bestDriver.getDriverId());
                return true;
            } else {
                log.warn("ML Service returned no matches.");
                return false;
            }

        } catch (Exception e) {
            log.error("Error executing ML dispatch strategy: {}", e.getMessage(), e);
            return false;
        }
    }

    private List<MLServiceClient.DriverCandidate> getMockCandidates(TransportOrderDto order) {
        // Simulating candidate retrieval
        List<MLServiceClient.DriverCandidate> candidates = new ArrayList<>();

        candidates.add(MLServiceClient.DriverCandidate.builder()
                .driverId("101")
                .currentLat(order.getPickupLat() + 0.01)
                .currentLng(order.getPickupLng() + 0.01)
                .vehicleType("VAN")
                .rating(4.8)
                .acceptanceRate(0.95)
                .build());

        candidates.add(MLServiceClient.DriverCandidate.builder()
                .driverId("102")
                .currentLat(order.getPickupLat() + 0.05)
                .currentLng(order.getPickupLng() + 0.05)
                .vehicleType("TRUCK")
                .rating(4.5)
                .acceptanceRate(0.80)
                .build());

        return candidates;
    }
}
