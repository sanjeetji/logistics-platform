# B2B Order Service (Adapter) API Documentation

## Purpose
The B2B Order Service acts as an adapter for enterprise clients. It translates bulk shipment requests into Core Order records and manages B2B-specific extensions like warehouse-dock scheduling.

## Access Details
- **Base URL**: `http://localhost:8118`
- **Gateway URL**: `http://localhost:8080/api/b2b/orders`
- **Swagger UI**: [http://localhost:8118/swagger-ui/index.html](http://localhost:8118/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/b2b/shipments/bulk` | Process bulk CSV/JSON shipments |
| GET | `/api/b2b/batch/{batchId}` | Status of a bulk processing batch |
| GET | `/api/b2b/tenants/{tenantId}/stats` | B2B specific order metrics |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `B2B Order Adapter`
