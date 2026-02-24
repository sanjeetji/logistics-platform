package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderItem;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceSplitMergeTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderHistoryService historyService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSplitOrder_Success() {
        // Arrange
        String orderId = "O1";
        OrderItem item1 = OrderItem.builder().sku("SKU1").weight(5.0).build();
        OrderItem item2 = OrderItem.builder().sku("SKU2").weight(10.0).build();

        Order originalOrder = Order.builder()
                .orderId(orderId)
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>(Arrays.asList(item1, item2)))
                .stops(new ArrayList<>())
                .build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(originalOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Order childOrder = orderService.splitOrder(orderId, Arrays.asList("SKU2"));

        // Assert
        assertNotNull(childOrder);
        assertEquals(1, originalOrder.getItems().size());
        assertEquals(1, childOrder.getItems().size());
        assertEquals("SKU1", originalOrder.getItems().get(0).getSku());
        assertEquals("SKU2", childOrder.getItems().get(0).getSku());
        assertEquals(orderId, childOrder.getParentOrderId());
    }

    @Test
    void testMergeOrders_Success() {
        // Arrange
        Order order1 = Order.builder()
                .orderId("O1")
                .tenantId("T1")
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>(Arrays.asList(OrderItem.builder().sku("SKU1").build())))
                .build();

        Order order2 = Order.builder()
                .orderId("O2")
                .tenantId("T1")
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>(Arrays.asList(OrderItem.builder().sku("SKU2").build())))
                .build();

        when(orderRepository.findByOrderId("O1")).thenReturn(Optional.of(order1));
        when(orderRepository.findByOrderId("O2")).thenReturn(Optional.of(order2));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Order masterOrder = orderService.mergeOrders(Arrays.asList("O1", "O2"));

        // Assert
        assertNotNull(masterOrder);
        assertEquals("O1", masterOrder.getOrderId());
        assertEquals(2, masterOrder.getItems().size());
        assertEquals(OrderStatus.CANCELLED, order2.getStatus());
        assertEquals("O1", order2.getMergedIntoOrderId());
    }
}
