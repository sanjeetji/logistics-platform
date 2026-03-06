package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderLocation;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class OrderMergingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderMergingService orderMergingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAndMergeEligibleOrders_GroupsCorrectly() {
        // Arrange
        String tenantId = "T1";
        String customerId = "C1";
        String address = "123 Main St";
        OrderLocation location = OrderLocation.builder().address(address).build();

        Order o1 = Order.builder().orderId("O1").tenantId(tenantId).customerId(customerId).dropLocation(location)
                .status(OrderStatus.CREATED).build();
        Order o2 = Order.builder().orderId("O2").tenantId(tenantId).customerId(customerId).dropLocation(location)
                .status(OrderStatus.CREATED).build();
        Order o3 = Order.builder().orderId("O3").tenantId(tenantId).customerId("C2").dropLocation(location)
                .status(OrderStatus.CREATED).build(); // Different customer

        when(orderRepository.findAll()).thenReturn(Arrays.asList(o1, o2, o3));

        // Act
        orderMergingService.findAndMergeEligibleOrders();

        // Assert
        verify(orderService, times(1))
                .mergeOrders(argThat(list -> list.contains("O1") && list.contains("O2") && list.size() == 2));
        verify(orderService, never()).mergeOrders(argThat(list -> list.contains("O3")));
    }
}
