# Order Service (Core) API Documentation

## Purpose
The Order Service is the "Source of Truth" for all shipments in the platform. It manages the lifecycle of orders, multi-stop routing (OrderStops), and global order state transitions.

## Access Details
- **Base URL**: `http://localhost:8085`
- **Gateway URL**: `http://localhost:8080/api/v1/orders`
- **Swagger UI**: [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Create a new master order record |
| GET | `/api/v1/orders/{id}` | Get order by ID |
| GET | `/api/v1/orders/order/{orderId}` | Get order by Business ID |
| PATCH | `/api/v1/orders/{id}/status` | Update order status |
| POST | `/api/v1/orders/{orderId}/assign` | Assign driver to order |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Fulfillment/Orders`
