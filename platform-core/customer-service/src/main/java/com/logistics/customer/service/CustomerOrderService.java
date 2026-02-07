package com.logistics.customer.service;

import com.logistics.customer.dto.CreateOrderRequest;
import com.logistics.customer.model.Customer;
import com.logistics.customer.model.CustomerAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for customer order operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderService {

    private final CustomerService customerService;
    private final RestTemplate restTemplate;

    @Value("${pricing.service.url}")
    private String pricingServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    /**
     * Create order with pricing integration
     */
    @Transactional
    public Map<String, Object> createOrder(Long customerId, CreateOrderRequest request) {
        log.info("Creating order for customer: {}", customerId);

        // 1. Get customer
        Customer customer = customerService.getCustomerById(customerId);

        // 2. Get addresses
        CustomerAddress pickupAddress = customerService.getAddressById(request.getPickupAddressId());
        CustomerAddress dropAddress = customerService.getAddressById(request.getDropAddressId());

        // 3. Get price estimate from pricing service
        Map<String, Object> priceEstimate = getPriceEstimate(
                pickupAddress.getLatitude(), pickupAddress.getLongitude(),
                dropAddress.getLatitude(), dropAddress.getLongitude(),
                request.getVehicleType()
        );

        // 4. Create order in order-service
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("customerId", customer.getUserId().toString());
        orderRequest.put("type", "DELIVERY");
        orderRequest.put("vehicleType", request.getVehicleType());
        
        // Pickup location
        Map<String, Object> pickupLocation = new HashMap<>();
        pickupLocation.put("address", pickupAddress.getAddress());
        pickupLocation.put("latitude", pickupAddress.getLatitude());
        pickupLocation.put("longitude", pickupAddress.getLongitude());
        orderRequest.put("pickupLocation", pickupLocation);
        
        // Drop location
        Map<String, Object> dropLocation = new HashMap<>();
        dropLocation.put("address", dropAddress.getAddress());
        dropLocation.put("latitude", dropAddress.getLatitude());
        dropLocation.put("longitude", dropAddress.getLongitude());
        orderRequest.put("dropLocation", dropLocation);
        
        orderRequest.put("notes", request.getNotes());
        orderRequest.put("weightKg", request.getWeightKg());
        
        // Add pricing info
        orderRequest.put("estimatedPrice", priceEstimate.get("totalPrice"));

        // Call order-service (mock for now)
        log.info("Creating order in order-service: {}", orderRequest);
        // Map<String, Object> orderResponse = restTemplate.postForObject(
        //     orderServiceUrl + "/api/v1/orders",
        //     orderRequest,
        //     Map.class
        // );

        // Mock response
        Map<String, Object> orderResponse = new HashMap<>();
        orderResponse.put("orderId", "ORD-" + System.currentTimeMillis());
        orderResponse.put("status", "CREATED");
        orderResponse.put("priceEstimate", priceEstimate);

        log.info("Order created successfully: {}", orderResponse.get("orderId"));
        return orderResponse;
    }

    /**
     * Get price estimate from pricing service
     */
    private Map<String, Object> getPriceEstimate(
            double pickupLat, double pickupLon,
            double dropLat, double dropLon,
            String vehicleType) {
        
        Map<String, Object> request = new HashMap<>();
        request.put("pickupLatitude", pickupLat);
        request.put("pickupLongitude", pickupLon);
        request.put("dropLatitude", dropLat);
        request.put("dropLongitude", dropLon);
        request.put("vehicleType", vehicleType);

        log.info("Getting price estimate from pricing service");
        
        // Call pricing-service (mock for now)
        // Map<String, Object> response = restTemplate.postForObject(
        //     pricingServiceUrl + "/api/v1/pricing/estimate",
        //     request,
        //     Map.class
        // );

        // Mock response
        Map<String, Object> response = new HashMap<>();
        response.put("estimateId", "EST-" + System.currentTimeMillis());
        response.put("totalPrice", 150.00);
        response.put("distance", 10.5);
        response.put("estimatedTime", 25);

        return response;
    }
}
