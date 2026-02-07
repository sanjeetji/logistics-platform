package com.logistics.order.service;

import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStatusHistory;
import com.logistics.order.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderHistoryService {

    private final OrderStatusHistoryRepository historyRepository;

    /**
     * Records a status change in history
     */
    @Transactional
    public void recordStatusChange(
            String orderId,
            OrderStatus previousStatus,
            OrderStatus newStatus,
            String changedBy,
            String reason,
            Double latitude,
            Double longitude) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changedAt(LocalDateTime.now())
                .changedBy(changedBy != null ? changedBy : "SYSTEM")
                .reason(reason)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        historyRepository.save(history);
        log.info("Recorded status change for order {}: {} -> {}", orderId, previousStatus, newStatus);
    }

    /**
     * Gets status history for an order
     */
    public List<OrderStatusHistory> getOrderHistory(String orderId) {
        return historyRepository.findByOrderIdOrderByChangedAtDesc(orderId);
    }
}
