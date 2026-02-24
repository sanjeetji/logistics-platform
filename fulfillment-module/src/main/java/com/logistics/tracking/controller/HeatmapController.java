package com.logistics.tracking.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.tracking.dto.HeatmapData;
import com.logistics.tracking.service.HeatmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracking/heatmap")
@RequiredArgsConstructor
public class HeatmapController {

    private final HeatmapService heatmapService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HeatmapData>>> getLiveHeatmap() {
        List<HeatmapData> heatmapData = heatmapService.getLiveHeatmap();
        return ResponseEntity.ok(ApiResponse.success(heatmapData, "Heatmap data retrieved successfully"));
    }
}
