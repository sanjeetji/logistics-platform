# Geo Service API Documentation

## Purpose
The Geo Service provides routing, distance calculation, and geocoding capabilities. It uses OSRM (Open Source Routing Machine) as its backend engine.

## Access Details
- **Base URL**: `http://localhost:8105` (Check discovery for exact port)
- **Gateway URL**: `http://localhost:8080/api/geo`
- **Swagger UI**: [http://localhost:8105/swagger-ui/index.html](http://localhost:8105/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/geo/distance` | Calculate OSRM distance between coordinates |
| POST | `/api/geo/geocode` | Convert address to GPS coordinates |
| POST | `/api/geo/reverse-geocode` | Convert coordinates to address |
| GET | `/api/geo/route` | Get full polyline for a route |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Shared - Geo Service`
