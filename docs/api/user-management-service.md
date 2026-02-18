# User Management Service API Documentation

## Purpose
The User Management Service manages user profiles, roles, permissions, and organizational hierarchies. It was consolidated from the legacy `user-service`, `role-permission-service`, and `user-preferences`.

## Access Details
- **Base URL**: `http://localhost:8081` (shares port/context with Auth in some environments, or check gateway)
- **Gateway URL**: `http://localhost:8080/api/users`
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{userId}` | Get detailed user profile |
| PUT | `/api/users/{userId}` | Update user profile |
| GET | `/api/roles` | List available system roles |
| POST | `/api/users/{userId}/roles` | Assign roles to a user |
| GET | `/api/users/preferences` | Get user-specific settings |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `User Management`
