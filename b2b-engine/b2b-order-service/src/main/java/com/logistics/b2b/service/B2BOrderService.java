package com.logistics.b2b.service;

import java.util.Objects;

import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.dto.OrderStopDTO;
import com.logistics.b2b.model.*;
import com.logistics.b2b.repository.B2BOrderRepository;
import com.logistics.platform.common.dto.order.B2BOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for B2B order management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2BOrderService {

    private final B2BOrderRepository orderRepository;
    private final SLAMonitoringService slaMonitoringService;
    private final SLARuleService slaRuleService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    /**
     * Sync B2B order from ERP
     */
    @Transactional
    public void syncOrder(B2BOrderDto request) {
        log.info("Syncing order from ERP: {}", request.getSapOrderId());

        if (orderRepository.findByOrderId(request.getSapOrderId()).isPresent()) {
            log.warn("Order {} already synced, skipping.", request.getSapOrderId());
            return;
        }

        Long clientId = Long.parseLong(request.getClientId().replaceAll("[^0-9]", ""));
        LocalDateTime deadline = slaRuleService.calculateDeadline(clientId, OrderType.SINGLE, Priority.MEDIUM);

        B2BOrder order = B2BOrder.builder()
                .orderId("B2B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .erpOrderId(request.getSapOrderId())
                .clientId(clientId)
                .orderType(OrderType.SINGLE)
                .priority(Priority.MEDIUM)
                .slaDeadline(deadline)
                .status(B2BOrderStatus.SCHEDULED)
                .notes("Synced from SAP")
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("erp_items", request.getItems());
        metadata.put("erp_total", request.getTotalAmount());
        order.setMetadata(metadata);

        orderRepository.save(order);
        log.info("Order synced and saved: {}", order.getOrderId());
    }

    /**
     * Create B2B order
     */
    @Transactional
    public B2BOrder createOrder(CreateB2BOrderRequest request) {
        log.info("Creating B2B order for client: {}", request.getClientId());

        OrderType type = OrderType.valueOf(request.getOrderType() != null ? request.getOrderType() : "SINGLE");
        Priority priority = Priority.valueOf(request.getPriority() != null ? request.getPriority() : "MEDIUM");

        LocalDateTime deadline = request.getSlaDeadline();
        if (deadline == null) {
            deadline = slaRuleService.calculateDeadline(request.getClientId(), type, priority);
        }

        B2BOrder order = B2BOrder.builder()
                .orderId("B2B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clientId(request.getClientId())
                .orderType(type)
                .priority(priority)
                .slaDeadline(deadline)
                .scheduledPickupTime(request.getScheduledPickupTime())
                .scheduledDeliveryTime(request.getScheduledDeliveryTime())
                .metadata(request.getMetadata())
                .notes(request.getNotes())
                .status(B2BOrderStatus.PENDING_APPROVAL)
                .build();

        if (request.getStops() != null) {
            for (OrderStopDTO stopDTO : request.getStops()) {
                OrderStop stop = OrderStop.builder()
                        .stopSequence(stopDTO.getStopSequence())
                        .stopType(StopType.valueOf(stopDTO.getStopType()))
                        .address(stopDTO.getAddress())
                        .latitude(stopDTO.getLatitude())
                        .longitude(stopDTO.getLongitude())
                        .contactName(stopDTO.getContactName())
                        .contactPhone(stopDTO.getContactPhone())
                        .estimatedArrival(stopDTO.getEstimatedArrival())
                        .items(stopDTO.getItems())
                        .notes(stopDTO.getNotes())
                        .build();
                order.addStop(stop);
            }
        }

        order = Objects.requireNonNull(orderRepository.save(order));

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", order.getOrderId());
        variables.put("clientId", order.getClientId());
        runtimeService.startProcessInstanceByKey("b2bOrderApproval", order.getOrderId(), variables);

        slaMonitoringService.updateSLAStatus(order);

        log.info("B2B order created and approval flow started: {}", order.getOrderId());
        return order;
    }

    /**
     * Update order status with SLA pausing logic
     */
    @Transactional
    public B2BOrder updateOrderStatus(String orderId, B2BOrderStatus newStatus) {
        B2BOrder order = getOrderById(orderId);
        B2BOrderStatus oldStatus = order.getStatus();

        // Handle SLA pausing
        if (newStatus == B2BOrderStatus.ON_HOLD && oldStatus != B2BOrderStatus.ON_HOLD) {
            order.setSlaPausedAt(LocalDateTime.now());
            order.setSlaRemainingMinutes(ChronoUnit.MINUTES.between(LocalDateTime.now(), order.getSlaDeadline()));
            log.info("SLA paused for order: {} with {} mins remaining", orderId, order.getSlaRemainingMinutes());
        } else if (oldStatus == B2BOrderStatus.ON_HOLD && newStatus != B2BOrderStatus.ON_HOLD) {
            if (order.getSlaRemainingMinutes() != null) {
                order.setSlaDeadline(LocalDateTime.now().plusMinutes(order.getSlaRemainingMinutes()));
                order.setSlaPausedAt(null);
                order.setSlaRemainingMinutes(null);
                log.info("SLA unpaused for order: {}. New deadline: {}", orderId, order.getSlaDeadline());
            }
        }

        order.setStatus(newStatus);
        slaMonitoringService.updateSLAStatus(order);

        return orderRepository.save(order);
    }

    /**
     * Approve B2B order
     */
    @Transactional
    public void approveOrder(String orderId) {
        log.info("Approving B2B order: {}", orderId);
        completeTask(orderId, true);
    }

    /**
     * Reject B2B order
     */
    @Transactional
    public void rejectOrder(String orderId) {
        log.info("Rejecting B2B order: {}", orderId);
        completeTask(orderId, false);
    }

    private void completeTask(String orderId, boolean approved) {
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(orderId)
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", approved);
            taskService.complete(task.getId(), variables);
        } else {
            throw new RuntimeException("No active approval task found for order: " + orderId);
        }
    }

    /**
     * Get order by ID
     */
    public B2BOrder getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    /**
     * Get client orders
     */
    public List<B2BOrder> getClientOrders(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    /**
     * Get orders by SLA status
     */
    public List<B2BOrder> getOrdersBySLAStatus(SLAStatus slaStatus) {
        return orderRepository.findBySlaStatus(slaStatus);
    }

    /**
     * Reschedule order
     */
    @Transactional
    public B2BOrder rescheduleOrder(String orderId, LocalDateTime newSlaDeadline) {
        B2BOrder order = getOrderById(orderId);
        order.setSlaDeadline(newSlaDeadline);

        // Recalculate SLA status
        slaMonitoringService.updateSLAStatus(order);

        return orderRepository.save(order);
    }
}
