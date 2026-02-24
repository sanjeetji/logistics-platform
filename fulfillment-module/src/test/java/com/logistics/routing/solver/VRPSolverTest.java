package com.logistics.routing.solver;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.traffic.TrafficIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VRPSolverTest {

    private VRPSolver vrpSolver;

    @Mock
    private TrafficIntegrationService trafficIntegrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vrpSolver = new VRPSolver(trafficIntegrationService);
    }

    @Test
    void solve_WithTimeWindowConstraints_ShouldReturnOptimizedRoute() {
        // Build a request where the nearest stop has a later time window
        // Stop 1: Near, Window: 12:00-13:00
        // Stop 2: Further, Window: 09:00-10:00

        LocalDateTime baseDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        RouteOptimizationRequest.DeliveryStop stop1 = RouteOptimizationRequest.DeliveryStop.builder()
                .stopId("STOP1")
                .latitude(12.9716)
                .longitude(77.5946)
                .timeWindowStart(baseDate.withHour(12))
                .timeWindowEnd(baseDate.withHour(13))
                .demandWeight(10)
                .serviceDurationMinutes(10)
                .build();

        RouteOptimizationRequest.DeliveryStop stop2 = RouteOptimizationRequest.DeliveryStop.builder()
                .stopId("STOP2")
                .latitude(12.2958) // Further away
                .longitude(76.6394)
                .timeWindowStart(baseDate.withHour(9))
                .timeWindowEnd(baseDate.withHour(10))
                .demandWeight(10)
                .serviceDurationMinutes(10)
                .build();

        RouteOptimizationRequest.Vehicle vehicle = RouteOptimizationRequest.Vehicle.builder()
                .vehicleId("VEH1")
                .startLatitude(12.9716) // Starting at Stop 1's city (Bangalore)
                .startLongitude(77.5946)
                .shiftStart(baseDate.withHour(8))
                .shiftEnd(baseDate.withHour(18))
                .capacityWeight(100)
                .build();

        RouteOptimizationRequest request = RouteOptimizationRequest.builder()
                .tenantId("test")
                .stops(Arrays.asList(stop1, stop2))
                .vehicles(Arrays.asList(vehicle))
                .constraints(RouteOptimizationRequest.OptimizationConstraints.builder()
                        .respectTimeWindows(true)
                        .respectCapacity(true)
                        .build())
                .build();

        // Solve
        RouteOptimizationResponse response = vrpSolver.solve(request);

        // Verification
        assertNotNull(response);
        assertEquals(RouteOptimizationResponse.OptimizationStatus.COMPLETED, response.getStatus());
        assertFalse(response.getRoutes().isEmpty());

        // The solver should pick STOP2 first because its time window is earlier,
        // even though STOP1 is closer to the depot (Bangalore).
        List<RouteOptimizationResponse.RouteStop> optimizedStops = response.getRoutes().get(0).getStops();
        assertEquals("STOP2", optimizedStops.get(0).getStopId());
    }
}
