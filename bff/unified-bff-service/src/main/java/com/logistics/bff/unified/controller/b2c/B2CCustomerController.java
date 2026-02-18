package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.CustomerServiceClient;
import com.logistics.platform.dto.customer.AddressDTO;
import com.logistics.platform.dto.customer.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B2C Customer Controller
 * Handles customer profile operations for B2C web app
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Customer", description = "Customer profile management for B2C web app")
public class B2CCustomerController {

    private final CustomerServiceClient customerClient;

    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Get customer profile information")
    public ResponseEntity<CustomerDTO> getProfile(@RequestParam String customerId) {
        log.info("Fetching customer profile: {}", customerId);
        return ResponseEntity.ok(customerClient.getCustomer(customerId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update customer profile information")
    public ResponseEntity<CustomerDTO> updateProfile(
            @RequestParam String customerId,
            @RequestBody CustomerDTO customerData) {
        log.info("Updating customer profile: {}", customerId);
        return ResponseEntity.ok(customerClient.updateCustomer(customerId, customerData));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get addresses", description = "Get all saved addresses for customer")
    public ResponseEntity<List<AddressDTO>> getAddresses(@RequestParam String customerId) {
        log.info("Fetching addresses for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.getCustomerAddresses(customerId));
    }
}
