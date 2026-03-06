# B2B Order Service (Adapter) API Documentation

## Purpose
The B2B Order Service acts as an adapter for enterprise clients. It translates bulk shipment requests into Core Order records and manages B2B-specific extensions like warehouse-dock scheduling.

## Access Details
- **Base URL**: `http://localhost:8118`
- **Gateway URL**: `http://localhost:8080/api/v1/b2b/orders`
- **Swagger UI**: [http://localhost:8118/swagger-ui/index.html](http://localhost:8118/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/b2b/orders` | Create B2B order |
| POST | `/api/v1/b2b/orders/sync` | Sync order from external system |
| POST | `/api/v1/b2b/orders/bulk/csv` | Bulk upload via CSV |
| GET | `/api/v1/b2b/orders/sla/{status}` | List orders by SLA status |
| GET | `/api/v1/b2b/orders/sla-report` | Generate SLA compliance report |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `B2B & Inventory/B2B Orders`
