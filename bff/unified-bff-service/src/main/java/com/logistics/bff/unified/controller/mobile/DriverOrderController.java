package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.service.mobile.MobileOrderService;
import com.logistics.platform.dto.order.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile Driver Order Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/driver/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Orders", description = "Order management for driver mobile app")
public class DriverOrderController {

        private final MobileOrderService mobileOrderService;

        @PostMapping("/{id}/accept")
        @Operation(summary = "Accept order")
        public ResponseEntity<OrderDTO> acceptOrder(@PathVariable String id) {
                log.info("Mobile driver accept order request: {}", id);
                return ResponseEntity.ok(mobileOrderService.acceptOrder(id));
        }

        @PostMapping("/{id}/reject")
        @Operation(summary = "Reject order")
        public ResponseEntity<OrderDTO> rejectOrder(@PathVariable String id) {
                log.info("Mobile driver reject order request: {}", id);
                return ResponseEntity.ok(mobileOrderService.rejectOrder(id));
        }
}
