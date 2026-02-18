package com.logistics.bff.unified.controller.mobile;

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
 * Mobile Customer Profile Controller
 * Handles customer profile operations for mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile", description = "Customer profile management for mobile app")
public class CustomerProfileController {

    private final CustomerServiceClient customerClient;

    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Get customer profile information")
    public ResponseEntity<CustomerDTO> getProfile(@RequestParam String customerId) {
        log.info("Mobile: Fetching customer profile: {}", customerId);
        return ResponseEntity.ok(customerClient.getCustomer(customerId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update customer profile information")
    public ResponseEntity<CustomerDTO> updateProfile(
            @RequestParam String customerId,
            @RequestBody CustomerDTO customerData) {
        log.info("Mobile: Updating customer profile: {}", customerId);
        return ResponseEntity.ok(customerClient.updateCustomer(customerId, customerData));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get addresses", description = "Get all saved addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(@RequestParam String customerId) {
        log.info("Mobile: Fetching addresses for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.getCustomerAddresses(customerId));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add address", description = "Add a new address for customer")
    public ResponseEntity<AddressDTO> addAddress(
            @RequestParam String customerId,
            @RequestBody AddressDTO addressData) {
        log.info("Mobile: Adding new address for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.addCustomerAddress(customerId, addressData));
    }
}
