# Order Service (Core) API Documentation

## Purpose
The Order Service is the "Source of Truth" for all shipments in the platform. It manages the lifecycle of orders, multi-stop routing (OrderStops), and global order state transitions.

## Access Details
- **Base URL**: `http://localhost:8085`
- **Gateway URL**: `http://localhost:8080/api/orders`
- **Swagger UI**: [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new master order record |
| GET | `/api/orders/{orderId}` | Get order with stops and metadata |
| GET | `/api/orders/tenant/{tenantId}` | List orders for a specific tenant |
| PUT | `/api/orders/{orderId}/status` | Update internal state machine |
| POST | `/api/orders/search` | Advanced filtering of orders |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Core Order Service`
