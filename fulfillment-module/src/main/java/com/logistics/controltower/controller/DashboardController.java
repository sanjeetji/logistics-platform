package com.logistics.controltower.controller;

import com.logistics.controltower.model.DashboardMetric;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/snapshot")
    public ResponseEntity<List<DashboardMetric>> getSnapshot() {
        // In a real implementation, this would fetch aggregation from Redis/DB
        // For now, returning a mock snapshot for connectivity testing
        return ResponseEntity.ok(List.of(
                DashboardMetric.builder()
                        .metricName("SYSTEM_STATUS")
                        .value("ONLINE")
                        .unit("STATUS")
                        .timestamp(LocalDateTime.now())
                        .tags(Map.of("service", "control-tower"))
                        .build()));
    }
}
