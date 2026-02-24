package com.logistics.returns.automation;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderType;
import com.logistics.order.service.OrderService;
import com.logistics.returns.model.ReturnRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReversePickupServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReversePickupService reversePickupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void scheduleReverseLogistics_shouldCreateOrderWithReversePickupType() {
        ReturnRequest returnRequest = ReturnRequest.builder()
                .returnId("RET-123")
                .customerId("CUST-99")
                .pickupLatitude(34.0522)
                .pickupLongitude(-118.2437)
                .pickupAddress("Client Home")
                .build();

        Order mockSaved = new Order();
        mockSaved.setOrderId("ORD-REV-12345");
        // ArgumentCaptor intercepts the constructed internal order to verify mapping
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderService.createOrder(orderCaptor.capture())).thenReturn(mockSaved);

        Order result = reversePickupService.scheduleReverseLogistics(returnRequest);

        assertNotNull(result);
        assertEquals("ORD-REV-12345", result.getOrderId());

        Order captured = orderCaptor.getValue();
        assertEquals(OrderType.REVERSE_PICKUP, captured.getType());
        assertEquals("RET-123", captured.getExternalOrderId());
        assertEquals(34.0522, captured.getPickupLocation().getLatitude());
        assertEquals("Central Returns Hub, Industrial Area", captured.getDropLocation().getAddress());
    }
}
