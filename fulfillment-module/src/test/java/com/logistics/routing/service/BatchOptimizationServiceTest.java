package com.logistics.routing.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStop;
import com.logistics.order.model.OrderLocation;
import com.logistics.order.repository.OrderRepository;
import com.logistics.platform.api.fleet.FleetClient;
import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.solver.VRPSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BatchOptimizationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FleetClient fleetClient;

    @Mock
    private VRPSolver vrpSolver;

    @InjectMocks
    private BatchOptimizationService batchOptimizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOptimizeBatch_Success() {
        // Arrange
        String tenantId = "T1";
        Order order = Order.builder()
                .orderId("O1")
                .tenantId(tenantId)
                .status(OrderStatus.CREATED)
                .stops(Collections.singletonList(OrderStop.builder()
                        .id(101L)
                        .completed(false)
                        .location(OrderLocation.builder()
                                .latitude(12.9)
                                .longitude(77.6)
                                .build())
                        .build()))
                .build();

        DriverDto driver = DriverDto.builder()
                .id(1L)
                .currentLatitude(12.8)
                .currentLongitude(77.5)
                .build();

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(fleetClient.findNearestAvailableDrivers(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(ApiResponse.success(Collections.singletonList(driver), "Drivers found"));

        RouteOptimizationResponse solverResponse = RouteOptimizationResponse.builder()
                .status(RouteOptimizationResponse.OptimizationStatus.COMPLETED)
                .routes(Collections.singletonList(RouteOptimizationResponse.OptimizedRoute.builder()
                        .driverId("1")
                        .vehicleId("V-1")
                        .stops(Collections.singletonList(RouteOptimizationResponse.RouteStop.builder()
                                .stopId("101")
                                .orderId("O1")
                                .build()))
                        .build()))
                .build();

        when(vrpSolver.solve(any(RouteOptimizationRequest.class))).thenReturn(solverResponse);

        // Act
        RouteOptimizationResponse response = batchOptimizationService.optimizeBatch(tenantId, 12.8, 77.5, 5000.0);

        // Assert
        assertEquals(RouteOptimizationResponse.OptimizationStatus.COMPLETED, response.getStatus());
        verify(orderRepository).saveAll(anyList());
        assertEquals("1", order.getDriverId());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
    }

    @Test
    void testOptimizeBatch_NoOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        RouteOptimizationResponse response = batchOptimizationService.optimizeBatch("T1", 0.0, 0.0, 1000.0);

        // Assert
        assertEquals(RouteOptimizationResponse.OptimizationStatus.FAILED, response.getStatus());
        verify(vrpSolver, never()).solve(any());
    }
}
