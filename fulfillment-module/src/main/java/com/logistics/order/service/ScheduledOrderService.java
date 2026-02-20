package com.logistics.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.order.model.Order;
import com.logistics.order.model.ScheduledOrder;
import com.logistics.order.repository.ScheduledOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledOrderService {

    private final ScheduledOrderRepository scheduledOrderRepository;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ScheduledOrder createScheduledOrder(Order orderTemplate, String cronExpression, String customerId,
            String tenantId) {
        // Validate Cron
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }

        String orderJson;
        try {
            orderJson = objectMapper.writeValueAsString(orderTemplate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order template", e);
        }

        ScheduledOrder scheduledOrder = ScheduledOrder.builder()
                .customerId(customerId)
                .tenantId(tenantId)
                .orderTemplateJson(orderJson)
                .cronExpression(cronExpression)
                .status(ScheduledOrder.ScheduledOrderStatus.ACTIVE)
                .nextExecutionTime(calculateNextExecutionTime(cronExpression))
                .build();

        return scheduledOrderRepository.save(scheduledOrder);
    }

    public List<ScheduledOrder> getScheduledOrders(String customerId) {
        return scheduledOrderRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void deleteScheduledOrder(Long id) {
        ScheduledOrder order = scheduledOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheduled order not found"));
        order.setDeleted(true);
        order.setStatus(ScheduledOrder.ScheduledOrderStatus.CANCELLED);
        scheduledOrderRepository.save(order);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledOrder> dueOrders = scheduledOrderRepository.findByNextExecutionTimeBeforeAndStatus(now,
                ScheduledOrder.ScheduledOrderStatus.ACTIVE);

        for (ScheduledOrder scheduledOrder : dueOrders) {
            try {
                processSingleOrder(scheduledOrder);
            } catch (Exception e) {
                log.error("Failed to process scheduled order {}", scheduledOrder.getId(), e);
            }
        }
    }

    private void processSingleOrder(ScheduledOrder scheduledOrder) {
        try {
            Order orderTemplate = objectMapper.readValue(scheduledOrder.getOrderTemplateJson(), Order.class);

            // Clone relevant fields, reset ID and status
            orderTemplate.setOrderId(null);
            orderTemplate.setCreatedAt(null);
            orderTemplate.setUpdatedAt(null);
            // setLastModifiedAt is not available
            orderTemplate.setStatus(null); // Let OrderService set initial status

            // Update scheduled time for the new order
            orderTemplate.setScheduledTime(LocalDateTime.now()); // Or next execution time based on business logic

            // Create actual order
            orderService.createOrder(orderTemplate);

            // Update next execution time
            scheduledOrder.setNextExecutionTime(calculateNextExecutionTime(scheduledOrder.getCronExpression()));
            scheduledOrderRepository.save(scheduledOrder);

            log.info("Successfully processed scheduled order {}", scheduledOrder.getId());

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order template for scheduled order {}", scheduledOrder.getId(), e);
            // Consider pausing or flagging the scheduled order
        }
    }

    private LocalDateTime calculateNextExecutionTime(String cronExpression) {
        CronExpression cron = CronExpression.parse(cronExpression);
        LocalDateTime next = cron.next(LocalDateTime.now());
        if (next == null) {
            throw new IllegalArgumentException("Cron expression '" + cronExpression + "' will never fire.");
        }
        return next;
    }
}
