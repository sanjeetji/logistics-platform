# Unified BFF Service API Documentation

## Purpose
The Unified BFF (Backend-for-Frontend) is the single entry point for all client applications (Mobile, Web-B2C, Web-B2B). It aggregates data from multiple microservices and provides optimized responses.

## Access Details
- **Base URL**: `http://localhost:8080` (Directly or via Gateway mapping)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Key Endpoints

### Mobile Channel
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/auth/me` | Aggregated driver profile + stats |
| GET | `/api/v1/orders/order/{orderId}` | Order details for mobile |

### B2C Channel
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bff/b2c/orders` | Create order through Parcel adapter |
| GET | `/api/bff/b2c/tracking/{id}` | Public tracking with Geo data |

### Dashboard (Aggregated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/snapshot` | Aggregated stats for enterprise dashboard |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Platform Support`
