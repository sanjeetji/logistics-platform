package com.logistics.b2b.service;

import com.logistics.b2b.client.NotificationServiceClient;
import com.logistics.b2b.model.*;
import com.logistics.b2b.repository.B2BOrderRepository;
import com.logistics.b2b.repository.SLAEscalationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SLAMonitoringServiceTest {

    @Mock
    private B2BOrderRepository orderRepository;
    @Mock
    private SLARuleService slaRuleService;
    @Mock
    private SLAEscalationRepository escalationRepository;
    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private SLAMonitoringService slaMonitoringService;

    @BeforeEach
    void setUp() {
        // Inject self for transactional method calls within the same class
        ReflectionTestUtils.setField(slaMonitoringService, "self", slaMonitoringService);
    }

    @Test
    void testMonitorAllOrders_Breach() {
        B2BOrder order = new B2BOrder();
        order.setOrderId("ORD-123");
        order.setClientId(1L);
        order.setStatus(B2BOrderStatus.IN_PROGRESS);
        order.setSlaDeadline(LocalDateTime.now().minusMinutes(10)); // Past deadline
        order.setSlaStatus(SLAStatus.ON_TIME);

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        slaMonitoringService.monitorAllOrders();

        verify(escalationRepository).save(any(SLAEscalation.class));
        verify(notificationClient).sendNotification(any());
        verify(orderRepository).save(order);
        assertEquals(SLAStatus.BREACHED, order.getSlaStatus());
    }

    @Test
    void testMonitorAllOrders_AtRisk() {
        B2BOrder order = new B2BOrder();
        order.setOrderId("ORD-124");
        order.setClientId(1L);
        order.setOrderType(OrderType.SINGLE);
        order.setPriority(Priority.MEDIUM);
        order.setStatus(B2BOrderStatus.IN_PROGRESS);
        order.setSlaDeadline(LocalDateTime.now().plusMinutes(30)); // Deadline in 30 mins
        order.setSlaStatus(SLAStatus.ON_TIME);

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(slaRuleService.getAtRiskThreshold(any(), any(), any())).thenReturn(60); // Threshold 60 mins

        slaMonitoringService.monitorAllOrders();

        verify(escalationRepository).save(any(SLAEscalation.class)); // Warning escalation
        verify(notificationClient).sendNotification(any());
        verify(orderRepository).save(order);
        assertEquals(SLAStatus.AT_RISK, order.getSlaStatus());
    }

    @Test
    void testMonitorAllOrders_OnTime() {
        B2BOrder order = new B2BOrder();
        order.setOrderId("ORD-125");
        order.setClientId(1L);
        order.setOrderType(OrderType.SINGLE);
        order.setPriority(Priority.MEDIUM);
        order.setStatus(B2BOrderStatus.IN_PROGRESS);
        order.setSlaDeadline(LocalDateTime.now().plusMinutes(120)); // Deadline in 2 hours
        order.setSlaStatus(SLAStatus.ON_TIME);

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(slaRuleService.getAtRiskThreshold(any(), any(), any())).thenReturn(60); // Threshold 60 mins

        slaMonitoringService.monitorAllOrders();

        verify(escalationRepository, never()).save(any(SLAEscalation.class));
        verify(notificationClient, never()).sendNotification(any());
        // verify(orderRepository).save(order); // Save might be called if status was
        // effectively same but logic calls updateSLAStatus which might set it again?
        // Logic: if oldStatus != newStatus.
        // default status for new object is null? No, explicit check.
        // here oldStatus is ON_TIME, newStatus is ON_TIME. so save shouldn't be called.
        verify(orderRepository, never()).save(order);
    }

    @Test
    void testNotificationFailureDoesNotThrow() {
        B2BOrder order = new B2BOrder();
        order.setOrderId("ORD-123");
        order.setClientId(1L);
        order.setStatus(B2BOrderStatus.IN_PROGRESS);
        order.setSlaDeadline(LocalDateTime.now().minusMinutes(10));
        order.setSlaStatus(SLAStatus.ON_TIME);

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        doThrow(new RuntimeException("Notification service down")).when(notificationClient).sendNotification(any());

        assertDoesNotThrow(() -> slaMonitoringService.monitorAllOrders());

        verify(escalationRepository).save(any(SLAEscalation.class)); // Escalation should still be saved
        verify(orderRepository).save(order); // Order status should still update
        assertEquals(SLAStatus.BREACHED, order.getSlaStatus());
    }
}
