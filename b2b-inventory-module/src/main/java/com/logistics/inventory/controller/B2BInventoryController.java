package com.logistics.inventory.controller;

import java.util.Objects;

import com.logistics.inventory.model.InventoryItem;
import com.logistics.inventory.service.B2BInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Real-time inventory management")
public class B2BInventoryController {

    private final B2BInventoryService inventoryService;

    @PostMapping("/init")
    @Operation(summary = "Initialize stock for an SKU")
    public ResponseEntity<InventoryItem> initializeStock(@RequestParam String sku,
            @RequestParam String productId,
            @RequestParam String warehouseId,
            @RequestParam int quantity,
            @RequestParam String location) {
        return ResponseEntity.ok(inventoryService.initializeStock(Objects.requireNonNull(sku), productId, warehouseId,
                quantity, location));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock (Atomic)")
    public ResponseEntity<Boolean> reserveStock(@RequestParam String sku, @RequestParam int quantity) {
        boolean success = inventoryService.reserveStock(sku, quantity);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get current stock level")
    public ResponseEntity<Integer> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getStock(sku));
    }
}
