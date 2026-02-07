package com.logistics.warehouse.service;

import com.logistics.warehouse.model.Warehouse;
import com.logistics.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for warehouse assignment based on proximity
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseAssignmentService {

    private final WarehouseRepository warehouseRepository;

    /**
     * Assign warehouse based on proximity to pickup location
     */
    public Warehouse assignWarehouse(Double pickupLatitude, Double pickupLongitude) {
        log.info("Finding nearest warehouse to location: {}, {}", pickupLatitude, pickupLongitude);

        List<Warehouse> nearestWarehouses = warehouseRepository.findNearestWarehouses(pickupLatitude, pickupLongitude);

        if (nearestWarehouses.isEmpty()) {
            throw new RuntimeException("No active warehouses found");
        }

        // Find first warehouse with available capacity
        for (Warehouse warehouse : nearestWarehouses) {
            if (warehouse.getAvailableCapacity() > 0) {
                log.info("Assigned warehouse: {}", warehouse.getWarehouseCode());
                return warehouse;
            }
        }

        // If no warehouse has capacity, return nearest one anyway
        log.warn("No warehouse with available capacity, returning nearest");
        return nearestWarehouses.get(0);
    }

    /**
     * Get all available warehouses
     */
    public List<Warehouse> getAvailableWarehouses() {
        return warehouseRepository.findAvailableWarehouses();
    }

    /**
     * Get warehouse by code
     */
    public Warehouse getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByWarehouseCode(warehouseCode)
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + warehouseCode));
    }
}
