package com.logistics.order.service;

import com.logistics.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderNotificationService {

    @Async
    public CompletableFuture<Void> sendOrderConfirmation(Order order) {
        log.info("Sending order confirmation for order: {}", order.getOrderId());
        try {
            // Simulate heavy task
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Notification interrupted for order: {}", order.getOrderId(), e);
        }
        log.info("Order confirmation sent for order: {}", order.getOrderId());
        return CompletableFuture.completedFuture(null);
    }
}
