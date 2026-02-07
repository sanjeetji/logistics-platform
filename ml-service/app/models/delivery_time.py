"""
Delivery Time Prediction Model
Predicts delivery time based on distance, traffic, and other factors
"""
import numpy as np
from sklearn.ensemble import GradientBoostingRegressor
import joblib
import logging
import os
from math import radians, cos, sin, asin, sqrt

logger = logging.getLogger(__name__)

class DeliveryTimePredictor:
    def __init__(self, model_path="trained_models/delivery_time_model.pkl"):
        self.model = None
        self.model_path = model_path
        self.feature_names = ['distance_km', 'vehicle_speed', 'time_factor', 'weather_factor']
    
    def predict(self, pickup_lat: float, pickup_lng: float,
                delivery_lat: float, delivery_lng: float,
                vehicle_type: str, time_of_day: str,
                weather_condition: str = "CLEAR") -> dict:
        """
        Predict delivery time
        
        Args:
            pickup_lat, pickup_lng: Pickup coordinates
            delivery_lat, delivery_lng: Delivery coordinates
            vehicle_type: BIKE, CAR, VAN, TRUCK
            time_of_day: MORNING, AFTERNOON, EVENING, NIGHT
            weather_condition: CLEAR, RAIN, FOG
        
        Returns:
            dict with predicted time and factors
        """
        # Calculate distance
        distance_km = self._haversine_distance(
            pickup_lat, pickup_lng, delivery_lat, delivery_lng
        )
        
        # Get vehicle speed
        vehicle_speed = self._get_vehicle_speed(vehicle_type)
        
        # Get time factor (traffic)
        time_factor = self._get_time_factor(time_of_day)
        
        # Get weather factor
        weather_factor = self._get_weather_factor(weather_condition)
        
        # Calculate base time
        base_time = (distance_km / vehicle_speed) * 60  # Convert to minutes
        
        # Apply factors
        adjusted_time = base_time * time_factor * weather_factor
        
        # Add buffer for stops, loading/unloading
        buffer_time = 10  # minutes
        final_time = int(adjusted_time + buffer_time)
        
        # Calculate confidence
        confidence = 0.80 if distance_km < 50 else 0.70
        
        factors = {
            "distance_km": round(distance_km, 2),
            "vehicle_type": vehicle_type,
            "vehicle_speed_kmh": vehicle_speed,
            "time_of_day": time_of_day,
            "traffic_factor": time_factor,
            "weather_condition": weather_condition,
            "weather_factor": weather_factor,
            "base_time_minutes": int(base_time),
            "buffer_minutes": buffer_time
        }
        
        return {
            "predicted_time_minutes": final_time,
            "confidence": confidence,
            "factors": factors
        }
    
    def _haversine_distance(self, lat1: float, lon1: float, 
                           lat2: float, lon2: float) -> float:
        """
        Calculate distance between two points using Haversine formula
        Returns distance in kilometers
        """
        # Convert to radians
        lat1, lon1, lat2, lon2 = map(radians, [lat1, lon1, lat2, lon2])
        
        # Haversine formula
        dlat = lat2 - lat1
        dlon = lon2 - lon1
        a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
        c = 2 * asin(sqrt(a))
        
        # Radius of earth in kilometers
        r = 6371
        
        return c * r
    
    def _get_vehicle_speed(self, vehicle_type: str) -> float:
        """Get average speed for vehicle type (km/h)"""
        speeds = {
            "BIKE": 25,
            "CAR": 40,
            "VAN": 35,
            "TRUCK": 30
        }
        return speeds.get(vehicle_type, 30)
    
    def _get_time_factor(self, time_of_day: str) -> float:
        """Get traffic factor based on time of day"""
        factors = {
            "MORNING": 1.3,    # Rush hour
            "AFTERNOON": 1.0,  # Normal traffic
            "EVENING": 1.4,    # Peak rush hour
            "NIGHT": 0.8       # Light traffic
        }
        return factors.get(time_of_day, 1.0)
    
    def _get_weather_factor(self, weather_condition: str) -> float:
        """Get delay factor based on weather"""
        factors = {
            "CLEAR": 1.0,
            "RAIN": 1.2,
            "FOG": 1.3,
            "STORM": 1.5
        }
        return factors.get(weather_condition, 1.0)
