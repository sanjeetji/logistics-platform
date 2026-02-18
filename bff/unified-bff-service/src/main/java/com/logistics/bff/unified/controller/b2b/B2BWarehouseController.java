package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.WarehouseManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2B Warehouse Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Warehouse", description = "Warehouse and inventory management for B2B clients")
public class B2BWarehouseController {

        private final WarehouseManagementService warehouseService;

        @GetMapping
        @Operation(summary = "List warehouses")
        public ResponseEntity<List<Map<String, Object>>> getWarehouses() {
                log.info("B2B warehouse list request");
                return ResponseEntity.ok(warehouseService.getLocations());
        }

        @GetMapping("/{id}/inventory")
        @Operation(summary = "Get warehouse inventory")
        public ResponseEntity<List<Map<String, Object>>> getInventory(@PathVariable String id) {
                log.info("B2B warehouse inventory request for: {}", id);
                return ResponseEntity.ok(warehouseService.getInventory(id));
        }

        @GetMapping("/alerts")
        @Operation(summary = "Get warehouse alerts")
        public ResponseEntity<List<Map<String, Object>>> getAlerts() {
                log.info("B2B warehouse alerts request");
                return ResponseEntity.ok(warehouseService.getAlerts());
        }
}
