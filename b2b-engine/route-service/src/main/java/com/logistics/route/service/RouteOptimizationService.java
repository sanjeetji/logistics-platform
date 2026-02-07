package com.logistics.route.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Route optimization using greedy nearest-neighbor + 2-opt improvement
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    private final DistanceMatrixService distanceMatrixService;

    /**
     * Optimize route using nearest-neighbor heuristic
     */
    public List<Integer> optimizeRoute(double[][] distanceMatrix, int startIndex) {
        int n = distanceMatrix.length;
        List<Integer> route = new ArrayList<>();
        boolean[] visited = new boolean[n];

        // Start from depot
        int current = startIndex;
        route.add(current);
        visited[current] = true;

        // Greedy nearest-neighbor
        for (int i = 1; i < n; i++) {
            int nearest = findNearestUnvisited(distanceMatrix, current, visited);
            if (nearest == -1) break;
            
            route.add(nearest);
            visited[nearest] = true;
            current = nearest;
        }

        // Return to depot if needed
        if (route.size() == n) {
            route.add(startIndex);
        }

        log.info("Initial route distance: {}", calculateRouteDistance(route, distanceMatrix));

        // Apply 2-opt improvement
        route = twoOptImprovement(route, distanceMatrix);

        log.info("Optimized route distance: {}", calculateRouteDistance(route, distanceMatrix));

        return route;
    }

    /**
     * Find nearest unvisited location
     */
    private int findNearestUnvisited(double[][] distanceMatrix, int current, boolean[] visited) {
        int nearest = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < distanceMatrix.length; i++) {
            if (!visited[i] && distanceMatrix[current][i] < minDistance) {
                minDistance = distanceMatrix[current][i];
                nearest = i;
            }
        }

        return nearest;
    }

    /**
     * 2-opt local search improvement
     */
    private List<Integer> twoOptImprovement(List<Integer> route, double[][] distanceMatrix) {
        boolean improved = true;
        int iterations = 0;
        int maxIterations = 100;

        while (improved && iterations < maxIterations) {
            improved = false;
            iterations++;

            for (int i = 1; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    double currentDistance = 
                            distanceMatrix[route.get(i - 1)][route.get(i)] +
                            distanceMatrix[route.get(j)][route.get(j + 1)];

                    double newDistance = 
                            distanceMatrix[route.get(i - 1)][route.get(j)] +
                            distanceMatrix[route.get(i)][route.get(j + 1)];

                    if (newDistance < currentDistance) {
                        // Reverse segment between i and j
                        reverseSegment(route, i, j);
                        improved = true;
                    }
                }
            }
        }

        log.info("2-opt completed in {} iterations", iterations);
        return route;
    }

    /**
     * Reverse route segment
     */
    private void reverseSegment(List<Integer> route, int start, int end) {
        while (start < end) {
            int temp = route.get(start);
            route.set(start, route.get(end));
            route.set(end, temp);
            start++;
            end--;
        }
    }

    /**
     * Calculate total route distance
     */
    public double calculateRouteDistance(List<Integer> route, double[][] distanceMatrix) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += distanceMatrix[route.get(i)][route.get(i + 1)];
        }
        return totalDistance;
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
