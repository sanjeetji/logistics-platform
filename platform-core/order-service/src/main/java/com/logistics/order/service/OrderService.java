package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.event.OrderEventProducer;
import com.logistics.order.mappers.OrderMapper;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import com.logistics.platform.common.dto.order.TransportOrderDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
        private final OrderEventStore eventStore;
        private final NotificationService notificationService;

        public List<Order> getAllOrders() {
                return orderRepository.findAll();
        }

        @Cacheable(value = "orders", key = "#id")
        public Optional<Order> getOrderById(Long id) {
                return orderRepository.findById(id);
        }

        @Cacheable(value = "orders_by_id", key = "#orderId")
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
                        eventStore.saveEvent(savedOrder.getOrderId(), event);

                        // Publish Audit Log
                        com.logistics.platform.event.dto.AuditLogEvent auditEvent = com.logistics.platform.event.dto.AuditLogEvent
                                        .builder()
                                        .entityId(savedOrder.getOrderId())
                                        .entityType("ORDER")
                                        .action("CREATE")
                                        .changedBy("SYSTEM") // Should be from SecurityContext
                                        .tenantId(savedOrder.getTenantId())
                                        .newValue("Order Created")
                                        .timestamp(LocalDateTime.now())
                                        .build();
                        orderEventProducer.publishAuditLog(auditEvent);

                        // Async notification
                        notificationService.sendOrderConfirmation(savedOrder);
                } catch (Exception e) {
                        log.error("Failed to publish OrderCreatedEvent for order {}: {}", savedOrder.getOrderId(),
                                        e.getMessage());
                }

                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        // Note: Ideally we should use specific keys, but since we map by both ID and
        // OrderID, simple invalidation is safer for now.
        // Optimization: @CacheEvict(value = "orders", key = "#orderId") and another for
        // internal ID if possible.
        // For now, evicting all related cache entries or specifically targeted ones if
        // we can resolve the other ID.
        // Given we don't have the internal ID easily for the "orders" cache without
        // fetching, we might need a custom strategy or just accept cache inconsistency
        // for a moment until TTL or use 'allEntries' for safety if volume allows.
        // Better approach: We fetch the order first anyway.
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

                // Save event for sourcing
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                savedOrder.getOrderId(),
                                                previousStatus.name(),
                                                newStatus.name());
                eventStore.saveEvent(savedOrder.getOrderId(), event);
                orderEventProducer.publishOrderStatusChanged(event);

                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
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

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.ASSIGNED.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Assigned driver {} and vehicle {} to order {}", driverId, vehicleId, orderId);
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
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

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.CANCELLED.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Cancelled order {} with reason: {}", orderId, reason);
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
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
                                order.getPickupLocation() != null ? order.getPickupLocation().getLatitude() : null,
                                order.getPickupLocation() != null ? order.getPickupLocation().getLongitude() : null);

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.PICKED_UP.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Order {} marked as picked up", orderId);
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
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

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.IN_TRANSIT.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Order {} marked as in transit", orderId);
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
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
                                order.getDropLocation() != null ? order.getDropLocation().getLatitude() : null,
                                order.getDropLocation() != null ? order.getDropLocation().getLongitude() : null);

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.DELIVERED.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Order {} marked as delivered", orderId);
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order markDelivered(String orderId, String photoUrl) {
                Order order = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                // Validate delivery
                validationService.validateDelivery(order);

                OrderStatus previousStatus = order.getStatus();

                // Mark as delivered
                order.setStatus(OrderStatus.DELIVERED);
                order.setActualDeliveryTime(LocalDateTime.now());

                // Set photo proof if provided (for safe drop deliveries)
                if (photoUrl != null && !photoUrl.isBlank()) {
                        order.setSafeDropPhotoUrl(photoUrl);
                }

                Order savedOrder = orderRepository.save(order);

                // Record status change
                historyService.recordStatusChange(
                                orderId,
                                previousStatus,
                                OrderStatus.DELIVERED,
                                "SYSTEM",
                                "Order delivered" + (photoUrl != null ? " with photo proof" : ""),
                                order.getDropLocation() != null ? order.getDropLocation().getLatitude() : null,
                                order.getDropLocation() != null ? order.getDropLocation().getLongitude() : null);

                // Save event
                com.logistics.platform.event.dto.OrderStatusChangedEvent event = com.logistics.platform.event.dto.OrderStatusChangedEvent
                                .create(
                                                orderId,
                                                previousStatus.name(),
                                                OrderStatus.DELIVERED.name());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishOrderStatusChanged(event);

                log.info("Order {} marked as delivered{}", orderId, photoUrl != null ? " with photo proof" : "");
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order updateDeliveryPreferences(String orderId,
                        com.logistics.order.dto.DeliveryPreferencesRequest request) {
                // Validate preferences
                validationService.validateDeliveryPreferences(request);

                // Find order
                Order order = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                // Update preferences
                if (request.getDeliveryInstructions() != null) {
                        order.setDeliveryInstructions(request.getDeliveryInstructions());
                }
                if (request.getContactlessDelivery() != null) {
                        order.setContactlessDelivery(request.getContactlessDelivery());
                }
                if (request.getPreferredDeliveryTimeStart() != null) {
                        order.setPreferredDeliveryTimeStart(request.getPreferredDeliveryTimeStart());
                }
                if (request.getPreferredDeliveryTimeEnd() != null) {
                        order.setPreferredDeliveryTimeEnd(request.getPreferredDeliveryTimeEnd());
                }
                if (request.getSafeDropLocation() != null) {
                        order.setSafeDropLocation(request.getSafeDropLocation());
                }

                Order savedOrder = orderRepository.save(order);

                // Publish event
                com.logistics.platform.event.dto.DeliveryPreferencesUpdatedEvent event = com.logistics.platform.event.dto.DeliveryPreferencesUpdatedEvent
                                .create(
                                                orderId,
                                                request.getDeliveryInstructions(),
                                                request.getContactlessDelivery(),
                                                request.getPreferredDeliveryTimeStart(),
                                                request.getPreferredDeliveryTimeEnd(),
                                                request.getSafeDropLocation());
                eventStore.saveEvent(orderId, event);
                orderEventProducer.publishDeliveryPreferencesUpdated(event);

                log.info("Delivery preferences updated for order {}", orderId);
                return savedOrder;
        }

        public List<Order> getCompletedOrdersForPeriod(LocalDateTime start, LocalDateTime end) {
                return orderRepository.findByStatusAndActualDeliveryTimeBetween(OrderStatus.DELIVERED, start, end);
        }

        public Integer getDemand() {
                List<OrderStatus> terminalStates = List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);
                return (int) orderRepository.findAll().stream()
                                .filter(order -> !terminalStates.contains(order.getStatus()))
                                .count();
        }
}
