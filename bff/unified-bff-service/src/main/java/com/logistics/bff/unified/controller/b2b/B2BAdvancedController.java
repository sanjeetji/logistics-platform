package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.AdvancedFeaturesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Advanced Features Controller
 * Handles advanced operations for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Advanced", description = "Advanced features for B2B clients")
public class B2BAdvancedController {

    private final AdvancedFeaturesService advancedService;

    @GetMapping("/reports/generate")
    @Operation(summary = "Generate report", description = "Generate custom reports")
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestParam String reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Generating report: {}, period: {} to {}", reportType, startDate, endDate);
        return ResponseEntity.ok(advancedService.generateReport(reportType, startDate, endDate));
    }

    @GetMapping("/exports/data")
    @Operation(summary = "Export data", description = "Export data in various formats")
    public ResponseEntity<Map<String, Object>> exportData(
            @RequestParam String dataType,
            @RequestParam(required = false) String format) {
        log.info("Exporting data: {}, format: {}", dataType, format);
        return ResponseEntity.ok(advancedService.exportData(dataType, format));
    }

    @PostMapping("/bulk/operations")
    @Operation(summary = "Bulk operations", description = "Perform bulk operations on multiple entities")
    public ResponseEntity<Map<String, Object>> bulkOperations(@RequestBody Map<String, Object> bulkData) {
        log.info("Processing bulk operation");
        return ResponseEntity.ok(advancedService.bulkOperations(bulkData));
    }

    @GetMapping("/integrations/status")
    @Operation(summary = "Integration status", description = "Get status of external integrations")
    public ResponseEntity<Map<String, Object>> getIntegrationStatus() {
        log.info("Fetching integration status");
        return ResponseEntity.ok(advancedService.getIntegrationStatus());
    }
}
