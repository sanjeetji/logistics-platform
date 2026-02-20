package com.logistics.warehouse.controller;

import com.logistics.warehouse.model.WarehouseOrder;
import com.logistics.warehouse.service.WarehouseFulfillmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse/wms")
@RequiredArgsConstructor
@Slf4j
public class WarehouseWmsController {

    private final WarehouseFulfillmentService fulfillmentService;

    @PostMapping("/orders/{orderId}/start-picking")
    public ResponseEntity<WarehouseOrder> startPicking(@PathVariable String orderId) {
        return ResponseEntity.ok(fulfillmentService.startPicking(orderId));
    }

    @PostMapping("/orders/{orderId}/pick")
    public ResponseEntity<Void> pickItem(
            @PathVariable String orderId,
            @RequestParam String sku,
            @RequestParam Integer quantity,
            @RequestParam Long binId) {
        fulfillmentService.pickItem(orderId, sku, quantity, binId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/pack")
    public ResponseEntity<WarehouseOrder> packOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(fulfillmentService.packOrder(orderId));
    }

    @PostMapping("/orders/{orderId}/ship")
    public ResponseEntity<WarehouseOrder> shipOrder(
            @PathVariable String orderId,
            @RequestParam String performedBy) {
        return ResponseEntity.ok(fulfillmentService.shipOrder(orderId, performedBy));
    }
}
