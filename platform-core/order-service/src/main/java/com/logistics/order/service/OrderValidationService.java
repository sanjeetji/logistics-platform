package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderValidationService {

    /**
     * Validates order creation request
     */
    public void validateOrderCreation(Order order) {
        // Validate customer ID
        if (order.getCustomerId() == null || order.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        // Validate order type
        if (order.getType() == null) {
            throw new IllegalArgumentException("Order type is required");
        }

        // Validate pickup location
        if (order.getPickupLocation() == null) {
            throw new IllegalArgumentException("Pickup location is required");
        }
        validateLocation(order.getPickupLocation().getLatitude(),
                order.getPickupLocation().getLongitude(), "Pickup");

        // Validate drop location
        if (order.getDropLocation() == null) {
            throw new IllegalArgumentException("Drop location is required");
        }
        validateLocation(order.getDropLocation().getLatitude(),
                order.getDropLocation().getLongitude(), "Drop");

        // Validate weight
        if (order.getWeightKg() != null && order.getWeightKg() <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }

        // Validate price
        if (order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    /**
     * Validates driver assignment
     */
    public void validateDriverAssignment(Order order, String driverId, String vehicleId) {
        // Order must be in CREATED status
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Order must be in CREATED status to assign driver. Current status: " + order.getStatus());
        }

        // Driver ID is required
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("Driver ID is required");
        }

        // Vehicle ID is required
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }

        // Order should not already be assigned
        if (order.getDriverId() != null) {
            throw new IllegalStateException("Order is already assigned to driver: " + order.getDriverId());
        }
    }

    /**
     * Validates order cancellation
     */
    public void validateCancellation(Order order, String reason) {
        // Cannot cancel delivered orders
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }

        // Cannot cancel already cancelled orders
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        // Reason is required
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }

    /**
     * Validates pickup action
     */
    public void validatePickup(Order order) {
        // Must be in ASSIGNED status
        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Order must be in ASSIGNED status for pickup. Current status: " + order.getStatus());
        }

        // Must have driver assigned
        if (order.getDriverId() == null) {
            throw new IllegalStateException("No driver assigned to this order");
        }
    }

    /**
     * Validates delivery action
     */
    public void validateDelivery(Order order) {
        // Must be in IN_TRANSIT status
        if (order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new IllegalStateException(
                    "Order must be in IN_TRANSIT status for delivery. Current status: " + order.getStatus());
        }
    }

    /**
     * Validates geographic coordinates
     */
    private void validateLocation(Double latitude, Double longitude, String locationType) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(locationType + " coordinates are required");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(locationType + " latitude must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(locationType + " longitude must be between -180 and 180");
        }
    }
}
