package com.logistics.bff.b2b.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.logistics.platform.dto.warehouse.*;

import java.util.List;

@FeignClient(name = "warehouse-service")
public interface WarehouseServiceClient {
    
    @GetMapping("/api/v1/warehouses/{id}")
    WarehouseDTO getWarehouse(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/warehouses")
    List<WarehouseDTO> getWarehouses();
    
    @GetMapping("/api/v1/warehouses/{id}/inventory")
    List<InventoryItemDTO> getInventory(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/warehouses/alerts")
    List<InventoryAlertDTO> getAlerts();
}
