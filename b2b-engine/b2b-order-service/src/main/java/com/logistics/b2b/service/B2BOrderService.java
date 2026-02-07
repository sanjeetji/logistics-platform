package com.logistics.b2b.service;

import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.dto.OrderStopDTO;
import com.logistics.b2b.model.*;
import com.logistics.b2b.repository.B2BOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for B2B order management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2BOrderService {

    private final B2BOrderRepository orderRepository;
    private final SLAMonitoringService slaMonitoringService;

    /**
     * Create B2B order
     */
    @Transactional
    public B2BOrder createOrder(CreateB2BOrderRequest request) {
        log.info("Creating B2B order for client: {}", request.getClientId());

        B2BOrder order = B2BOrder.builder()
                .orderId("B2B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clientId(request.getClientId())
                .orderType(OrderType.valueOf(request.getOrderType() != null ? request.getOrderType() : "SINGLE"))
                .priority(Priority.valueOf(request.getPriority() != null ? request.getPriority() : "MEDIUM"))
                .slaDeadline(request.getSlaDeadline())
                .scheduledPickupTime(request.getScheduledPickupTime())
                .scheduledDeliveryTime(request.getScheduledDeliveryTime())
                .metadata(request.getMetadata())
                .notes(request.getNotes())
                .build();

        // Add stops
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

        order = orderRepository.save(order);
        
        // Initialize SLA monitoring
        slaMonitoringService.updateSLAStatus(order);

        log.info("B2B order created: {}", order.getOrderId());
        return order;
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
     * Update order status
     */
    @Transactional
    public B2BOrder updateOrderStatus(String orderId, B2BOrderStatus newStatus) {
        B2BOrder order = getOrderById(orderId);
        order.setStatus(newStatus);
        
        // Update SLA status
        slaMonitoringService.updateSLAStatus(order);
        
        return orderRepository.save(order);
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
