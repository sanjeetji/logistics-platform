package com.logistics.yard.service;

import com.logistics.dispatch.service.DispatchService;
import com.logistics.platform.dto.warehouse.WarehouseOrderPackedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class YardManagementService {

    private final DispatchService dispatchService;

    /**
     * Coordinate Yard operations when a warehouse item is packed and ready.
     * This mimics scheduling a dock and engaging the Dispatcher.
     */
    public void scheduleDockAndDispatch(WarehouseOrderPackedEvent event) {
        log.info("Received packed event for Order: {}, Warehouse: {}", event.getOrderId(), event.getWarehouseId());

        // 1. Simulate scheduling a dock door based on Region or pseudo-logic
        String assignedDock = assignDockDoor(event.getWarehouseId());
        log.info("Assigned Dock Door [{}] to Order: {}", assignedDock, event.getOrderId());

        // 2. Schedule specific staging area instructions (mock representation)
        // ... (staging operations)

        // 3. Hand over to dispatcher to recruit a driver towards this specialized
        // warehouse/dock
        log.info("Requesting Dispatch Service to initialize assignment for Order: {}", event.getOrderId());
        dispatchService.initiateDispatch(event.getOrderId());
    }

    private String assignDockDoor(Long warehouseId) {
        // Pseudo-random dock assignment algorithm
        String[] docks = { "DOCK-A1", "DOCK-A2", "DOCK-B1", "DOCK-C4" };
        int index = Math.abs(UUID.randomUUID().hashCode()) % docks.length;
        return docks[index];
    }
}
