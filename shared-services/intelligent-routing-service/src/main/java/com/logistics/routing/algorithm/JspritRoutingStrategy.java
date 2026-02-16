package com.logistics.routing.algorithm;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.logistics.routing.dto.*;
import com.logistics.routing.service.DistanceMatrixService;
import com.logistics.routing.service.GreenRoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JspritRoutingStrategy implements RoutingAlgorithm {

    private final DistanceMatrixService distanceMatrixService;
    private final GreenRoutingService greenRoutingService;

    @Override
    public RouteOptimizationRequest.OptimizationType getType() {
        return RouteOptimizationRequest.OptimizationType.VRP_JSPRIT;
    }

    @Override
    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        log.info("Executing JSPRIT VRP optimization");

        // 1. Prepare Data
        // For simplicity in this refactor, we assume request.shipments and
        // request.vehicles are populated
        // Or we fallback to the old logic if orderIds are present (but we encourage
        // using shipments)

        if (request.getShipments() == null || request.getShipments().isEmpty()) {
            throw new IllegalArgumentException("Shipments are required for VRP optimization");
        }

        // Build coordinates for Distance Matrix
        // 0: Start (Depot), 1..N: Pickups & Deliveries
        // JSPRIT uses indices or location IDs. We'll use indices mapped to our
        // coordinates array.

        // Flatten shipments into stops
        List<LocationDTO> stops = new ArrayList<>();
        // Add depot (assume single depot for now from startLocation or vehicle start)
        LocationDTO depot = request.getVehicles().get(0).getStartLocation();
        stops.add(depot);

        for (ShipmentDTO shipment : request.getShipments()) {
            stops.add(shipment.getPickupLocation());
            stops.add(shipment.getDeliveryLocation());
        }

        double[][] coordinates = new double[stops.size()][2];
        for (int i = 0; i < stops.size(); i++) {
            coordinates[i][0] = stops.get(i).getLatitude();
            coordinates[i][1] = stops.get(i).getLongitude();
        }

        // Calculate Matrix
        double[][] distanceMatrix = distanceMatrixService.buildDistanceMatrix(coordinates);

        // 2. Build JSPRIT Problem
        VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance();

        // Map vehicleId to type for CO2 calculation later
        java.util.Map<Long, String> vIdToType = new java.util.HashMap<>();

        // Add Vehicles
        for (VehicleDTO vDto : request.getVehicles()) {
            vIdToType.put(vDto.getId(), vDto.getVehicleType());
            VehicleType type = VehicleTypeImpl.Builder.newInstance("type_" + vDto.getId())
                    .addCapacityDimension(0, vDto.getCapacityWeight())
                    .setCostPerDistance(vDto.getCostPerKm() != null ? vDto.getCostPerKm() : 1.0)
                    .setFixedCost(vDto.getCostPerFixed() != null ? vDto.getCostPerFixed() : 0.0)
                    .build();

            VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle_" + vDto.getId())
                    .setStartLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(0, 0))
                    .setType(type)
                    .setReturnToDepot(true)
                    .build();

            problemBuilder.addVehicle(vehicle);
        }

        // Add Services (Shipments)
        int idx = 1;
        for (ShipmentDTO shipment : request.getShipments()) {
            com.graphhopper.jsprit.core.problem.job.Shipment.Builder jobBuilder = com.graphhopper.jsprit.core.problem.job.Shipment.Builder
                    .newInstance(shipment.getId())
                    .addSizeDimension(0, shipment.getWeight())
                    .setPickupLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(idx, 0))
                    .setDeliveryLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(idx + 1, 0));

            // Time Windows
            if (shipment.getPickupWindowStart() != null && shipment.getPickupWindowEnd() != null) {
                jobBuilder.setPickupTimeWindow(new TimeWindow(shipment.getPickupWindowStart(), shipment.getPickupWindowEnd()));
            }
            if (shipment.getDeliveryWindowStart() != null && shipment.getDeliveryWindowEnd() != null) {
                jobBuilder.setDeliveryTimeWindow(new TimeWindow(shipment.getDeliveryWindowStart(), shipment.getDeliveryWindowEnd()));
            }
            if (shipment.getServiceTimeMinutes() != null) {
                jobBuilder.setPickupServiceTime(shipment.getServiceTimeMinutes() * 60.0);
                jobBuilder.setDeliveryServiceTime(shipment.getServiceTimeMinutes() * 60.0);
            }
            if (shipment.getPriority() != null) {
                jobBuilder.setPriority(shipment.getPriority());
            }

            problemBuilder.addJob(jobBuilder.build());
            idx += 2;
        }

        // Set Routing Cost
        problemBuilder.setRoutingCost(new com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts() {
            @Override
            public double getBackwardTransportCost(com.graphhopper.jsprit.core.problem.Location from,
                    com.graphhopper.jsprit.core.problem.Location to, double departureTime,
                    com.graphhopper.jsprit.core.problem.driver.Driver driver,
                    com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, departureTime, vehicle);
            }

            @Override
            public double getBackwardTransportTime(com.graphhopper.jsprit.core.problem.Location from,
                    com.graphhopper.jsprit.core.problem.Location to, double departureTime,
                    com.graphhopper.jsprit.core.problem.driver.Driver driver,
                    com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, departureTime, vehicle);
            }

            @Override
            public double getTransportCost(com.graphhopper.jsprit.core.problem.Location from,
                    com.graphhopper.jsprit.core.problem.Location to, double departureTime,
                    com.graphhopper.jsprit.core.problem.driver.Driver driver,
                    com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, departureTime, vehicle);
            }

            @Override
            public double getTransportTime(com.graphhopper.jsprit.core.problem.Location from,
                    com.graphhopper.jsprit.core.problem.Location to, double departureTime,
                    com.graphhopper.jsprit.core.problem.driver.Driver driver,
                    com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, departureTime, vehicle);
            }

            @Override
            public double getDistance(com.graphhopper.jsprit.core.problem.Location from,
                    com.graphhopper.jsprit.core.problem.Location to, double departureTime,
                    com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                int fromIdx = (int) from.getCoordinate().getX();
                int toIdx = (int) to.getCoordinate().getX();
                return distanceMatrix[fromIdx][toIdx];
            }
        });

        VehicleRoutingProblem problem = problemBuilder.build();

        // 3. Solve
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        // 4. Transform Response
        List<RouteOptimizationResponse.OptimizedRouteDTO> resultRoutes = new ArrayList<>();
        double totalCost = 0;

        if (bestSolution != null) {
            totalCost = bestSolution.getCost();

            for (VehicleRoute vr : bestSolution.getRoutes()) {
                List<LocationDTO> waypoints = new ArrayList<>();
                // Flatten activities to waypoints
                for (com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity activity : vr
                        .getActivities()) {
                    int locationIdx = (int) activity.getLocation().getCoordinate().getX();
                    waypoints.add(stops.get(locationIdx));
                }

                RouteOptimizationResponse.OptimizedRouteDTO.OptimizedRouteDTOBuilder routeBuilder = RouteOptimizationResponse.OptimizedRouteDTO
                        .builder()
                        .vehicleId(Long.parseLong(vr.getVehicle().getId().replace("vehicle_", "")))
                        .waypoints(waypoints);

                // Calculate Route Distance Manually
                double routeDistance = 0;
                com.graphhopper.jsprit.core.problem.Location lastLoc = vr.getStart().getLocation();
                for (com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity act : vr
                        .getActivities()) {
                    routeDistance += problem.getTransportCosts().getTransportCost(lastLoc, act.getLocation(), 0,
                            vr.getDriver(), vr.getVehicle());
                    lastLoc = act.getLocation();
                }
                if (vr.getEnd() != null) {
                    routeDistance += problem.getTransportCosts().getTransportCost(lastLoc, vr.getEnd().getLocation(), 0,
                            vr.getDriver(), vr.getVehicle());
                }

                routeBuilder.distanceKm(routeDistance / 1000.0);
                resultRoutes.add(routeBuilder.build());
            }
        }

        double totalCo2 = 0;
        if (bestSolution != null) {
            for (RouteOptimizationResponse.OptimizedRouteDTO route : resultRoutes) {
                // Find vehicle type for this vehicleId
                String vehicleType = vIdToType.get(route.getVehicleId());
                java.math.BigDecimal co2 = greenRoutingService.calculateCO2Emission(route.getDistanceKm(), vehicleType);
                route.setCo2EmissionsKg(co2.doubleValue());
                totalCo2 += co2.doubleValue();
            }
        }

        return RouteOptimizationResponse.builder()
                .routes(resultRoutes)
                .totalDistanceKm(totalCost / 1000.0)
                .totalCo2EmissionsKg(totalCo2)
                .algorithm("JSPRIT_VRP")
                .build();
    }
}
