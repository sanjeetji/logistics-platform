package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.AdvancedFeaturesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2B Advanced Features Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/advanced")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Advanced", description = "Advanced operations for B2B clients")
public class B2BAdvancedController {

        private final AdvancedFeaturesService advancedService;

        @GetMapping("/reports")
        @Operation(summary = "Generate report")
        public ResponseEntity<Map<String, Object>> generateReport(
                        @RequestParam String reportType,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {
                log.info("B2B report generation request: {}", reportType);
                return ResponseEntity.ok(advancedService.generateReport(reportType, startDate, endDate));
        }

        @PostMapping("/export")
        @Operation(summary = "Export data")
        public ResponseEntity<Map<String, Object>> exportData(
                        @RequestParam String dataType,
                        @RequestParam(defaultValue = "CSV") String format) {
                log.info("B2B data export request: {}", dataType);
                return ResponseEntity.ok(advancedService.exportData(dataType, format));
        }

        @PostMapping("/bulk")
        @Operation(summary = "Bulk operations")
        public ResponseEntity<Map<String, Object>> bulkOperations(@RequestBody Map<String, Object> bulkData) {
                log.info("B2B bulk operations request");
                return ResponseEntity.ok(advancedService.bulkOperations(bulkData));
        }

        @GetMapping("/integrations")
        @Operation(summary = "Integration status")
        public ResponseEntity<List<Map<String, Object>>> getIntegrationStatus() {
                log.info("B2B integration status request");
                return ResponseEntity.ok(advancedService.getIntegrationStatus());
        }
}
