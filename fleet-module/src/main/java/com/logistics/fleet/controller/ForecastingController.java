package com.logistics.fleet.controller;

import com.logistics.fleet.dto.FleetForecastDTO;
import com.logistics.fleet.service.CapacityForecastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/fleet/forecasting")
@RequiredArgsConstructor
public class ForecastingController {

    private final CapacityForecastingService forecastingService;

    @GetMapping("/gap-analysis")
    public ResponseEntity<FleetForecastDTO> getGapAnalysis(
            @RequestParam String region,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(forecastingService.getForecast(region, date));
    }
}
