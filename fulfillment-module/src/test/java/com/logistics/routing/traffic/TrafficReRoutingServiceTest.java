package com.logistics.routing.traffic;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStop;
import com.logistics.order.repository.OrderRepository;
import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.kafka.TrafficUpdateEvent;
import com.logistics.routing.rerouting.DynamicReRoutingService;
import com.logistics.routing.rerouting.ReRoutingTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrafficReRoutingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DynamicReRoutingService dynamicReRoutingService;

    @InjectMocks
    private TrafficReRoutingService trafficReRoutingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessTrafficIncident_Success() {
        // Arrange
        TrafficUpdateEvent event = TrafficUpdateEvent.builder()
                .eventId("E1")
                .eventType("INCIDENT")
                .latitude(12.9716)
                .longitude(77.5946)
                .radiusKm(5.0)
                .description("Heavy Jam")
                .build();

        Order order = Order.builder()
                .orderId("O1")
                .driverId("D1")
                .vehicleId("V1")
                .status(OrderStatus.ASSIGNED)
                .pickupLocation(com.logistics.order.model.OrderLocation.builder()
                        .latitude(12.9710)
                        .longitude(77.5940)
                        .build())
                .stops(Collections.singletonList(OrderStop.builder()
                        .id(101L)
                        .completed(false)
                        .build()))
                .build();

        when(orderRepository.findAffectedOrders(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.singletonList(order));

        // Act
        trafficReRoutingService.processTrafficIncident(event);

        // Assert
        ArgumentCaptor<ReRoutingRequest> requestCaptor = ArgumentCaptor.forClass(ReRoutingRequest.class);
        verify(dynamicReRoutingService).triggerReRouting(requestCaptor.capture());

        ReRoutingRequest capturedRequest = requestCaptor.getValue();
        assertEquals("R-O1", capturedRequest.getRouteId());
        assertEquals("D1", capturedRequest.getDriverId());
        assertEquals(ReRoutingTrigger.TRAFFIC_INCIDENT, capturedRequest.getTrigger());
        assertEquals(Collections.singletonList("101"), capturedRequest.getRemainingStopIds());
    }

    @Test
    void testProcessTrafficIncident_NoAffectedOrders() {
        // Arrange
        TrafficUpdateEvent event = TrafficUpdateEvent.builder()
                .latitude(0.0)
                .longitude(0.0)
                .radiusKm(1.0)
                .build();

        when(orderRepository.findAffectedOrders(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());

        // Act
        trafficReRoutingService.processTrafficIncident(event);

        // Assert
        verify(dynamicReRoutingService, never()).triggerReRouting(any());
    }
}
