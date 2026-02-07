package com.logistics.route.service;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Advanced Route Optimization using GraphHopper JSPRIT
 * Solves Vehicle Routing Problem (VRP) with robust constraints.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    private final DistanceMatrixService distanceMatrixService;

    /**
     * Optimize route using JSPRIT VRP Solver
     *
     * @param distanceMatrix N x N distance matrix in meters
     * @param startIndex     Index of the depot/start location
     * @return Ordered list of location indices (Depot -> Stop 1 -> Stop 2 -> ... -> Depot)
     */
    public List<Integer> optimizeRoute(double[][] distanceMatrix, int startIndex) {
        log.info("Starting JSPRIT optimization for {} locations", distanceMatrix.length);

        // 1. Define Vehicle Type
        VehicleType type = VehicleTypeImpl.Builder.newInstance("truck_type")
                .addCapacityDimension(0, 1000) // Max capacity 1000 units
                .setCostPerDistance(1.0)
                .build();

        // 2. Define Vehicle
        // Current implementation assumes single vehicle starting at 'startIndex'
        VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle_1")
                .setStartLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(startIndex, 0))
                .setType(type)
                .setReturnToDepot(true)
                .build();

        // 3. Build Problem
        VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance()
                .addVehicle(vehicle)
                .setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);

        // 4. Add Services (Stops)
        // Skip startIndex as it is the depot
        for (int i = 0; i < distanceMatrix.length; i++) {
            if (i == startIndex) continue;

            Service service = Service.Builder.newInstance("service_" + i)
                    .addSizeDimension(0, 10) // Assume each stop takes 10 units of capacity
                    .setLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(i, 0))
                    .build();
            problemBuilder.addJob(service);
        }

        // 5. Set Routing Cost Matrix (from DistanceMatrix)
        problemBuilder.setRoutingCost(new com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts() {
            @Override
            public double getBackwardTransportCost(com.graphhopper.jsprit.core.problem.Location from, com.graphhopper.jsprit.core.problem.Location to, double departureTime, com.graphhopper.jsprit.core.problem.driver.Driver driver, com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, distanceMatrix, departureTime, vehicle);
            }

            @Override
            public double getBackwardTransportTime(com.graphhopper.jsprit.core.problem.Location from, com.graphhopper.jsprit.core.problem.Location to, double departureTime, com.graphhopper.jsprit.core.problem.driver.Driver driver, com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, distanceMatrix, departureTime, vehicle); // Assuming 1m/s for simplicity if time not critical yet
            }

            @Override
            public double getTransportCost(com.graphhopper.jsprit.core.problem.Location from, com.graphhopper.jsprit.core.problem.Location to, double departureTime, com.graphhopper.jsprit.core.problem.driver.Driver driver, com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, distanceMatrix, departureTime, vehicle);
            }

            @Override
            public double getTransportTime(com.graphhopper.jsprit.core.problem.Location from, com.graphhopper.jsprit.core.problem.Location to, double departureTime, com.graphhopper.jsprit.core.problem.driver.Driver driver, com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
                return getDistance(from, to, distanceMatrix, departureTime, vehicle);
            }
        });

        VehicleRoutingProblem problem = problemBuilder.build();

        // 6. Run Algorithm
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        // 7. Parse Result
        List<Integer> route = new ArrayList<>();
        route.add(startIndex); // Start at depot

        if (bestSolution != null) {
            log.info("Best solution cost: {}", bestSolution.getCost());
            for (VehicleRoute vr : bestSolution.getRoutes()) {
                for (com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity activity : vr.getActivities()) {
                     // Location index is stored in the Location object's index (if coordinate based) or id
                     // Here we used index as the x-coordinate in newInstance(i, 0) - waiting for better ID mapping
                     // But actually newInstance(x,y) stores coords.
                     // The Location ID is usually null unless set.
                     // Let's use the coordinate trick or ID parsing if we set ID as index.
                     // In loop above: Service.Builder.newInstance("service_" + i)
                     // Validation:
                     String jobId = ((com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity.JobActivity) activity).getJob().getId();
                     int index = Integer.parseInt(jobId.replace("service_", ""));
                     route.add(index);
                }
            }
        }

        route.add(startIndex); // Return to depot
        return route;
    }

    private double getDistance(com.graphhopper.jsprit.core.problem.Location from, com.graphhopper.jsprit.core.problem.Location to, double[][] matrix, double time, com.graphhopper.jsprit.core.problem.vehicle.Vehicle vehicle) {
        // We hacked the location construction: newInstance(index, 0)
        // So x coordinate is the index.
        int fromIndex = (int) from.getCoordinate().getX();
        int toIndex = (int) to.getCoordinate().getX();
        return matrix[fromIndex][toIndex];
    }

    /**
     * Calculate optimization score (0-100)
     */
    public double calculateOptimizationScore(double optimizedDistance, double naiveDistance) {
        if (naiveDistance == 0) return 100.0;
        double improvement = ((naiveDistance - optimizedDistance) / naiveDistance) * 100;
        return Math.max(0, Math.min(100, improvement));
    }
}
