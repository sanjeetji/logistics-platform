"""
API Routes for ML Service
"""
from fastapi import APIRouter, HTTPException, status
from app.api.schemas import *
from app.models.demand_prediction import DemandPredictor
from app.models.delivery_time import DeliveryTimePredictor
from app.models.route_optimization import MLRouteOptimizer
from app.models.dynamic_pricing import DynamicPricingModel
import logging

logger = logging.getLogger(__name__)

router = APIRouter()

# Initialize models
demand_predictor = DemandPredictor()
delivery_predictor = DeliveryTimePredictor()
route_optimizer = MLRouteOptimizer()
pricing_model = DynamicPricingModel()

@router.post("/predict/demand", response_model=DemandPredictionResponse)
async def predict_demand(request: DemandPredictionRequest):
    """
    Predict order demand for a region and date
    
    - **region**: Region code (e.g., NORTH, SOUTH, EAST, WEST)
    - **date**: Target date for prediction
    - **historical_days**: Number of historical days to consider
    """
    try:
        logger.info(f"Demand prediction request for region: {request.region}, date: {request.date}")
        
        result = demand_predictor.predict(
            region=request.region,
            target_date=request.date
        )
        
        return DemandPredictionResponse(
            region=request.region,
            predicted_date=request.date,
            predicted_demand=result["predicted_demand"],
            confidence=result["confidence"],
            factors=result["factors"]
        )
    except Exception as e:
        logger.error(f"Error in demand prediction: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Prediction failed: {str(e)}"
        )

@router.post("/predict/delivery-time", response_model=DeliveryTimePredictionResponse)
async def predict_delivery_time(request: DeliveryTimePredictionRequest):
    """
    Predict delivery time based on route and conditions
    
    - **pickup_lat/lng**: Pickup location coordinates
    - **delivery_lat/lng**: Delivery location coordinates
    - **vehicle_type**: BIKE, CAR, VAN, TRUCK
    - **time_of_day**: MORNING, AFTERNOON, EVENING, NIGHT
    - **weather_condition**: CLEAR, RAIN, FOG (optional)
    """
    try:
        logger.info(f"Delivery time prediction: {request.vehicle_type} at {request.time_of_day}")
        
        result = delivery_predictor.predict(
            pickup_lat=request.pickup_lat,
            pickup_lng=request.pickup_lng,
            delivery_lat=request.delivery_lat,
            delivery_lng=request.delivery_lng,
            vehicle_type=request.vehicle_type,
            time_of_day=request.time_of_day,
            weather_condition=request.weather_condition
        )
        
        return DeliveryTimePredictionResponse(
            predicted_time_minutes=result["predicted_time_minutes"],
            confidence=result["confidence"],
            factors=result["factors"]
        )
    except Exception as e:
        logger.error(f"Error in delivery time prediction: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Prediction failed: {str(e)}"
        )

@router.post("/optimize/route", response_model=RouteOptimizationResponse)
async def optimize_route(request: RouteOptimizationRequest):
    """
    Optimize delivery route using ML predictions
    
    - **start_location**: Starting point
    - **stops**: List of delivery stops
    - **vehicle_type**: Vehicle type
    - **max_stops**: Maximum stops per route
    """
    try:
        logger.info(f"Route optimization for {len(request.stops)} stops")
        
        # Convert Pydantic models to dicts
        start_dict = {
            "lat": request.start_location.lat,
            "lng": request.start_location.lng,
            "stop_id": request.start_location.stop_id
        }
        
        stops_list = [
            {"lat": stop.lat, "lng": stop.lng, "stop_id": stop.stop_id}
            for stop in request.stops
        ]
        
        result = route_optimizer.optimize_route(
            start_location=start_dict,
            stops=stops_list,
            vehicle_type=request.vehicle_type
        )
        
        optimized = OptimizedRoute(**result)
        
        return RouteOptimizationResponse(
            optimized_route=optimized,
            alternative_routes=[]  # Can add alternative routes in future
        )
    except Exception as e:
        logger.error(f"Error in route optimization: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Optimization failed: {str(e)}"
        )

@router.post("/pricing/calculate", response_model=PricingResponse)
async def calculate_dynamic_pricing(request: PricingRequest):
    """
    Calculate dynamic pricing with surge
    
    - **region**: Region code
    - **distance_km**: Distance in kilometers
    - **vehicle_type**: Vehicle type
    - **time_of_day**: Time of day
    - **current_demand**: Current order demand
    """
    try:
        logger.info(f"Pricing calculation for {request.region}: {request.distance_km}km")
        
        result = pricing_model.calculate_price(
            region=request.region,
            distance_km=request.distance_km,
            vehicle_type=request.vehicle_type,
            time_of_day=request.time_of_day,
            current_demand=request.current_demand
        )
        
        return PricingResponse(**result)
    except Exception as e:
        logger.error(f"Error in pricing calculation: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Pricing calculation failed: {str(e)}"
        )

@router.post("/train/demand", response_model=TrainingResponse)
async def train_demand_model(request: TrainingRequest):
    """
    Trigger demand model training (admin only)
    
    - **model_type**: Type of model to train
    - **data_start_date**: Start date for training data
    - **data_end_date**: End date for training data
    """
    try:
        logger.info(f"Training request for {request.model_type} model")
        
        # In production, this would:
        # 1. Fetch data from database
        # 2. Train model asynchronously
        # 3. Save model
        # 4. Return training metrics
        
        return TrainingResponse(
            status="initiated",
            model_type=request.model_type,
            accuracy_score=0.0,
            training_samples=0,
            message="Model training initiated. Check logs for progress."
        )
    except Exception as e:
        logger.error(f"Error in model training: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Training failed: {str(e)}"
        )

@router.get("/models/status")
async def get_models_status():
    """Get status of all ML models"""
    import os
    
    models_dir = "trained_models"
    models = {}
    
    model_files = {
        "demand": "demand_model.pkl",
        "delivery_time": "delivery_time_model.pkl",
        "route": "route_model.pkl"
    }
    
    for model_name, filename in model_files.items():
        path = os.path.join(models_dir, filename)
        models[model_name] = {
            "loaded": os.path.exists(path),
            "path": path
        }
    
    return {
        "models": models,
        "service_status": "operational"
    }
