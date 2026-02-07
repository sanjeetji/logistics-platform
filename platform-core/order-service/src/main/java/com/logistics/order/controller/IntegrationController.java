package com.logistics.order.controller;

import com.logistics.order.dto.StandardExternalOrderRequest;
import com.logistics.order.model.Order;
import com.logistics.order.model.OrderLocation;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderType;
import com.logistics.order.service.OrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integration")
@RequiredArgsConstructor
@Slf4j
public class IntegrationController {

    private final OrderService orderService;

    /**
     * STANDARD API: For custom platforms or generic integrations.
     * POST /api/v1/integration/orders
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<String>> ingestStandardOrder(@RequestBody StandardExternalOrderRequest request) {
        log.info("Received Standard External Order: {}", request);
        processOrder(request);
        return ResponseEntity.ok(ApiResponse.success("Order received via Standard API"));
    }

    /**
     * SHOPIFY ADAPTER: Webhook for Shopify Order Creation
     * POST /api/v1/integration/shopify/webhook
     */
    @PostMapping("/shopify/webhook")
    public ResponseEntity<ApiResponse<String>> handleShopifyWebhook(
            @RequestHeader(value = "X-Shopify-Topic", required = false) String topic,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmac,
            @RequestBody Map<String, Object> payload) {

        log.info("Received Shopify Webhook. Topic: {}", topic);

        // Adapter Logic: Map Shopify JSON -> StandardExternalOrderRequest
        StandardExternalOrderRequest request = mapShopifyToStandard(payload);
        processOrder(request);

        return ResponseEntity.ok(ApiResponse.success("Shopify Webhook processed"));
    }

    /**
     * WOOCOMMERCE ADAPTER: Webhook for WooCommerce Order Creation
     * POST /api/v1/integration/woocommerce/webhook
     */
    @PostMapping("/woocommerce/webhook")
    public ResponseEntity<ApiResponse<String>> handleWooCommerceWebhook(
            @RequestHeader(value = "X-WC-Webhook-Topic", required = false) String topic,
            @RequestBody Map<String, Object> payload) {

        log.info("Received WooCommerce Webhook. Topic: {}", topic);

        // Adapter Logic: Map WooCommerce JSON -> StandardExternalOrderRequest
        StandardExternalOrderRequest request = mapWooToStandard(payload);
        processOrder(request);

        return ResponseEntity.ok(ApiResponse.success("WooCommerce Webhook processed"));
    }

    // --- Internal Processing & Adapters ---

    private void processOrder(StandardExternalOrderRequest request) {
        log.info("PROCESSING ORDER -> Platform: {}, ExtID: {}, Amount: {} {}",
                request.getPlatform(), request.getExternalOrderId(), request.getTotalPrice(), request.getCurrency());

        // 1. Create Drop Location from Request
        OrderLocation dropLocation = new OrderLocation();
        dropLocation.setAddress(request.getShippingAddress() + ", " + request.getCity() + ", " + request.getZipCode());
        dropLocation.setContactName(request.getCustomerName());
        dropLocation.setContactPhone(request.getCustomerPhone());
        dropLocation.setLatitude(request.getLatitude() != null ? request.getLatitude() : 0.0); // Default if missing
        dropLocation.setLongitude(request.getLongitude() != null ? request.getLongitude() : 0.0);

        // 2. Determine Pickup Location
        // Logic: specific merchant configuration > payload location > default warehouse
        OrderLocation pickupLocation = new OrderLocation();
        pickupLocation.setAddress("Central Warehouse, Logistics Hub");
        pickupLocation.setContactName("Warehouse Manager");
        pickupLocation.setLatitude(12.9716); // Example coords
        pickupLocation.setLongitude(77.5946);

        // 3. Build Order Entity
        Order order = Order.builder()
                .customerId("guest_" + request.getCustomerEmail()) // Simple guest ID generation
                .type(OrderType.B2C_ON_DEMAND) // Corrected Enum
                .status(OrderStatus.CREATED)
                .price(request.getTotalPrice())
                .pickupLocation(pickupLocation)
                .dropLocation(dropLocation)
                .metadata("{\"source\": \"" + request.getPlatform() + "\", \"externalId\": \""
                        + request.getExternalOrderId() + "\"}")
                .build();

        // 4. Save to DB
        try {
            Order savedOrder = orderService.createOrder(order);
            log.info("Successfully created internal order: {}", savedOrder.getOrderId());
        } catch (Exception e) {
            log.error("Failed to create order from integration: {}", e.getMessage(), e);
            throw new RuntimeException("Integration Order Creation Failed", e);
        }
    }

    private StandardExternalOrderRequest mapShopifyToStandard(Map<String, Object> payload) {
        String id = String.valueOf(payload.get("id"));
        String email = (String) payload.getOrDefault("email", "unknown@shopify.com");
        String price = (String) payload.getOrDefault("total_price", "0.00");
        String currency = (String) payload.getOrDefault("currency", "USD");

        // Extract Customer Details
        String customerName = "Guest User";
        if (payload.get("customer") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> customer = (Map<String, Object>) payload.get("customer");
            customerName = customer.getOrDefault("first_name", "") + " " + customer.getOrDefault("last_name", "");
        }

        // Extract Shipping Details
        String address = "No Address Provided";
        String city = "Unknown";
        String zip = "000000";

        if (payload.get("shipping_address") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> shipping = (Map<String, Object>) payload.get("shipping_address");
            address = (String) shipping.getOrDefault("address1", address);
            city = (String) shipping.getOrDefault("city", city);
            zip = (String) shipping.getOrDefault("zip", zip);
        }

        return StandardExternalOrderRequest.builder()
                .platform("SHOPIFY")
                .externalOrderId(id)
                .customerEmail(email)
                .customerName(customerName.trim())
                .shippingAddress(address)
                .city(city)
                .zipCode(zip)
                .totalPrice(new BigDecimal(price))
                .currency(currency)
                .build();
    }

    private StandardExternalOrderRequest mapWooToStandard(Map<String, Object> payload) {
        String id = String.valueOf(payload.get("id"));
        String total = (String) payload.getOrDefault("total", "0.00");
        String currency = (String) payload.getOrDefault("currency", "USD");

        // WooCommerce puts everything in "billing" or "shipping" objects
        @SuppressWarnings("unchecked")
        Map<String, Object> billing = (Map<String, Object>) payload.getOrDefault("billing", Collections.emptyMap());
        @SuppressWarnings("unchecked")
        Map<String, Object> shipping = (Map<String, Object>) payload.getOrDefault("shipping", Collections.emptyMap());

        String email = (String) billing.getOrDefault("email", "unknown@woo.com");
        String firstName = (String) billing.getOrDefault("first_name", "Guest");
        String lastName = (String) billing.getOrDefault("last_name", "User");

        String address = (String) shipping.getOrDefault("address_1", "No Address");
        String city = (String) shipping.getOrDefault("city", "Unknown");
        String zip = (String) shipping.getOrDefault("postcode", "00000");

        return StandardExternalOrderRequest.builder()
                .platform("WOOCOMMERCE")
                .externalOrderId(id)
                .customerEmail(email)
                .customerName((firstName + " " + lastName).trim())
                .shippingAddress(address)
                .city(city)
                .zipCode(zip)
                .totalPrice(new BigDecimal(total))
                .currency(currency)
                .build();
    }
}
