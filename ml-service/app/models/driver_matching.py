from typing import List, Dict, Any
import logging
from math import radians, sin, cos, sqrt, atan2

logger = logging.getLogger(__name__)

class DriverMatchingModel:
    """
    Heuristic-based driver scoring model.
    Future improvement: Replace with trained ML model (Learning to Rank).
    """

    def __init__(self):
        # Weights for different factors
        self.w_distance = 0.5
        self.w_rating = 0.3
        self.w_acceptance_rate = 0.2

    def score_drivers(self, order_details: Dict[str, Any], candidates: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Score and rank drivers for a given order.
        """
        logger.info(f"Scoring {len(candidates)} drivers for order {order_details.get('order_id')}")
        
        scored_candidates = []
        
        pickup_lat = order_details.get("pickup_lat")
        pickup_lng = order_details.get("pickup_lng")
        
        for driver in candidates:
            # 1. Calculate Distance Score (closer is better)
            distance = self._calculate_haversine(
                pickup_lat, pickup_lng, 
                driver.get("current_lat"), driver.get("current_lng")
            )
            
            # Normalize distance (assuming max relevant distance is 20km)
            # Score 1.0 for 0km, 0.0 for >20km
            distance_score = max(0, 1 - (distance / 20.0))
            
            # 2. Rating Score (normalized 0-1)
            rating = driver.get("rating", 5.0)
            rating_score = rating / 5.0
            
            # 3. Acceptance Rate Score (normalized 0-1)
            # Assuming acceptance_rate is 0-100 or 0.0-1.0. Let's assume 0.0-1.0
            acceptance_rate = driver.get("acceptance_rate", 1.0)
            acceptance_score = acceptance_rate
            
            # 4. Vehicle Fit (Binary constraint, usually filtered before, but boosting preference here)
            # For now, simplistic boolean multiplier
            vehicle_score = 1.0
            if order_details.get("required_vehicle") and \
               order_details.get("required_vehicle") != driver.get("vehicle_type"):
                 vehicle_score = 0.0 # Should have been filtered out, but safeguard
            
            # Aggregate Score
            total_score = (
                (self.w_distance * distance_score) + 
                (self.w_rating * rating_score) + 
                (self.w_acceptance_rate * acceptance_score)
            ) * vehicle_score
            
            scored_candidates.append({
                "driver_id": driver.get("driver_id"),
                "score": round(total_score, 4),
                "distance_km": round(distance, 2),
                "metadata": {
                    "distance_score": round(distance_score, 2),
                    "rating_score": round(rating_score, 2)
                }
            })
            
        # Sort by score descending
        scored_candidates.sort(key=lambda x: x["score"], reverse=True)
        
        return scored_candidates

    def _calculate_haversine(self, lat1, lon1, lat2, lon2):
        """
        Calculate the great circle distance between two points 
        on the earth (specified in decimal degrees)
        """
        # convert decimal degrees to radians 
        lon1, lat1, lon2, lat2 = map(radians, [float(lon1), float(lat1), float(lon2), float(lat2)])

        # haversine formula 
        dlon = lon2 - lon1 
        dlat = lat2 - lat1 
        a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
        c = 2 * atan2(sqrt(a), sqrt(1-a)) 
        r = 6371 # Radius of earth in kilometers.
        return c * r
