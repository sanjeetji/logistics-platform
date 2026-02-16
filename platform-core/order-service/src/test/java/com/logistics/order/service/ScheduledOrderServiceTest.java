package com.logistics.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.order.dto.CreateScheduledOrderRequest;
import com.logistics.order.model.Order;
import com.logistics.order.model.ScheduledOrder;
import com.logistics.order.repository.ScheduledOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduledOrderServiceTest {

    @Mock
    private ScheduledOrderRepository scheduledOrderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ScheduledOrderService scheduledOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createScheduledOrder_Success() throws Exception {
        Order orderTemplate = new Order();
        CreateScheduledOrderRequest request = new CreateScheduledOrderRequest();
        request.setOrderTemplate(orderTemplate);
        request.setCronExpression("0 0 12 * * ?"); // Daily at 12 PM
        request.setCustomerId("cust123");
        request.setTenantId("tenant1");

        when(objectMapper.writeValueAsString(any(Order.class))).thenReturn("{}");
        when(scheduledOrderRepository.save(any(ScheduledOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScheduledOrder result = scheduledOrderService.createScheduledOrder(
                request.getOrderTemplate(),
                request.getCronExpression(),
                request.getCustomerId(),
                request.getTenantId());

        assertNotNull(result);
        assertEquals("cust123", result.getCustomerId());
        assertEquals("tenant1", result.getTenantId());
        assertEquals("0 0 12 * * ?", result.getCronExpression());
        assertNotNull(result.getNextExecutionTime());
        assertEquals(ScheduledOrder.ScheduledOrderStatus.ACTIVE, result.getStatus());
        verify(scheduledOrderRepository).save(any(ScheduledOrder.class));
    }

    @Test
    void createScheduledOrder_InvalidCron() {
        CreateScheduledOrderRequest request = new CreateScheduledOrderRequest();
        request.setOrderTemplate(new Order());
        request.setCronExpression("invalid cron");
        request.setCustomerId("cust123");
        request.setTenantId("tenant1");

        assertThrows(IllegalArgumentException.class, () -> scheduledOrderService.createScheduledOrder(
                request.getOrderTemplate(),
                request.getCronExpression(),
                request.getCustomerId(),
                request.getTenantId()));
    }

    @Test
    void processScheduledOrders_Success() throws Exception {
        ScheduledOrder scheduledOrder = ScheduledOrder.builder()
                .customerId("cust123")
                .tenantId("tenant1")
                .cronExpression("0 0 12 * * ?")
                .orderTemplateJson("{}")
                .status(ScheduledOrder.ScheduledOrderStatus.ACTIVE)
                .nextExecutionTime(LocalDateTime.now().minusMinutes(1))
                .build();
        scheduledOrder.setId(1L);

        when(scheduledOrderRepository.findByNextExecutionTimeBeforeAndStatus(any(LocalDateTime.class),
                eq(ScheduledOrder.ScheduledOrderStatus.ACTIVE)))
                .thenReturn(List.of(scheduledOrder));
        when(objectMapper.readValue(anyString(), eq(Order.class))).thenReturn(new Order());
        when(orderService.createOrder(any(Order.class))).thenReturn(new Order());

        scheduledOrderService.processScheduledOrders();

        verify(orderService).createOrder(any(Order.class));
        verify(scheduledOrderRepository).save(scheduledOrder);
        assertTrue(scheduledOrder.getNextExecutionTime().isAfter(LocalDateTime.now()));
    }

    @Test
    void getScheduledOrders_Success() {
        when(scheduledOrderRepository.findByCustomerId("cust123")).thenReturn(Collections.emptyList());

        List<ScheduledOrder> results = scheduledOrderService.getScheduledOrders("cust123");

        assertNotNull(results);
        verify(scheduledOrderRepository).findByCustomerId("cust123");
    }

    @Test
    void deleteScheduledOrder_Success() {
        ScheduledOrder scheduledOrder = new ScheduledOrder();
        scheduledOrder.setId(1L);
        when(scheduledOrderRepository.findById(1L)).thenReturn(Optional.of(scheduledOrder));

        scheduledOrderService.deleteScheduledOrder(1L);

        assertTrue(scheduledOrder.getDeleted());
        assertEquals(ScheduledOrder.ScheduledOrderStatus.CANCELLED, scheduledOrder.getStatus());
        verify(scheduledOrderRepository).save(scheduledOrder);
    }
}
