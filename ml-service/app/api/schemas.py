"""
Pydantic schemas for API requests and responses
"""
from pydantic import BaseModel, Field
from typing import List, Optional, Dict
from datetime import date, datetime

# Demand Prediction
class DemandPredictionRequest(BaseModel):
    region: str = Field(..., description="Region code (e.g., NORTH, SOUTH)")
    date: date = Field(..., description="Target date for prediction")
    historical_days: int = Field(30, description="Days of historical data to use")

class DemandPredictionResponse(BaseModel):
    region: str
    predicted_date: date
    predicted_demand: int
    confidence: float
    factors: Dict[str, any] = {}

# Delivery Time Prediction
class DeliveryTimePredictionRequest(BaseModel):
    pickup_lat: float = Field(..., ge=-90, le=90)
    pickup_lng: float = Field(..., ge=-180, le=180)
    delivery_lat: float = Field(..., ge=-90, le=90)
    delivery_lng: float = Field(..., ge=-180, le=180)
    vehicle_type: str = Field(..., description="BIKE, CAR, VAN, TRUCK")
    time_of_day: str = Field(..., description="MORNING, AFTERNOON, EVENING, NIGHT")
    weather_condition: Optional[str] = Field("CLEAR", description="CLEAR, RAIN, FOG")

class DeliveryTimePredictionResponse(BaseModel):
    predicted_time_minutes: int
    confidence: float
    factors: Dict[str, any]

# Route Optimization
class LocationPoint(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lng: float = Field(..., ge=-180, le=180)
    stop_id: str

class RouteOptimizationRequest(BaseModel):
    start_location: LocationPoint
    stops: List[LocationPoint]
    vehicle_type: str
    max_stops: int = Field(20, description="Maximum stops per route")

class OptimizedRoute(BaseModel):
    stop_sequence: List[str]
    total_distance_km: float
    estimated_time_minutes: int
    optimization_score: float

class RouteOptimizationResponse(BaseModel):
    optimized_route: OptimizedRoute
    alternative_routes: List[OptimizedRoute] = []

# Dynamic Pricing
class PricingRequest(BaseModel):
    region: str
    distance_km: float
    vehicle_type: str
    time_of_day: str
    current_demand: int

class PricingResponse(BaseModel):
    base_price: float
    surge_multiplier: float
    final_price: float
    factors: Dict[str, any]

# Model Training
class TrainingRequest(BaseModel):
    model_type: str = Field(..., description="demand, delivery_time, route")
    data_start_date: date
    data_end_date: date

class TrainingResponse(BaseModel):
    status: str
    model_type: str
    accuracy_score: float
    training_samples: int
    message: str
