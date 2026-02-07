# ML Service - Python FastAPI

Machine Learning service for logistics platform providing demand prediction, delivery time estimation, route optimization, and dynamic pricing.

## Features

- **Demand Prediction**: Forecast order demand by region and date
- **Delivery Time Prediction**: Estimate delivery time based on distance, traffic, weather
- **Route Optimization**: ML-enhanced route planning
- **Dynamic Pricing**: Surge pricing based on demand

## Tech Stack

- Python 3.11
- FastAPI
- scikit-learn
- pandas, numpy
- PostgreSQL (via SQLAlchemy)

## Setup

### Local Development

```bash
# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Set environment variables
cp .env.example .env
# Edit .env with your configuration

# Run service
uvicorn app.main:app --reload --port 8092
```

### Docker

```bash
# Build image
docker build -t logistics-ml-service:latest .

# Run container
docker run -p 8092:8092 logistics-ml-service:latest
```

## API Endpoints

### Predictions

- `POST /api/v1/ml/predict/demand` - Predict order demand
- `POST /api/v1/ml/predict/delivery-time` - Predict delivery time
- `POST /api/v1/ml/optimize/route` - Optimize delivery route
- `POST /api/v1/ml/pricing/calculate` - Calculate dynamic pricing

### Management

- `POST /api/v1/ml/train/demand` - Train demand model
- `GET /api/v1/ml/models/status` - Get model status
- `GET /health` - Health check

## API Documentation

Once running, visit:
- Swagger UI: http://localhost:8092/api/docs
- ReDoc: http://localhost:8092/api/redoc

## Example Usage

### Demand Prediction

```bash
curl -X POST "http://localhost:8092/api/v1/ml/predict/demand" \
  -H "Content-Type: application/json" \
  -d '{
    "region": "NORTH",
    "date": "2026-02-10",
    "historical_days": 30
  }'
```

### Delivery Time Prediction

```bash
curl -X POST "http://localhost:8092/api/v1/ml/predict/delivery-time" \
  -H "Content-Type: application/json" \
  -d '{
    "pickup_lat": 28.7041,
    "pickup_lng": 77.1025,
    "delivery_lat": 28.5355,
    "delivery_lng": 77.3910,
    "vehicle_type": "CAR",
    "time_of_day": "AFTERNOON",
    "weather_condition": "CLEAR"
  }'
```

## Integration with Java Services

Java services can call ML service via REST API:

```java
@Service
public class MLServiceClient {
    @Value("${ml.service.url}")
    private String mlServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public DeliveryTimePrediction predictDeliveryTime(...) {
        String url = mlServiceUrl + "/api/v1/ml/predict/delivery-time";
        return restTemplate.postForObject(url, request, DeliveryTimePrediction.class);
    }
}
```

## Model Training

Models can be trained using historical data:

```bash
curl -X POST "http://localhost:8092/api/v1/ml/train/demand" \
  -H "Content-Type: application/json" \
  -d '{
    "model_type": "demand",
    "data_start_date": "2025-01-01",
    "data_end_date": "2026-01-01"
  }'
```

## Project Structure

```
ml-service/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI application
│   ├── api/
│   │   ├── routes.py        # API endpoints
│   │   └── schemas.py       # Pydantic models
│   ├── models/
│   │   ├── demand_prediction.py
│   │   ├── delivery_time.py
│   │   ├── route_optimization.py
│   │   └── dynamic_pricing.py
│   └── database/
│       └── connection.py
├── trained_models/          # Saved ML models
├── data/                    # Training data
├── tests/                   # Unit tests
├── requirements.txt
├── Dockerfile
└── README.md
```

## Environment Variables

- `DATABASE_URL`: PostgreSQL connection string
- `SERVICE_PORT`: Port to run service (default: 8092)
- `LOG_LEVEL`: Logging level (default: INFO)
- `MODEL_PATH`: Path to saved models

## Notes

- Models use mock predictions until trained with real data
- For production, implement proper model versioning
- Add authentication for training endpoints
- Implement async training for large datasets
