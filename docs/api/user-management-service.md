# User Management Service API Documentation

## Purpose
The User Management Service manages user profiles, roles, permissions, and organizational hierarchies. It was consolidated from the legacy `user-service`, `role-permission-service`, and `user-preferences`.

## Access Details
- **Base URL**: `http://localhost:8081` (shares port/context with Auth in some environments, or check gateway)
- **Gateway URL**: `http://localhost:8080/api/v1/users`
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users/{userId}` | Get detailed user profile |
| PUT | `/api/v1/users/{userId}` | Update user profile |
| GET | `/api/v1/users/tenant/{tenantId}` | List users for a tenant |
| GET | `/api/v1/preferences/user/{userId}` | Get user-specific settings |
| PUT | `/api/v1/preferences/user/{userId}` | Update user-specific settings |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `User Management`
