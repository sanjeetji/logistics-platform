"""
Pydantic schemas for API requests and responses
"""
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
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

class Location(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lon: float = Field(..., ge=-180, le=180)

class OrderLocation(Location):
    id: str
    weight: Optional[float] = 0.0

class VehicleLocation(Location):
    id: str
    capacity: Optional[float] = 100.0

class RouteOptimizationRequest(BaseModel):
    depot: Location
    orders: List[OrderLocation]
    vehicles: List[VehicleLocation]

class VehicleRoute(BaseModel):
    vehicle_id: str
    route_order_ids: List[str]
    distance_meters: int

class RouteOptimizationResponse(BaseModel):
    status: str
    total_distance_meters: int
    routes: List[VehicleRoute]
"""
