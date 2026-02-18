package com.logistics.bff.unified.client.b2b;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "warehouse-service")
public interface WarehouseServiceClient {
    @GetMapping("/api/v1/warehouses/{id}")
    Map<String, Object> getWarehouse(@PathVariable("id") String id);

    @GetMapping("/api/v1/warehouses")
    List<Map<String, Object>> listWarehouses();

    @GetMapping("/api/v1/warehouses/{id}/inventory")
    List<Map<String, Object>> getInventory(@PathVariable("id") String id);

    @GetMapping("/api/v1/warehouses/alerts")
    List<Map<String, Object>> getAlerts();
}
