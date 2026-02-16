package com.logistics.platform.api.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {

    @PostMapping("/reserve")
    Boolean reserveStock(@RequestParam("sku") String sku, @RequestParam("quantity") int quantity);
}
