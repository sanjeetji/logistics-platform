# Parcel Service (B2C Adapter) API Documentation

## Purpose
The Parcel Service is the adapter for consumer-facing (B2C) operations. It handles small package delivery logic and creates corresponding records in the Core Order Service.

## Access Details
- **Base URL**: `http://localhost:8120` (Check discovery for exact port)
- **Gateway URL**: `http://localhost:8080/api/v1/parcels`
- **Swagger UI**: [http://localhost:8120/swagger-ui/index.html](http://localhost:8120/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/parcels` | Create a new parcel record |
| GET | `/api/v1/parcels/{trackingNumber}` | Get parcel by tracking number |
| GET | `/api/v1/parcels` | List all parcels |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Fulfillment/Parcels`
