package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStop;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.event.OrderEventProducer;
import com.logistics.order.mappers.OrderMapper;
import com.logistics.platform.event.dto.OrderCreatedEvent;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import com.logistics.routing.solver.VRPSolver;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

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
        private final OrderNotificationService notificationService;
        private final CrossBorderComplianceService complianceService;
        private final VRPSolver vrpSolver;
        private final ObservationRegistry observationRegistry;

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
                return Observation.createNotStarted("order.fulfillment.create", observationRegistry)
                                .lowCardinalityKeyValue("orderType",
                                                order.getType() != null ? order.getType().name() : "UNKNOWN")
                                .observe(() -> {
                                        // Validate order creation
                                        validationService.validateOrderCreation(order);

                                        if (order.getOrderId() == null) {
                                                order.setOrderId(UUID.randomUUID().toString());
                                        }

                                        if (order.getStatus() == null) {
                                                order.setStatus(OrderStatus.CREATED);
                                        }

                                        if (order.getCurrency() == null) {
                                                order.setCurrency("INR");
                                        }

                                        if (order.getTimezone() == null) {
                                                order.setTimezone("Asia/Kolkata");
                                        }

                                        // Apply Feature-gated Compliance Checks
                                        complianceService.processCompliance(order);

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
                                                log.error("Failed to publish OrderCreatedEvent for order {}: {}",
                                                                savedOrder.getOrderId(),
                                                                e.getMessage());
                                        }

                                        return savedOrder;
                                });
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

                // Re-optimize stops if it's a multi-stop order
                if (savedOrder.getStops() != null && savedOrder.getStops().size() > 1) {
                        try {
                                optimizeOrderStops(orderId);
                        } catch (Exception e) {
                                log.error("Failed to optimize stops for order {}: {}", orderId, e.getMessage());
                        }
                }

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

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order partialPickup(String orderId,
                        List<com.logistics.platform.common.dto.order.OrderItemFulfillmentDto> fulfillments) {
                Order order = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                stateMachineService.validateTransition(order.getStatus(), OrderStatus.PARTIALLY_PICKED_UP);

                updateItemsAndFulfillmentStatus(order, fulfillments);
                order.setStatus(OrderStatus.PARTIALLY_PICKED_UP);
                order.setActualPickupTime(LocalDateTime.now());

                Order savedOrder = orderRepository.save(order);
                recordStatusChange(orderId, OrderStatus.PARTIALLY_PICKED_UP, "Partial pickup completed");
                return savedOrder;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order partialDelivery(String orderId,
                        List<com.logistics.platform.common.dto.order.OrderItemFulfillmentDto> fulfillments) {
                Order order = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                stateMachineService.validateTransition(order.getStatus(), OrderStatus.PARTIALLY_DELIVERED);

                updateItemsAndFulfillmentStatus(order, fulfillments);
                order.setStatus(OrderStatus.PARTIALLY_DELIVERED);
                order.setActualDeliveryTime(LocalDateTime.now());

                Order savedOrder = orderRepository.save(order);
                recordStatusChange(orderId, OrderStatus.PARTIALLY_DELIVERED, "Partial delivery completed");
                return savedOrder;
        }

        private void updateItemsAndFulfillmentStatus(Order order,
                        List<com.logistics.platform.common.dto.order.OrderItemFulfillmentDto> fulfillments) {
                for (com.logistics.platform.common.dto.order.OrderItemFulfillmentDto f : fulfillments) {
                        order.getItems().stream()
                                        .filter(item -> item.getSku().equals(f.getSku()))
                                        .findFirst()
                                        .ifPresent(item -> {
                                                item.setFulfilledQuantity(f.getFulfilledQuantity());
                                                item.setStatus(com.logistics.order.model.OrderItem.ItemFulfillmentStatus
                                                                .valueOf(f.getStatus()));
                                        });
                }

                long totalItems = order.getItems().size();
                long fullyFulfilled = order.getItems().stream()
                                .filter(i -> i.getFulfilledQuantity().equals(i.getTotalQuantity()))
                                .count();

                if (fullyFulfilled < totalItems) {
                        order.setFulfillmentStatus(Order.FulfillmentStatus.PARTIAL);
                } else {
                        order.setFulfillmentStatus(Order.FulfillmentStatus.FULL);
                }
        }

        private void recordStatusChange(String orderId, OrderStatus newStatus, String notes) {
                historyService.recordStatusChange(
                                orderId,
                                null, // Extract previous if needed, but OrderService usually has it contextually
                                newStatus,
                                "SYSTEM",
                                notes,
                                null,
                                null);
        }

        @Transactional
        public void optimizeOrderStops(String orderId) {
                Order order = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                if (order.getStops() == null || order.getStops().size() <= 1) {
                        return;
                }

                log.info("Optimizing stops for order: {}", orderId);

                // Build Optimization Request
                List<com.logistics.routing.dto.RouteOptimizationRequest.DeliveryStop> stops = order.getStops().stream()
                                .map(s -> com.logistics.routing.dto.RouteOptimizationRequest.DeliveryStop.builder()
                                                .stopId(String.valueOf(s.getId()))
                                                .orderId(orderId)
                                                .latitude(s.getLocation().getLatitude())
                                                .longitude(s.getLocation().getLongitude())
                                                .address(s.getLocation().getAddress())
                                                .timeWindowStart(s.getEstimatedArrival() != null
                                                                ? s.getEstimatedArrival()
                                                                : null)
                                                .timeWindowEnd(s.getEstimatedArrival() != null
                                                                ? s.getEstimatedArrival().plusHours(2)
                                                                : null)
                                                .demandWeight(10) // Default
                                                .serviceDurationMinutes(10)
                                                .build())
                                .collect(java.util.stream.Collectors.toList());

                com.logistics.routing.dto.RouteOptimizationRequest.Vehicle vehicle = com.logistics.routing.dto.RouteOptimizationRequest.Vehicle
                                .builder()
                                .vehicleId(order.getVehicleId() != null ? order.getVehicleId() : "V-" + orderId)
                                .driverId(order.getDriverId())
                                .startLatitude(order.getPickupLocation().getLatitude())
                                .startLongitude(order.getPickupLocation().getLongitude())
                                .shiftStart(LocalDateTime.now().withHour(8).withMinute(0))
                                .shiftEnd(LocalDateTime.now().withHour(18).withMinute(0))
                                .capacityWeight(1000)
                                .build();

                com.logistics.routing.dto.RouteOptimizationRequest request = com.logistics.routing.dto.RouteOptimizationRequest
                                .builder()
                                .tenantId(order.getTenantId())
                                .stops(stops)
                                .vehicles(java.util.Collections.singletonList(vehicle))
                                .constraints(com.logistics.routing.dto.RouteOptimizationRequest.OptimizationConstraints
                                                .builder()
                                                .respectTimeWindows(true)
                                                .respectCapacity(true)
                                                .build())
                                .build();

                // Solve
                com.logistics.routing.dto.RouteOptimizationResponse response = vrpSolver.solve(request);

                if (response.getStatus() == com.logistics.routing.dto.RouteOptimizationResponse.OptimizationStatus.COMPLETED
                                && !response.getRoutes().isEmpty()) {
                        List<com.logistics.routing.dto.RouteOptimizationResponse.RouteStop> optimizedStops = response
                                        .getRoutes().get(0).getStops();

                        // Update stop sequences
                        for (int i = 0; i < optimizedStops.size(); i++) {
                                String stopIdStr = optimizedStops.get(i).getStopId();
                                Long stopId = Long.parseLong(stopIdStr);
                                final int sequence = i + 1;
                                order.getStops().stream()
                                                .filter(s -> s.getId().equals(stopId))
                                                .findFirst()
                                                .ifPresent(s -> s.setStopSequence(sequence));
                        }

                        orderRepository.save(order);
                        log.info("Stops re-sequenced successfully for order: {}", orderId);
                }
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order splitOrder(String orderId, List<String> itemSkus) {
                Order originalOrder = orderRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                if (originalOrder.getStatus() != OrderStatus.CREATED
                                && originalOrder.getStatus() != OrderStatus.ASSIGNED) {
                        throw new RuntimeException(
                                        "Order cannot be split in current status: " + originalOrder.getStatus());
                }

                List<com.logistics.order.model.OrderItem> itemsToMove = originalOrder.getItems().stream()
                                .filter(item -> itemSkus.contains(item.getSku()))
                                .collect(java.util.stream.Collectors.toList());

                if (itemsToMove.isEmpty()) {
                        throw new RuntimeException("No matching items found for split");
                }

                if (itemsToMove.size() == originalOrder.getItems().size()) {
                        throw new RuntimeException(
                                        "Cannot split all items into a new order; use re-assignment instead");
                }

                // Create new child order
                Order childOrder = Order.builder()
                                .orderId(UUID.randomUUID().toString())
                                .customerId(originalOrder.getCustomerId())
                                .tenantId(originalOrder.getTenantId())
                                .type(originalOrder.getType())
                                .status(originalOrder.getStatus())
                                .pickupLocation(originalOrder.getPickupLocation())
                                .dropLocation(originalOrder.getDropLocation())
                                .parentOrderId(orderId)
                                .items(new java.util.ArrayList<>())
                                .stops(new java.util.ArrayList<>())
                                .weightKg(itemsToMove.stream()
                                                .mapToDouble(i -> i.getWeight() != null ? i.getWeight() : 0.0).sum())
                                .build();

                // Move items
                for (com.logistics.order.model.OrderItem item : itemsToMove) {
                        originalOrder.getItems().remove(item);
                        item.setOrder(childOrder);
                        childOrder.getItems().add(item);
                }

                // Copy stops for child order
                for (OrderStop stop : originalOrder.getStops()) {
                        childOrder.getStops().add(OrderStop.builder()
                                        .order(childOrder)
                                        .location(stop.getLocation())
                                        .stopSequence(stop.getStopSequence())
                                        .stopType(stop.getStopType())
                                        .completed(false)
                                        .build());
                }

                Order savedChild = orderRepository.save(childOrder);
                orderRepository.save(originalOrder);

                historyService.recordStatusChange(orderId, originalOrder.getStatus(), originalOrder.getStatus(),
                                "SYSTEM", "Order split. Items moved to " + savedChild.getOrderId(), null, null);

                return savedChild;
        }

        @Transactional
        @CacheEvict(value = { "orders", "orders_by_id" }, allEntries = true)
        public Order mergeOrders(List<String> orderIds) {
                if (orderIds == null || orderIds.size() < 2) {
                        throw new RuntimeException("At least two orders are required for merging");
                }

                List<Order> sourceOrders = orderIds.stream()
                                .map(id -> orderRepository.findByOrderId(id)
                                                .orElseThrow(() -> new RuntimeException("Order not found: " + id)))
                                .collect(java.util.stream.Collectors.toList());

                // Validation: Same tenant, customer, and drop location
                Order first = sourceOrders.get(0);
                for (Order o : sourceOrders) {
                        if (!o.getTenantId().equals(first.getTenantId()))
                                throw new RuntimeException("Cannot merge orders from different tenants");
                        if (!o.getStatus().equals(OrderStatus.CREATED) && !o.getStatus().equals(OrderStatus.ASSIGNED)) {
                                throw new RuntimeException("Order " + o.getOrderId() + " cannot be merged in status "
                                                + o.getStatus());
                        }
                }

                // Create new Consolidated Order or pick the first one as master
                Order masterOrder = first;
                sourceOrders.remove(0);

                for (Order subOrder : sourceOrders) {
                        // Move items
                        for (com.logistics.order.model.OrderItem item : subOrder.getItems()) {
                                item.setOrder(masterOrder);
                                masterOrder.getItems().add(item);
                        }
                        subOrder.getItems().clear();

                        // Mark sub-order as merged
                        subOrder.setStatus(OrderStatus.CANCELLED);
                        subOrder.setCancellationReason("Merged into " + masterOrder.getOrderId());
                        subOrder.setMergedIntoOrderId(masterOrder.getOrderId());

                        historyService.recordStatusChange(subOrder.getOrderId(), OrderStatus.CREATED,
                                        OrderStatus.CANCELLED,
                                        "SYSTEM", "Merged into " + masterOrder.getOrderId(), null, null);

                        orderRepository.save(subOrder);
                }

                masterOrder.setWeightKg(masterOrder.getItems().stream()
                                .mapToDouble(i -> i.getWeight() != null ? i.getWeight() : 0.0).sum());

                Order savedMaster = orderRepository.save(masterOrder);
                log.info("Merged {} orders into {}", orderIds.size(), savedMaster.getOrderId());

                return savedMaster;
        }
}
