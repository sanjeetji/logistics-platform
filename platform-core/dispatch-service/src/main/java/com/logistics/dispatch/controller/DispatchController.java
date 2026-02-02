package com.logistics.dispatch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    @PostMapping("/assign/{orderId}")
    public ResponseEntity<String> assignDriver(@PathVariable Long orderId) {
        // Logic to assign a specific driver (B2B)
        return ResponseEntity.ok("Driver assignment process initiated for order: " + orderId);
    }

    @PostMapping("/broadcast/{orderId}")
    public ResponseEntity<String> broadcastOrder(@PathVariable Long orderId) {
        // Logic to broadcast order to nearby drivers (B2C)
        return ResponseEntity.ok("Order broadcast initiated for order: " + orderId);
    }
}
