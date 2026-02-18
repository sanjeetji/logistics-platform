package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.service.b2c.B2COrderService;
import com.logistics.platform.dto.order.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B2C Order Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Orders", description = "Order management for B2C customers")
public class B2COrderController {

    private final B2COrderService b2cOrderService;

    @PostMapping
    @Operation(summary = "Create order with pricing")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderRequest) {
        log.info("B2C Order creation request received");
        return ResponseEntity.ok(b2cOrderService.createOrderWithPricing(orderRequest));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable String id) {
        log.info("B2C Order cancellation request for: {}", id);
        return ResponseEntity.ok(b2cOrderService.cancelOrder(id));
    }
}
