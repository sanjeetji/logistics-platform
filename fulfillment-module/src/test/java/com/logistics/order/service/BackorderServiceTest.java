package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class BackorderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderHistoryService historyService;

    @InjectMocks
    private BackorderService backorderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMoveToBackorder_Success() {
        // Arrange
        String orderId = "O1";
        Order order = Order.builder()
                .orderId(orderId)
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

        // Act
        backorderService.moveToBackorder(orderId, "Stock out");

        // Assert
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.BACKORDERED));
        verify(historyService).recordStatusChange(eq(orderId), eq(OrderStatus.CREATED), eq(OrderStatus.BACKORDERED),
                anyString(), contains("Stock out"), any(), any());
    }
}
