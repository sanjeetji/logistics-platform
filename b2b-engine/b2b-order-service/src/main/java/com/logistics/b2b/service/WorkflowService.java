package com.logistics.b2b.service;

import com.logistics.b2b.model.B2BOrder;
import com.logistics.b2b.model.B2BOrderStatus;
import com.logistics.b2b.repository.B2BOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("workflowService")
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final B2BOrderRepository orderRepository;
    private final SLAMonitoringService slaMonitoringService;

    @Transactional
    public void activateOrder(String orderId) {
        log.info("Activating B2B order: {}", orderId);
        updateStatus(orderId, B2BOrderStatus.APPROVED);
        // Move to SCHEDULED after approval to trigger planning
        updateStatus(orderId, B2BOrderStatus.SCHEDULED);
    }

    @Transactional
    public void rejectOrder(String orderId) {
        log.info("Rejecting B2B order: {}", orderId);
        updateStatus(orderId, B2BOrderStatus.REJECTED);
    }

    private void updateStatus(String orderId, B2BOrderStatus status) {
        orderRepository.findByOrderId(orderId).ifPresent(order -> {
            order.setStatus(status);
            slaMonitoringService.updateSLAStatus(order);
            orderRepository.save(order);
        });
    }
}
