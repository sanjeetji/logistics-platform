package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.event.OrderEventProducer;
import com.logistics.order.mappers.OrderMapper;
import com.logistics.platform.common.dto.event.OrderCreatedEvent;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;
    private final OrderStateMachineService stateMachineService;
    private final OrderValidationService validationService;
    private final OrderHistoryService historyService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Transactional
    public Order createOrder(Order order) {
        // Validate order creation
        validationService.validateOrderCreation(order);

        if (order.getOrderId() == null) {
            order.setOrderId(UUID.randomUUID().toString());
        }

        // Set initial status
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CREATED);
        }

        Order savedOrder = orderRepository.save(order);

        // Record initial status in history
        historyService.recordStatusChange(
                savedOrder.getOrderId(),
                null,
                OrderStatus.CREATED,
                "SYSTEM",
                "Order created",
                null,
                null);

        // Publish Event
        try {
            TransportOrderDto orderDto = orderMapper.toDto(savedOrder);
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(savedOrder.getOrderId())
                    .orderDto(orderDto)
                    .timestamp(LocalDateTime.now())
                    .build();
            orderEventProducer.publishOrderCreated(event);
        } catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent for order {}: {}", savedOrder.getOrderId(), e.getMessage());
        }

        return savedOrder;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderStatus previousStatus = order.getStatus();

        // Validate state transition
        stateMachineService.validateTransition(previousStatus, newStatus);

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // Record status change in history
        historyService.recordStatusChange(
                savedOrder.getOrderId(),
                previousStatus,
                newStatus,
                "SYSTEM",
                "Status updated",
                null,
                null);

        return savedOrder;
    }

    @Transactional
    public Order assignDriver(String orderId, String driverId, String vehicleId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Validate assignment
        validationService.validateDriverAssignment(order, driverId, vehicleId);

        OrderStatus previousStatus = order.getStatus();

        // Assign driver and vehicle
        order.setDriverId(driverId);
        order.setVehicleId(vehicleId);
        order.setAssignedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.ASSIGNED);

        Order savedOrder = orderRepository.save(order);

        // Record status change
        historyService.recordStatusChange(
                orderId,
                previousStatus,
                OrderStatus.ASSIGNED,
                "SYSTEM",
                "Driver assigned: " + driverId,
                null,
                null);

        log.info("Assigned driver {} and vehicle {} to order {}", driverId, vehicleId, orderId);
        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(String orderId, String reason) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Validate cancellation
        validationService.validateCancellation(order, reason);

        OrderStatus previousStatus = order.getStatus();

        // Cancel order
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelledAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Record status change
        historyService.recordStatusChange(
                orderId,
                previousStatus,
                OrderStatus.CANCELLED,
                "SYSTEM",
                reason,
                null,
                null);

        log.info("Cancelled order {} with reason: {}", orderId, reason);
        return savedOrder;
    }

    @Transactional
    public Order markPickedUp(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Validate pickup
        validationService.validatePickup(order);

        OrderStatus previousStatus = order.getStatus();

        // Mark as picked up
        order.setStatus(OrderStatus.PICKED_UP);
        order.setActualPickupTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Record status change
        historyService.recordStatusChange(
                orderId,
                previousStatus,
                OrderStatus.PICKED_UP,
                "SYSTEM",
                "Order picked up",
                order.getPickupLocation().getLatitude(),
                order.getPickupLocation().getLongitude());

        log.info("Order {} marked as picked up", orderId);
        return savedOrder;
    }

    @Transactional
    public Order markInTransit(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OrderStatus previousStatus = order.getStatus();
        stateMachineService.validateTransition(previousStatus, OrderStatus.IN_TRANSIT);

        order.setStatus(OrderStatus.IN_TRANSIT);
        Order savedOrder = orderRepository.save(order);

        // Record status change
        historyService.recordStatusChange(
                orderId,
                previousStatus,
                OrderStatus.IN_TRANSIT,
                "SYSTEM",
                "Order in transit",
                null,
                null);

        log.info("Order {} marked as in transit", orderId);
        return savedOrder;
    }

    @Transactional
    public Order markDelivered(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Validate delivery
        validationService.validateDelivery(order);

        OrderStatus previousStatus = order.getStatus();

        // Mark as delivered
        order.setStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Record status change
        historyService.recordStatusChange(
                orderId,
                previousStatus,
                OrderStatus.DELIVERED,
                "SYSTEM",
                "Order delivered",
                order.getDropLocation().getLatitude(),
                order.getDropLocation().getLongitude());

        log.info("Order {} marked as delivered", orderId);
        return savedOrder;
    }
}
