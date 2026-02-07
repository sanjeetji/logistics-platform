package com.logistics.customer.service;

import com.logistics.customer.dto.AddressRequest;
import com.logistics.customer.dto.CustomerProfileRequest;
import com.logistics.customer.model.Customer;
import com.logistics.customer.model.CustomerAddress;
import com.logistics.customer.repository.CustomerAddressRepository;
import com.logistics.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for customer profile and address management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;

    /**
     * Create customer profile
     */
    @Transactional
    public Customer createProfile(CustomerProfileRequest request) {
        log.info("Creating customer profile for user: {}", request.getUserId());

        // Check if customer already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists: " + request.getEmail());
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Customer with phone number already exists: " + request.getPhoneNumber());
        }

        Customer customer = Customer.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        return customerRepository.save(customer);
    }

    /**
     * Get customer by user ID
     */
    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for user: " + userId));
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
    }

    /**
     * Update customer profile
     */
    @Transactional
    public Customer updateProfile(Long customerId, CustomerProfileRequest request) {
        Customer customer = getCustomerById(customerId);

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());

        return customerRepository.save(customer);
    }

    // ===== Address Management =====

    /**
     * Add new address
     */
    @Transactional
    public CustomerAddress addAddress(Long customerId, AddressRequest request) {
        log.info("Adding address for customer: {}", customerId);

        // If this is set as default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddress(customerId);
        }

        CustomerAddress address = CustomerAddress.builder()
                .customerId(customerId)
                .label(request.getLabel())
                .address(request.getAddress())
                .landmark(request.getLandmark())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();

        return addressRepository.save(address);
    }

    /**
     * Get customer addresses
     */
    public List<CustomerAddress> getAddresses(Long customerId) {
        return addressRepository.findByCustomerIdAndActive(customerId, true);
    }

    /**
     * Get address by ID
     */
    public CustomerAddress getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));
    }

    /**
     * Delete address
     */
    @Transactional
    public void deleteAddress(Long addressId) {
        CustomerAddress address = getAddressById(addressId);
        address.setActive(false);
        addressRepository.save(address);
        log.info("Deleted address: {}", addressId);
    }

    /**
     * Unset default address for customer
     */
    private void unsetDefaultAddress(Long customerId) {
        addressRepository.findByCustomerIdAndIsDefaultAndActive(customerId, true, true)
                .ifPresent(address -> {
                    address.setIsDefault(false);
                    addressRepository.save(address);
                });
    }
}
