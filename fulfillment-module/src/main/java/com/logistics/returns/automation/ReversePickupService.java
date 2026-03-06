package com.logistics.returns.automation;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderType;
import com.logistics.order.model.OrderLocation;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.service.OrderService;
import com.logistics.returns.model.ReturnRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Automates the creation of Dispatch Orders from Approved ReturnRequests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReversePickupService {

        private final OrderService orderService;

        public Order scheduleReverseLogistics(ReturnRequest returnRequest) {
                log.info("Scheduling Reverse Logistics Dispatch for Return: {}", returnRequest.getReturnId());

                String newOrderId = "ORD-REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                OrderLocation pickupLocation = OrderLocation.builder()
                                .address(returnRequest.getPickupAddress())
                                .latitude(returnRequest.getPickupLatitude())
                                .longitude(returnRequest.getPickupLongitude())
                                .contactName(returnRequest.getCustomerId())
                                .build();

                // Default Drop to nearest known Primary Hub
                OrderLocation dropLocation = OrderLocation.builder()
                                .address("Central Returns Hub, Industrial Area")
                                .latitude(40.7128) // Example Hub Latitude
                                .longitude(-74.0060) // Example Hub Longitude
                                .build();

                // The actual OrderService typically accepts DTOs but since it is mapped
                // internally
                // We'll mimic the internal construction for direct access
                Order newOrder = Order.builder()
                                .orderId(newOrderId)
                                .externalOrderId(returnRequest.getReturnId())
                                .tenantId("DEFAULT") // Tenant Context logic
                                .type(OrderType.REVERSE_PICKUP)
                                .status(OrderStatus.CREATED)
                                .pickupLocation(pickupLocation)
                                .dropLocation(dropLocation)
                                .weightKg(1.0)
                                .build();

                log.info("Created Dispatch Order {} for Reverse Pickup.", newOrder.getOrderId());

                // Push the order natively through the overarching engine.
                // It triggers dynamic dispatching, status assignments, and ETA modeling
                // organically.
                return orderService.createOrder(newOrder);
        }
}
