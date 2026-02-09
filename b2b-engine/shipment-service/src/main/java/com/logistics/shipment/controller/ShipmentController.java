package com.logistics.shipment.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.shipment.model.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Shipment>> createShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.createShipment(shipment)));
    }

    @GetMapping("/{shipmentId}")
    public ResponseEntity<ApiResponse<Shipment>> getShipment(@PathVariable String shipmentId) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.getShipment(shipmentId)));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<Shipment>>> getShipmentsByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.getShipmentsByDriver(driverId)));
    }

    @PatchMapping("/{shipmentId}/status")
    public ResponseEntity<ApiResponse<Shipment>> updateStatus(@PathVariable String shipmentId,
            @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.updateStatus(shipmentId, status)));
    }

    @PostMapping("/{shipmentId}/assign")
    public ResponseEntity<ApiResponse<Shipment>> assignDriver(
            @PathVariable String shipmentId,
            @RequestParam String driverId,
            @RequestParam String vehicleId) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.assignDriver(shipmentId, driverId, vehicleId)));
    }
}
