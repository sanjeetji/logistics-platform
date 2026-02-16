import requests
import json

def test_route_optimization():
    url = "http://localhost:8092/api/v1/ml/optimize/route"
    
    payload = {
        "depot": {"lat": 28.6139, "lon": 77.2090}, # New Delhi
        "orders": [
            {"id": "ORD1", "lat": 28.6210, "lon": 77.2150, "weight": 10},
            {"id": "ORD2", "lat": 28.6300, "lon": 77.2200, "weight": 20},
            {"id": "ORD3", "lat": 28.6050, "lon": 77.2000, "weight": 15},
            {"id": "ORD4", "lat": 28.6400, "lon": 77.2300, "weight": 5}
        ],
        "vehicles": [
            {"id": "V1", "lat": 28.6139, "lon": 77.2090, "capacity": 30},
            {"id": "V2", "lat": 28.6139, "lon": 77.2090, "capacity": 30}
        ]
    }
    
    print(f"Sending request to {url}...")
    try:
        # Note: We need the service running to test via requests.
        # For offline logic test, we can directly call the model.
        from app.models.route_optimization import RouteOptimizationModel
        model = RouteOptimizationModel()
        
        result = model.solve_vrp(
            depot=payload["depot"],
            orders=payload["orders"],
            vehicles=payload["vehicles"]
        )
        
        print("\nOptimization Result:")
        print(json.dumps(result, indent=2))
        
        if result["status"] == "OPTIMAL":
            print("\nTest PASSED: Found optimal routes.")
            for route in result["routes"]:
                print(f"Vehicle {route['vehicle_id']}: {route['route_order_ids']} (Dist: {route['distance_meters']}m)")
        else:
            print("\nTest FAILED: No solution found.")
            
    except Exception as e:
        print(f"Error during test: {e}")

if __name__ == "__main__":
    test_route_optimization()
