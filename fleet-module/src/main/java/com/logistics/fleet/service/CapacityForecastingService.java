package com.logistics.fleet.service;

import com.logistics.fleet.client.MLDemandClient;
import com.logistics.fleet.dto.FleetForecastDTO;
import com.logistics.platform.common.dto.ml.DemandPredictionRequest;
import com.logistics.platform.common.dto.ml.DemandPredictionResponse;
import com.logistics.shift.repository.ShiftAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CapacityForecastingService {

    private final MLDemandClient demandClient;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    public FleetForecastDTO getForecast(String region, LocalDate date) {
        log.info("Generating fleet forecast for region: {} on date: {}", region, date);

        // 1. Get Predicted Demand from ML Service
        DemandPredictionRequest demandRequest = DemandPredictionRequest.builder()
                .region(region)
                .date(date)
                .historicalDays(30)
                .build();

        DemandPredictionResponse demandResponse = demandClient.predictDemand(demandRequest);
        Integer predictedDemand = demandResponse.getPredictedDemand();

        // 2. Get Scheduled Capacity (count of shifts for that date and region)
        // Note: For simplicity, we assume shifts are region-linked. In a complex app,
        // we'd join with Team/Hub.
        long scheduledCapacity = shiftAssignmentRepository.countByShiftDate(date);

        long gap = scheduledCapacity - predictedDemand;
        String status = gap >= 0 ? (gap > 10 ? "SURPLUS" : "BALANCED") : "DEFICIT";

        String recommendation = generateRecommendation(status, Math.abs(gap));

        return FleetForecastDTO.builder()
                .region(region)
                .date(date)
                .predictedDemand(predictedDemand)
                .scheduledCapacity(scheduledCapacity)
                .capacityGap(gap)
                .status(status)
                .recommendation(recommendation)
                .metadata(new HashMap<>(demandResponse.getFactors()))
                .build();
    }

    private String generateRecommendation(String status, long gapAmount) {
        switch (status) {
            case "DEFICIT":
                return String.format("Alert: Expected deficit of %d drivers. Consider opening %d additional gig slots.",
                        gapAmount, gapAmount + 5);
            case "SURPLUS":
                return String.format(
                        "Surplus of %d drivers detected. Consider offering early checkout or re-routing to adjacent regions.",
                        gapAmount);
            default:
                return "Capacity matches predicted demand. No action required.";
        }
    }
}
