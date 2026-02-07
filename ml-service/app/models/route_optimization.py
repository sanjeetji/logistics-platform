"""
ML-based Route Optimization
Uses ML to predict optimal routes considering traffic patterns
"""
import numpy as np
from typing import List, Tuple
import logging

logger = logging.getLogger(__name__)

class MLRouteOptimizer:
    def __init__(self):
        self.model = None
    
    def optimize_route(self, start_location: dict, stops: List[dict], 
                      vehicle_type: str) -> dict:
        """
        Optimize route using ML predictions
        
        Args:
            start_location: {lat, lng, stop_id}
            stops: List of {lat, lng, stop_id}
            vehicle_type: Vehicle type
        
        Returns:
            dict with optimized route
        """
        logger.info(f"Optimizing route for {len(stops)} stops")
        
        # For now, use greedy nearest neighbor with ML-predicted times
        # In production, use more sophisticated algorithms
        
        current_location = start_location
        remaining_stops = stops.copy()
        route_sequence = []
        total_distance = 0
        total_time = 0
        
        while remaining_stops:
            # Find nearest stop
            nearest_stop, distance, time = self._find_nearest_stop(
                current_location, remaining_stops, vehicle_type
            )
            
            route_sequence.append(nearest_stop['stop_id'])
            total_distance += distance
            total_time += time
            
            current_location = nearest_stop
            remaining_stops.remove(nearest_stop)
        
        # Calculate optimization score (0-1, higher is better)
        optimization_score = self._calculate_optimization_score(
            total_distance, total_time, len(stops)
        )
        
        return {
            "stop_sequence": route_sequence,
            "total_distance_km": round(total_distance, 2),
            "estimated_time_minutes": int(total_time),
            "optimization_score": round(optimization_score, 3)
        }
    
    def _find_nearest_stop(self, current: dict, stops: List[dict], 
                          vehicle_type: str) -> Tuple[dict, float, float]:
        """Find nearest stop using ML-predicted travel time"""
        from app.models.delivery_time import DeliveryTimePredictor
        
        time_predictor = DeliveryTimePredictor()
        
        best_stop = None
        best_distance = float('inf')
        best_time = float('inf')
        
        for stop in stops:
            # Predict travel time
            prediction = time_predictor.predict(
                current['lat'], current['lng'],
                stop['lat'], stop['lng'],
                vehicle_type, "AFTERNOON"  # Default time
            )
            
            distance = prediction['factors']['distance_km']
            time = prediction['predicted_time_minutes']
            
            # Use time as primary metric (ML-enhanced)
            if time < best_time:
                best_time = time
                best_distance = distance
                best_stop = stop
        
        return best_stop, best_distance, best_time
    
    def _calculate_optimization_score(self, distance: float, 
                                     time: float, num_stops: int) -> float:
        """
        Calculate optimization score
        Higher score = better optimization
        """
        # Ideal metrics (benchmarks)
        ideal_distance_per_stop = 5  # km
        ideal_time_per_stop = 15     # minutes
        
        # Calculate efficiency
        actual_distance_per_stop = distance / max(num_stops, 1)
        actual_time_per_stop = time / max(num_stops, 1)
        
        # Score based on how close to ideal
        distance_score = min(1.0, ideal_distance_per_stop / actual_distance_per_stop)
        time_score = min(1.0, ideal_time_per_stop / actual_time_per_stop)
        
        # Weighted average
        score = (distance_score * 0.4 + time_score * 0.6)
        
        return max(0.0, min(1.0, score))
