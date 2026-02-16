from ortools.constraint_solver import routing_enums_pb2
from ortools.constraint_solver import pywrapcp
import math
from typing import List, Dict, Any, Tuple

class RouteOptimizationModel:
    def __init__(self):
        pass

    def calculate_distance_matrix(self, locations: List[Dict[str, float]]) -> List[List[int]]:
        """
        Calculates the distance matrix between all pairs of locations using Haversine formula.
        Returns distances in meters.
        """
        num_locations = len(locations)
        distance_matrix = [[0] * num_locations for _ in range(num_locations)]
        
        for i in range(num_locations):
            for j in range(num_locations):
                if i == j:
                    distance_matrix[i][j] = 0
                else:
                    dist = self._haversine(
                        locations[i]['lat'], locations[i]['lon'],
                        locations[j]['lat'], locations[j]['lon']
                    )
                    distance_matrix[i][j] = int(dist * 1000) # Convert km to meters
        return distance_matrix

    def _haversine(self, lat1, lon1, lat2, lon2):
        R = 6371  # radius of Earth in km
        phi1, phi2 = math.radians(lat1), math.radians(lat2) 
        dphi = math.radians(lat2 - lat1)
        dlambda = math.radians(lon2 - lon1)
        
        a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2)*math.sin(dlambda/2)**2
        return 2*R*math.atan2(math.sqrt(a), math.sqrt(1 - a))

    def solve_vrp(self, depot: Dict[str, float], orders: List[Dict[str, Any]], vehicles: List[Dict[str, Any]]) -> Dict[str, Any]:
        """
        Solves the Vehicle Routing Problem.
        
        Args:
            depot: Dict with 'lat', 'lon'
            orders: List of dicts with 'id', 'lat', 'lon', 'weight' (optional)
            vehicles: List of dicts with 'id', 'capacity' (optional)
            
        Returns:
            Dict containing routes for each vehicle.
        """
        # 1. Prepare Data
        # Locations: [Depot, Order1, Order2, ...]
        locations = [{'lat': depot['lat'], 'lon': depot['lon']}] + \
                   [{'lat': o['lat'], 'lon': o['lon']} for o in orders]
        
        # Requests mapping (Order Index in Input List -> Node Index in Routing Model)
        # Node 0 is Depot. Node i is Order i-1.
        
        distance_matrix = self.calculate_distance_matrix(locations)
        num_vehicles = len(vehicles)
        depot_index = 0
        
        # Create Routing Index Manager
        manager = pywrapcp.RoutingIndexManager(len(distance_matrix), num_vehicles, depot_index)
        
        # Create Routing Model
        routing = pywrapcp.RoutingModel(manager)
        
        # Create and register a transit callback
        def distance_callback(from_index, to_index):
            from_node = manager.IndexToNode(from_index)
            to_node = manager.IndexToNode(to_index)
            return distance_matrix[from_node][to_node]
            
        transit_callback_index = routing.RegisterTransitCallback(distance_callback)
        
        # Define cost of each arc
        routing.SetArcCostEvaluatorOfAllVehicles(transit_callback_index)
        
        # Add Capacity Constraints (Optional)
        # Assumes all orders have weight, all vehicles have capacity
        # For MVP, we treat capacity as number of stops if weight not provided
        # or just distance minimization if no capacity provided.
        
        # Setting search parameters
        search_parameters = pywrapcp.DefaultRoutingSearchParameters()
        search_parameters.first_solution_strategy = (
            routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC)
        
        # Solve the problem
        solution = routing.SolveWithParameters(search_parameters)
        
        # Format the solution
        if solution:
            return self._format_solution(manager, routing, solution, orders, vehicles)
        else:
            return {"status": "NO_SOLUTION", "routes": []}

    def _format_solution(self, manager, routing, solution, orders, vehicles):
        routes = []
        total_distance = 0
        
        for vehicle_id in range(len(vehicles)):
            index = routing.Start(vehicle_id)
            route = []
            route_distance = 0
            
            while not routing.IsEnd(index):
                node_index = manager.IndexToNode(index)
                # Skip depot (node 0) in the actual route list if we only want orders
                if node_index != 0:
                    # Adjust index to match input orders list (node_index - 1)
                    order_data = orders[node_index - 1]
                    route.append(order_data['id'])
                
                previous_index = index
                index = solution.Value(routing.NextVar(index))
                route_distance += routing.GetArcCostForVehicle(previous_index, index, vehicle_id)
                
            routes.append({
                "vehicle_id": vehicles[vehicle_id]['id'],
                "route_order_ids": route,
                "distance_meters": route_distance
            })
            total_distance += route_distance
            
        return {
            "status": "OPTIMAL",
            "total_distance_meters": total_distance,
            "routes": routes
        }
