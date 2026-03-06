# Geo Service API Documentation

## Purpose
The Geo Service provides routing, distance calculation, and geocoding capabilities. It uses OSRM (Open Source Routing Machine) as its backend engine.

## Access Details
- **Base URL**: `http://localhost:8105` (Check discovery for exact port)
- **Gateway URL**: `http://localhost:8080/api/v1/geo`
- **Swagger UI**: [http://localhost:8105/swagger-ui/index.html](http://localhost:8105/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/geo/distance` | Calculate distance between coordinates |
| GET | `/api/v1/geo/geocode` | Convert address to GPS coordinates |
| GET | `/api/v1/geo/reverse` | Convert coordinates to address |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Fulfillment/Geo`
