"""
Dynamic Pricing Model
ML-based surge pricing based on demand and supply
"""
import numpy as np
import logging

logger = logging.getLogger(__name__)

class DynamicPricingModel:
    def __init__(self):
        self.base_rates = {
            "BIKE": 5.0,    # per km
            "CAR": 8.0,
            "VAN": 12.0,
            "TRUCK": 15.0
        }
    
    def calculate_price(self, region: str, distance_km: float,
                       vehicle_type: str, time_of_day: str,
                       current_demand: int) -> dict:
        """
        Calculate dynamic price with surge
        
        Args:
            region: Region code
            distance_km: Distance in kilometers
            vehicle_type: Vehicle type
            time_of_day: Time of day
            current_demand: Current order demand
        
        Returns:
            dict with pricing details
        """
        # Base price
        base_rate = self.base_rates.get(vehicle_type, 8.0)
        base_price = base_rate * distance_km
        
        # Calculate surge multiplier
        surge_multiplier = self._calculate_surge(
            current_demand, time_of_day
        )
        
        # Apply surge
        final_price = base_price * surge_multiplier
        
        # Add minimum fare
        minimum_fare = 20.0
        final_price = max(final_price, minimum_fare)
        
        factors = {
            "base_rate_per_km": base_rate,
            "distance_km": distance_km,
            "base_price": round(base_price, 2),
            "demand_level": self._get_demand_level(current_demand),
            "time_of_day": time_of_day,
            "minimum_fare": minimum_fare
        }
        
        return {
            "base_price": round(base_price, 2),
            "surge_multiplier": round(surge_multiplier, 2),
            "final_price": round(final_price, 2),
            "factors": factors
        }
    
    def _calculate_surge(self, demand: int, time_of_day: str) -> float:
        """Calculate surge multiplier based on demand"""
        # Demand thresholds
        if demand < 50:
            demand_surge = 1.0
        elif demand < 100:
            demand_surge = 1.2
        elif demand < 150:
            demand_surge = 1.5
        else:
            demand_surge = 2.0
        
        # Time-based surge
        time_surge = {
            "MORNING": 1.1,
            "AFTERNOON": 1.0,
            "EVENING": 1.2,
            "NIGHT": 0.9
        }.get(time_of_day, 1.0)
        
        # Combined surge
        total_surge = demand_surge * time_surge
        
        # Cap at 3x
        return min(total_surge, 3.0)
    
    def _get_demand_level(self, demand: int) -> str:
        """Get demand level description"""
        if demand < 50:
            return "LOW"
        elif demand < 100:
            return "MEDIUM"
        elif demand < 150:
            return "HIGH"
        else:
            return "VERY_HIGH"
