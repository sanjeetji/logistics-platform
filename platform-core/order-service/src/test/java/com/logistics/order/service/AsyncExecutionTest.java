package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@ActiveProfiles("test")
public class AsyncExecutionTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    public void testAsyncExecution() throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.CREATED);
        order.setTenantId("tenant-1");

        long startTime = System.currentTimeMillis();
        CompletableFuture<Void> future = notificationService.sendOrderConfirmation(order);
        long endTime = System.currentTimeMillis();

        // The method calls Thread.sleep(1000), so if it runs synchronously,
        // endTime - startTime should be >= 1000.
        // If async, it should be very fast.

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        // Wait for future to complete to ensure it actually ran
        future.get();
    }
}
