package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.client.WarehouseServiceClient;
import com.logistics.bff.unified.service.WarehouseManagementService;
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
 * Handles warehouse and inventory operations for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/warehouse")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Warehouse", description = "Warehouse and inventory management for B2B clients")
public class B2BWarehouseController {

    private final WarehouseServiceClient warehouseClient;
    private final WarehouseManagementService warehouseService;

    @GetMapping("/inventory")
    @Operation(summary = "Get inventory", description = "Get current inventory status")
    public ResponseEntity<Map<String, Object>> getInventory(
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String category) {
        log.info("Fetching inventory - warehouse: {}, category: {}", warehouseId, category);
        return ResponseEntity.ok(warehouseService.getInventory(warehouseId, category));
    }

    @GetMapping("/locations")
    @Operation(summary = "Get locations", description = "Get all warehouse locations")
    public ResponseEntity<List<Map<String, Object>>> getLocations() {
        log.info("Fetching warehouse locations");
        return ResponseEntity.ok(warehouseService.getLocations());
    }

    @PostMapping("/stock")
    @Operation(summary = "Update stock", description = "Update stock levels for items")
    public ResponseEntity<Map<String, Object>> updateStock(@RequestBody Map<String, Object> stockData) {
        log.info("Updating stock levels");
        return ResponseEntity.ok(warehouseService.updateStock(stockData));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get movements", description = "Get stock movement history")
    public ResponseEntity<List<Map<String, Object>>> getStockMovements(
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching stock movements for warehouse: {}", warehouseId);
        return ResponseEntity.ok(warehouseService.getStockMovements(warehouseId, startDate, endDate));
    }
}
