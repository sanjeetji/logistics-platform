"""
Pydantic schemas for API requests and responses
"""
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import date as DateType, datetime

# Demand Prediction
class DemandPredictionRequest(BaseModel):
    region: str = Field(..., description="Region code (e.g., NORTH, SOUTH)")
    date: DateType = Field(..., description="Target date for prediction")
    historical_days: int = Field(default=30, description="Days of historical data to use")

class DemandPredictionResponse(BaseModel):
    region: str
    predicted_date: DateType
    predicted_demand: int
    confidence: float
    factors: Dict[str, Any] = Field(default_factory=dict)

# Delivery Time Prediction
class DeliveryTimePredictionRequest(BaseModel):
    pickup_lat: float = Field(..., ge=-90, le=90)
    pickup_lng: float = Field(..., ge=-180, le=180)
    delivery_lat: float = Field(..., ge=-90, le=90)
    delivery_lng: float = Field(..., ge=-180, le=180)
    vehicle_type: str = Field(..., description="BIKE, CAR, VAN, TRUCK")
    time_of_day: str = Field(..., description="MORNING, AFTERNOON, EVENING, NIGHT")
    weather_condition: Optional[str] = Field(default="CLEAR", description="CLEAR, RAIN, FOG")

class DeliveryTimePredictionResponse(BaseModel):
    predicted_time_minutes: int
    confidence: float
    factors: Dict[str, Any] = Field(default_factory=dict)

class Location(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lon: float = Field(..., ge=-180, le=180)

# ... (omitted OrderLocation/VehicleLocation since they are fine) ...

# ...

class DriverMatchingResponse(BaseModel):
    request_id: str
    candidates_count: int
    matches: List[Dict[str, Any]]

class OrderLocation(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lon: float = Field(..., ge=-180, le=180)
    id: str
    weight: Optional[float] = 0.0

class VehicleLocation(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lon: float = Field(..., ge=-180, le=180)
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

class OptimizedRoute(BaseModel):
    vehicle_id: str
    route: List[Location]
    distance: float
    eta: int

# Pricing
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
    currency: str

# Driver Matching
class DriverCandidate(BaseModel):
    driver_id: str
    lat: float
    lon: float
    vehicle_type: str
    rating: float
    trips_today: int

class DriverMatchingRequest(BaseModel):
    request_id: str
    order_id: str
    pickup_lat: float
    pickup_lng: float
    required_vehicle: str
    candidates: List[DriverCandidate]

class DriverMatchingResponse(BaseModel):
    request_id: str
    candidates_count: int
    # matches: List[Dict[str, Any]]

# Training
class TrainingRequest(BaseModel):
    model_type: str
    data_start_date: DateType
    data_end_date: DateType

class TrainingResponse(BaseModel):
    status: str
    model_type: str
    accuracy_score: float
    training_samples: int
    message: str
