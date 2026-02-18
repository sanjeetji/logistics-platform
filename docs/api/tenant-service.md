# Tenant Service API Documentation

## Purpose
The Tenant Service manages multi-tenancy records, including B2B client configurations, billing setings, and service level agreements (SLAs).

## Access Details
- **Base URL**: `http://localhost:8082` (Check discovery for exact port if not 8082)
- **Gateway URL**: `http://localhost:8080/api/tenants`
- **Swagger UI**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tenants` | Onboard a new business tenant |
| GET | `/api/tenants/{tenantId}` | Get tenant configuration and status |
| PUT | `/api/tenants/{tenantId}/config` | Update tenant-specific rules |
| GET | `/api/tenants/active` | List all active tenants |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Tenant Service`
