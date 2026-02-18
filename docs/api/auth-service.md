# Auth Service API Documentation

## Purpose
The Auth Service handles user registration, authentication, token management (JWT), and security-related operations across the platform.

## Access Details
- **Base URL**: `http://localhost:8081`
- **Gateway URL**: `http://localhost:8080/api/auth`
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and get JWT tokens |
| POST | `/api/auth/refresh` | Refresh an expired access token |
| POST | `/api/auth/logout` | Invalidate current session |
| GET | `/api/auth/me` | Get current authenticated user info |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Authentication`
