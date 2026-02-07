package com.logistics.customer.controller;

import com.logistics.customer.dto.AddressRequest;
import com.logistics.customer.dto.CustomerProfileRequest;
import com.logistics.customer.model.Customer;
import com.logistics.customer.model.CustomerAddress;
import com.logistics.customer.service.CustomerService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // ===== Profile Management =====

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Customer>> createProfile(@Valid @RequestBody CustomerProfileRequest request) {
        Customer customer = customerService.createProfile(request);
        return ResponseEntity.ok(ApiResponse.success(customer, "Profile created successfully"));
    }

    @GetMapping("/profile/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> getProfile(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @GetMapping("/profile/user/{userId}")
    public ResponseEntity<ApiResponse<Customer>> getProfileByUserId(@PathVariable Long userId) {
        Customer customer = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @PutMapping("/profile/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> updateProfile(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerProfileRequest request) {
        Customer customer = customerService.updateProfile(customerId, request);
        return ResponseEntity.ok(ApiResponse.success(customer, "Profile updated successfully"));
    }

    // ===== Address Management =====

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<ApiResponse<CustomerAddress>> addAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody AddressRequest request) {
        CustomerAddress address = customerService.addAddress(customerId, request);
        return ResponseEntity.ok(ApiResponse.success(address, "Address added successfully"));
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<ApiResponse<List<CustomerAddress>>> getAddresses(@PathVariable Long customerId) {
        List<CustomerAddress> addresses = customerService.getAddresses(customerId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        customerService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully"));
    }
}
