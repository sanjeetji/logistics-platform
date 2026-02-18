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
| GET | `/api/bff/mobile/orders/active` | Optimized active orders list for drivers |
| GET | `/api/bff/mobile/profile` | Aggregated driver profile + stats |

### B2C Channel
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bff/b2c/orders` | Create order through Parcel adapter |
| GET | `/api/bff/b2c/tracking/{id}` | Public tracking with Geo data |

### B2B Channel
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bff/b2b/dashboard` | Aggregated stats for enterprise dashboard |
| POST | `/api/bff/b2b/shipments/bulk` | Bulk creation via B2B Order adapter |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Unified BFF`
