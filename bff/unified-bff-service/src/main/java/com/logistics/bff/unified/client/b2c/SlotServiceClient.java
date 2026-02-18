package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "slot-service")
public interface SlotServiceClient {
    @GetMapping("/api/slots/available")
    List<Object> getAvailableSlots(@RequestParam String date, @RequestParam String location);

    @PostMapping("/api/slots/book")
    Object bookSlot(@RequestParam String userId, @RequestParam String slotId);
}
