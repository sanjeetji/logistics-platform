package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "slot-service")
public interface SlotServiceClient {
    
    @GetMapping("/api/slots/available")
    List<Object> getAvailableSlots(@RequestParam String date, @RequestParam String serviceType);
    
    @PostMapping("/api/slots/book")
    Object bookSlot(@RequestBody Object request);
}
