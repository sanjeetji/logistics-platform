# Tenant Service API Documentation

## Purpose
The Tenant Service manages multi-tenancy records, including B2B client configurations, billing setings, and service level agreements (SLAs).

## Access Details
- **Base URL**: `http://localhost:8082` (Check discovery for exact port if not 8082)
- **Gateway URL**: `http://localhost:8080/api/v1/tenants`
- **Swagger UI**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tenants` | Onboard a new business tenant |
| GET | `/api/v1/tenants/{tenantId}` | Get tenant configuration and status |
| GET | `/api/v1/tenants` | List all tenants (Super Admin) |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Identity/Tenants`
