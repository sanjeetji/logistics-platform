# Local API Access Guide

This guide shows you how to access the Swagger UI and API documentation for all microservices when running locally.

---

## Quick Start

### 1. Start Services Locally

**Option A: Using Docker Compose** (Recommended)
```bash
cd /Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform
docker-compose up -d
```

**Option B: Run Individual Service**
```bash
cd /Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform/shared-services/notification-service
mvn spring-boot:run
```

### 2. Access Swagger UI

Once a service is running, access its Swagger UI at:
```
http://localhost:{PORT}/swagger-ui.html
```

---

## Service Ports & Swagger URLs

### Infrastructure Services

| Service | Port | Swagger UI | API Docs JSON |
|---------|------|------------|---------------|
| Service Discovery (Eureka) | 8761 | N/A (Eureka Console) | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080/swagger-ui.html | http://localhost:8080/v3/api-docs |
| Config Server | 8888 | N/A | http://localhost:8888/actuator |

---

### Platform Core Services

| Service | Port | Swagger UI | API Docs JSON |
|---------|------|------------|---------------|
| Auth Service | 8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| User Service | 8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| Role Permission Service | 8083 | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| Pricing Service | 8084 | http://localhost:8084/swagger-ui.html | http://localhost:8084/v3/api-docs |
| Customer Service | 8085 | http://localhost:8085/swagger-ui.html | http://localhost:8085/v3/api-docs |
| Driver App Service | 8086 | http://localhost:8086/swagger-ui.html | http://localhost:8086/v3/api-docs |
| Analytics Service (Legacy) | 8091 | http://localhost:8091/swagger-ui.html | http://localhost:8091/v3/api-docs |
| Notification Service (Legacy) | 8093 | http://localhost:8093/swagger-ui.html | http://localhost:8093/v3/api-docs |

---

### B2B Engine Services

| Service | Port | Swagger UI | API Docs JSON |
|---------|------|------------|---------------|
| B2B Order Service | 8087 | http://localhost:8087/swagger-ui.html | http://localhost:8087/v3/api-docs |
| Route Service | 8088 | http://localhost:8088/swagger-ui.html | http://localhost:8088/v3/api-docs |
| Warehouse Service | 8089 | http://localhost:8089/swagger-ui.html | http://localhost:8089/v3/api-docs |
| Compliance Service | 8090 | http://localhost:8090/swagger-ui.html | http://localhost:8090/v3/api-docs |
| Shipment Service | 8095 | http://localhost:8095/swagger-ui.html | http://localhost:8095/v3/api-docs |
| Inventory Service | 8096 | http://localhost:8096/swagger-ui.html | http://localhost:8096/v3/api-docs |
| Team Service | 8097 | http://localhost:8097/swagger-ui.html | http://localhost:8097/v3/api-docs |

---

### Shared Services

| Service | Port | Swagger UI | API Docs JSON |
|---------|------|------------|---------------|
| Geo Service | 8084 | http://localhost:8084/swagger-ui.html | http://localhost:8084/v3/api-docs |
| Integration Service | 8107 | http://localhost:8107/swagger-ui.html | http://localhost:8107/v3/api-docs |
| Chat Service | 8108 | http://localhost:8108/swagger-ui.html | http://localhost:8108/v3/api-docs |
| **Audit Log Service** | **8109** | **http://localhost:8109/swagger-ui.html** | http://localhost:8109/v3/api-docs |
| Master Data Service | 8110 | http://localhost:8110/swagger-ui.html | http://localhost:8110/v3/api-docs |
| **Notification Service** | **8111** | **http://localhost:8111/swagger-ui.html** | http://localhost:8111/v3/api-docs |
| **Analytics Service** | **8112** | **http://localhost:8112/swagger-ui.html** | http://localhost:8112/v3/api-docs |
| **SLA Service** | **8113** | **http://localhost:8113/swagger-ui.html** | http://localhost:8113/v3/api-docs |
| Billing Service | 8114 | http://localhost:8114/swagger-ui.html | http://localhost:8114/v3/api-docs |
| Payment Service | 8115 | http://localhost:8115/swagger-ui.html | http://localhost:8115/v3/api-docs |

**Note**: Services in **bold** are newly implemented with full Swagger documentation.

---

## How to Use Swagger UI

### 1. Access the UI
Open your browser and navigate to the service's Swagger UI URL, for example:
```
http://localhost:8109/swagger-ui.html
```

### 2. Explore Endpoints
- Browse available endpoints organized by controller/tag
- Click on any endpoint to see details
- View request/response schemas
- See example values

### 3. Test Endpoints (Try it out)

#### Without Authentication
For public endpoints:
1. Click on an endpoint (e.g., `GET /api/health`)
2. Click **"Try it out"**
3. Fill in parameters if needed
4. Click **"Execute"**
5. View the response

#### With Authentication (JWT)
For protected endpoints:

**Step 1: Get JWT Token**
```bash
# Login to get token
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@example.com",
    "password": "admin123"
  }'
```

**Step 2: Authorize in Swagger**
1. Copy the `accessToken` from the login response
2. In Swagger UI, click the **"Authorize"** button (top right)
3. Enter: `Bearer YOUR_ACCESS_TOKEN`
4. Click **"Authorize"**
5. Now you can test protected endpoints

---

## Common Endpoints to Test

### Audit Log Service (Port 8109)
```
GET  http://localhost:8109/api/audit/logs
GET  http://localhost:8109/api/audit/logs/ORDER/ORD-123
GET  http://localhost:8109/api/audit/logs/user/USER-456
```

### Notification Service (Port 8111)
```
POST http://localhost:8111/api/notifications/send
GET  http://localhost:8111/api/notifications
GET  http://localhost:8111/api/notifications/{id}
```

### Analytics Service - CO2 Tracking (Port 8112)
```
POST http://localhost:8112/api/analytics/emissions/calculate
GET  http://localhost:8112/api/analytics/emissions/ORDER/ORD-123
GET  http://localhost:8112/api/analytics/emissions/total/ORDER
```

### SLA Service (Port 8113)
```
GET  http://localhost:8113/api/sla/definitions
POST http://localhost:8113/api/sla/definitions
GET  http://localhost:8113/api/sla/breaches
```

---

## Accessing via API Gateway

When all services are running with the API Gateway, you can access them through a single entry point:

```
http://localhost:8080/{service-name}/api/{endpoint}
```

**Examples**:
```
http://localhost:8080/notification-service/api/notifications
http://localhost:8080/audit-log-service/api/audit/logs
http://localhost:8080/analytics-service/api/analytics/emissions/calculate
```

**Aggregated Swagger UI** (if configured):
```
http://localhost:8080/swagger-ui.html
```

---

## Troubleshooting

### Service Not Starting
```bash
# Check if port is already in use
lsof -i :8109

# Kill process using the port
kill -9 <PID>

# Check service logs
docker logs logistics-audit-log-service
```

### Swagger UI Not Loading
1. Verify service is running:
   ```bash
   curl http://localhost:8109/actuator/health
   ```

2. Check if SpringDoc dependency is present:
   ```bash
   grep -r "springdoc" pom.xml
   ```

3. Verify application is configured:
   ```yaml
   springdoc:
     api-docs:
       enabled: true
     swagger-ui:
       enabled: true
   ```

### 404 Not Found
- Ensure you're using the correct context path
- Some services may have a base path like `/api/v1`
- Check the service's `application.yml` for `server.servlet.context-path`

---

## Health Checks

All services expose health endpoints:
```
http://localhost:{PORT}/actuator/health
```

**Examples**:
```bash
curl http://localhost:8109/actuator/health  # Audit Log Service
curl http://localhost:8111/actuator/health  # Notification Service
curl http://localhost:8112/actuator/health  # Analytics Service
curl http://localhost:8113/actuator/health  # SLA Service
```

---

## Quick Reference Commands

### Start All Services
```bash
cd /Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform
docker-compose up -d
```

### Check Running Services
```bash
docker-compose ps
```

### View Service Logs
```bash
docker-compose logs -f notification-service
docker-compose logs -f audit-log-service
docker-compose logs -f analytics-service
```

### Stop All Services
```bash
docker-compose down
```

### Restart a Specific Service
```bash
docker-compose restart notification-service
```

---

## Production URLs (Future)

When you deploy to production, replace `localhost:{PORT}` with your domain:

```
https://api.logistics-platform.com/notification-service/swagger-ui.html
https://api.logistics-platform.com/audit-log-service/swagger-ui.html
https://api.logistics-platform.com/analytics-service/swagger-ui.html
```

---

## Additional Resources

- **Full API Documentation**: `docs/api-documentation.md`
- **Audit Report**: `AUDIT_REPORT.md`
- **Docker Compose**: `docker-compose.yml`
- **Postman Collection**: Export from Swagger UI → Download → OpenAPI JSON

---

**Last Updated**: February 10, 2026  
**For Support**: Check service logs or health endpoints
