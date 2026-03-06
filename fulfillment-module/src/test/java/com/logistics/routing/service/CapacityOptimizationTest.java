package com.logistics.routing.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import com.logistics.platform.api.fleet.FleetClient;
import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.solver.VRPSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class CapacityOptimizationTest {

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
    void testOptimizeBatch_CapacityExceeded_PrioritizesHighPriority() {
        // Arrange
        String tenantId = "T1";
        // 3 orders of 500kg each (Total 1500kg)
        Order o1 = Order.builder().orderId("O1").tenantId(tenantId).status(OrderStatus.CREATED).priority(1)
                .weightKg(500.0).build();
        Order o2 = Order.builder().orderId("O2").tenantId(tenantId).status(OrderStatus.CREATED).priority(10)
                .weightKg(500.0).build();
        Order o3 = Order.builder().orderId("O3").tenantId(tenantId).status(OrderStatus.CREATED).priority(5)
                .weightKg(500.0).build();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(o1, o2, o3));

        // 1 driver with 1000kg capacity
        DriverDto d1 = DriverDto.builder().id(1L).currentLatitude(0.0).currentLongitude(0.0).build();
        when(fleetClient.findNearestAvailableDrivers(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(ApiResponse.success(Arrays.asList(d1)));

        when(vrpSolver.solve(any())).thenReturn(RouteOptimizationResponse.builder()
                .status(RouteOptimizationResponse.OptimizationStatus.COMPLETED)
                .routes(Arrays.asList())
                .build());

        // Act
        RouteOptimizationResponse response = batchOptimizationService.optimizeBatch(tenantId, 0.0, 0.0, 1000.0);

        // Assert
        assertNotNull(response);
        List<String> unassigned = response.getUnassignedOrderIds();
        assertEquals(1, unassigned.size(), "One order should be unassigned");
        assertEquals("O1", unassigned.get(0), "Lowest priority order O1 should be unassigned");
        // O2 (Priority 10) and O3 (Priority 5) sum to 1000kg, which fits.
    }

    @Test
    void testOptimizeBatch_FitsAllWhenUnderCapacity() {
        // Arrange
        String tenantId = "T1";
        Order o1 = Order.builder().orderId("O1").tenantId(tenantId).status(OrderStatus.CREATED).weightKg(100.0).build();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(o1));

        DriverDto d1 = DriverDto.builder().id(1L).build();
        when(fleetClient.findNearestAvailableDrivers(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(ApiResponse.success(Arrays.asList(d1)));

        when(vrpSolver.solve(any())).thenReturn(RouteOptimizationResponse.builder()
                .status(RouteOptimizationResponse.OptimizationStatus.COMPLETED)
                .routes(Arrays.asList())
                .build());

        // Act
        RouteOptimizationResponse response = batchOptimizationService.optimizeBatch(tenantId, 0.0, 0.0, 1000.0);

        // Assert
        assertTrue(response.getUnassignedOrderIds().isEmpty(), "No orders should be unassigned when under capacity");
    }
}
