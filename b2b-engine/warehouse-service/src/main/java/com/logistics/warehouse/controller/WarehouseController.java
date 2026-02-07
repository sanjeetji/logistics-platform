package com.logistics.warehouse.controller;

import com.logistics.warehouse.model.Warehouse;
import com.logistics.warehouse.service.WarehouseAssignmentService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseAssignmentService warehouseAssignmentService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<Warehouse>> assignWarehouse(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        Warehouse warehouse = warehouseAssignmentService.assignWarehouse(latitude, longitude);
        return ResponseEntity.ok(ApiResponse.success(warehouse, "Warehouse assigned"));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAvailableWarehouses() {
        List<Warehouse> warehouses = warehouseAssignmentService.getAvailableWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/{warehouseCode}")
    public ResponseEntity<ApiResponse<Warehouse>> getWarehouse(@PathVariable String warehouseCode) {
        Warehouse warehouse = warehouseAssignmentService.getWarehouseByCode(warehouseCode);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }
}
