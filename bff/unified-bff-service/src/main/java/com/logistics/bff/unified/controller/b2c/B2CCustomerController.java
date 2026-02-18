package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.b2c.CustomerServiceClient;
import com.logistics.platform.dto.customer.CustomerDTO;
import com.logistics.platform.dto.customer.AddressDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B2C Customer Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Customer", description = "Customer profile management for B2C web app")
public class B2CCustomerController {

    private final CustomerServiceClient customerClient;

    @GetMapping("/profile")
    @Operation(summary = "Get profile")
    public ResponseEntity<CustomerDTO> getProfile(@RequestParam String customerId) {
        log.info("B2C profile request for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.getCustomerById(customerId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update profile")
    public ResponseEntity<CustomerDTO> updateProfile(
            @RequestParam String customerId,
            @RequestBody CustomerDTO customerData) {
        log.info("B2C profile update request for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.updateCustomer(customerId, customerData));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(@RequestParam String customerId) {
        log.info("B2C addresses request for customer: {}", customerId);
        return ResponseEntity.ok(customerClient.getAddresses(customerId));
    }
}
