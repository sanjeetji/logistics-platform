import random
from typing import Dict, Any

class ETAPredictionModel:
    def __init__(self):
        # Base speed in km/h for different vehicle types
        self.average_speeds = {
            "BIKE": 30.0,
            "CAR": 40.0,
            "VAN": 35.0,
            "TRUCK": 25.0
        }
        # Traffic multipliers
        self.traffic_factors = {
            "LOW": 1.0,
            "MEDIUM": 1.2,
            "HIGH": 1.5,
            "VERY_HIGH": 2.0
        }

    def predict_eta(self, distance_km: float, vehicle_type: str, traffic_condition: str = "MEDIUM") -> Dict[str, Any]:
        """
        Predict ETA based on distance, vehicle type, and traffic.
        Returns ETA in minutes.
        """
        avg_speed = self.average_speeds.get(vehicle_type.upper(), 30.0)
        traffic_multiplier = self.traffic_factors.get(traffic_condition.upper(), 1.2)
        
        # Calculate base time in hours
        base_time_hours = distance_km / avg_speed
        
        # Apply traffic factor
        adjusted_time_hours = base_time_hours * traffic_multiplier
        
        # Convert to minutes
        estimated_minutes = int(adjusted_time_hours * 60)
        
        # Add a small buffer (5-10 mins) for pickup/dropoff handling
        buffer_minutes = random.randint(5, 10)
        total_minutes = estimated_minutes + buffer_minutes
        
        return {
            "distance_km": distance_km,
            "vehicle_type": vehicle_type,
            "traffic_condition": traffic_condition,
            "estimated_minutes": total_minutes,
            "breakdown": {
                "travel_time_minutes": estimated_minutes,
                "buffer_minutes": buffer_minutes,
                "assumed_speed_kmh": avg_speed,
                "traffic_multiplier": traffic_multiplier
            }
        }
