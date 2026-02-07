package com.logistics.warehouse.controller;

import com.logistics.warehouse.dto.StockOperationRequest;
import com.logistics.warehouse.model.InventoryItem;
import com.logistics.warehouse.service.InventoryService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/stock-in")
    public ResponseEntity<ApiResponse<InventoryItem>> addStock(@Valid @RequestBody StockOperationRequest request) {
        InventoryItem item = inventoryService.addStock(
                request.getWarehouseId(),
                request.getSku(),
                request.getQuantity(),
                request.getReason(),
                request.getPerformedBy());
        return ResponseEntity.ok(ApiResponse.success(item, "Stock added successfully"));
    }

    @PostMapping("/stock-out")
    public ResponseEntity<ApiResponse<InventoryItem>> removeStock(@Valid @RequestBody StockOperationRequest request) {
        InventoryItem item = inventoryService.removeStock(
                request.getWarehouseId(),
                request.getSku(),
                request.getQuantity(),
                request.getOrderId(),
                request.getReason(),
                request.getPerformedBy());
        return ResponseEntity.ok(ApiResponse.success(item, "Stock removed successfully"));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryItem>> reserveStock(@Valid @RequestBody StockOperationRequest request) {
        InventoryItem item = inventoryService.reserveStock(
                request.getWarehouseId(),
                request.getSku(),
                request.getQuantity(),
                request.getOrderId());
        return ResponseEntity.ok(ApiResponse.success(item, "Stock reserved"));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<InventoryItem>> releaseReservation(
            @Valid @RequestBody StockOperationRequest request) {
        InventoryItem item = inventoryService.releaseReservation(
                request.getWarehouseId(),
                request.getSku(),
                request.getQuantity(),
                request.getOrderId());
        return ResponseEntity.ok(ApiResponse.success(item, "Reservation released"));
    }

    @GetMapping("/{warehouseId}/inventory")
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getWarehouseInventory(@PathVariable Long warehouseId) {
        List<InventoryItem> items = inventoryService.getWarehouseInventory(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getLowStockItems(
            @RequestParam(required = false) Long warehouseId) {
        List<InventoryItem> items = inventoryService.getLowStockItems(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
}
