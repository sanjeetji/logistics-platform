package com.logistics.bff.unified.service.b2b;

import com.logistics.bff.unified.client.b2b.WarehouseServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Warehouse Management Service
 * Business logic for warehouse operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseManagementService {

    private final WarehouseServiceClient warehouseClient;

    /**
     * Get inventory status
     */
    @Cacheable(value = "inventory", key = "#warehouseId")
    public List<Map<String, Object>> getInventory(String warehouseId) {
        log.info("Fetching inventory for warehouse: {}", warehouseId);
        try {
            return warehouseClient.getInventory(warehouseId);
        } catch (Exception e) {
            log.error("Failed to fetch inventory", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get warehouse locations
     */
    @Cacheable(value = "warehouse-locations")
    public List<Map<String, Object>> getLocations() {
        log.info("Fetching all warehouse locations");
        try {
            return warehouseClient.listWarehouses();
        } catch (Exception e) {
            log.error("Failed to fetch warehouses", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get warehouse alerts
     */
    public List<Map<String, Object>> getAlerts() {
        try {
            return warehouseClient.getAlerts();
        } catch (Exception e) {
            log.error("Failed to fetch alerts", e);
            return new ArrayList<>();
        }
    }
}
