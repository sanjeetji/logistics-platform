package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.mobile.CustomerServiceClient;
import com.logistics.platform.dto.customer.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile Customer Profile Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Customer", description = "Profile management for customer mobile app")
public class CustomerProfileController {

        private final CustomerServiceClient customerClient;

        @GetMapping("/profile")
        @Operation(summary = "Get profile")
        public ResponseEntity<CustomerDTO> getProfile(@RequestParam String customerId) {
                log.info("Mobile customer profile request: {}", customerId);
                return ResponseEntity.ok(customerClient.getCustomerById(customerId));
        }

        @PutMapping("/profile")
        @Operation(summary = "Update profile")
        public ResponseEntity<CustomerDTO> updateProfile(
                        @RequestParam String customerId,
                        @RequestBody CustomerDTO profileData) {
                log.info("Mobile customer profile update request: {}", customerId);
                return ResponseEntity.ok(customerClient.updateCustomer(customerId, profileData));
        }
}
