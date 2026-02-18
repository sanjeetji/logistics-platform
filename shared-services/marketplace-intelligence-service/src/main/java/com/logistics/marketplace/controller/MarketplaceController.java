package com.logistics.marketplace.controller;

import com.logistics.marketplace.service.DemandPredictionService;
import com.logistics.marketplace.service.MarketTrendAnalyzer;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketTrendAnalyzer trendAnalyzer;
    private final DemandPredictionService predictionService;

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<MarketTrendAnalyzer.MarketTrendDto>> getMarketTrends(
            @RequestParam("zoneId") String zoneId) {
        return ResponseEntity.ok(ApiResponse.success(trendAnalyzer.analyzeTrend(zoneId)));
    }

    @GetMapping("/prediction")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getDemandPrediction(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now().plusDays(1);
        }
        return ResponseEntity.ok(ApiResponse.success(predictionService.predictDemand(date)));
    }
}
