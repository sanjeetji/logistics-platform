# Logistics Platform API Documentation

**Version**: 1.0.0  
**Base URL**: `https://api.logistics-platform.com`  
**Documentation Portal**: `/swagger-ui.html` (via API Gateway)

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Common Patterns](#common-patterns)
4. [Core APIs](#core-apis)
5. [B2B APIs](#b2b-apis)
6. [B2C APIs](#b2c-apis)
7. [Shared Services APIs](#shared-services-apis)
8. [Error Handling](#error-handling)
9. [Rate Limiting](#rate-limiting)
10. [Code Examples](#code-examples)

---

## Overview

The Logistics Platform provides a comprehensive suite of REST APIs for managing end-to-end logistics operations including order management, fleet tracking, route optimization, billing, and analytics.

### Architecture
- **Microservices**: 40+ independent services
- **API Gateway**: Single entry point at `https://api.logistics-platform.com`
- **Protocol**: HTTPS only
- **Format**: JSON
- **Documentation**: OpenAPI 3.0 (Swagger)

### Service Discovery
All services are accessible through the API Gateway with the pattern:
```
https://api.logistics-platform.com/{service-name}/api/{resource}
```

---

## Authentication

### JWT Bearer Token
All API requests require a valid JWT token in the Authorization header.

**Obtain Token**:
```http
POST /auth-service/api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "your-password"
}
```

**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 3600
}
```

**Use Token**:
```http
GET /order-service/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### Token Refresh
```http
POST /auth-service/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## Common Patterns

### Pagination
All list endpoints support pagination:

**Request**:
```http
GET /api/orders?page=0&size=20&sort=createdAt,desc
```

**Response**:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false
}
```

### Filtering
Use query parameters for filtering:
```http
GET /api/orders?status=PENDING&customerId=CUST-123&startDate=2026-01-01
```

### Field Selection
Request specific fields to reduce payload:
```http
GET /api/orders?fields=id,status,totalAmount
```

### Sorting
```http
GET /api/orders?sort=createdAt,desc&sort=totalAmount,asc
```

---

## Core APIs

### 1. Authentication Service
**Base Path**: `/auth-service/api/auth`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/login` | POST | Authenticate user and obtain JWT token |
| `/refresh` | POST | Refresh access token using refresh token |
| `/logout` | POST | Invalidate current session |
| `/register` | POST | Register new user account |
| `/reset-password` | POST | Request password reset |

**Swagger UI**: `https://api.logistics-platform.com/auth-service/swagger-ui.html`

---

### 2. Order Service
**Base Path**: `/order-service/api/orders`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | List all orders (paginated) |
| `/` | POST | Create new order |
| `/{orderId}` | GET | Get order details |
| `/{orderId}` | PUT | Update order |
| `/{orderId}` | DELETE | Cancel order |
| `/{orderId}/status` | PATCH | Update order status |
| `/{orderId}/tracking` | GET | Get real-time tracking |

**Example - Create Order**:
```json
POST /order-service/api/orders
{
  "customerId": "CUST-12345",
  "pickupAddress": {
    "street": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102",
    "country": "USA"
  },
  "deliveryAddress": {
    "street": "456 Market St",
    "city": "Oakland",
    "state": "CA",
    "zipCode": "94607",
    "country": "USA"
  },
  "items": [
    {
      "description": "Electronics",
      "weight": 5.5,
      "dimensions": {
        "length": 30,
        "width": 20,
        "height": 15
      }
    }
  ],
  "serviceType": "EXPRESS"
}
```

**Swagger UI**: `https://api.logistics-platform.com/order-service/swagger-ui.html`

---

### 3. Fleet Service
**Base Path**: `/fleet-service/api`

#### Vehicles
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/vehicles` | GET | List all vehicles |
| `/vehicles` | POST | Add new vehicle |
| `/vehicles/{vehicleId}` | GET | Get vehicle details |
| `/vehicles/{vehicleId}/location` | GET | Get current location |
| `/vehicles/{vehicleId}/maintenance` | GET | Get maintenance history |

#### Drivers
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/drivers` | GET | List all drivers |
| `/drivers` | POST | Add new driver |
| `/drivers/{driverId}` | GET | Get driver details |
| `/drivers/{driverId}/assignments` | GET | Get current assignments |
| `/drivers/{driverId}/earnings` | GET | Get earnings report |

**Swagger UI**: `https://api.logistics-platform.com/fleet-service/swagger-ui.html`

---

### 4. Dispatch Service
**Base Path**: `/dispatch-service/api/dispatch`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/assign` | POST | Assign order to driver |
| `/optimize` | POST | Optimize route for multiple orders |
| `/active` | GET | Get active dispatches |
| `/{dispatchId}` | GET | Get dispatch details |
| `/{dispatchId}/complete` | POST | Mark dispatch as completed |

**Swagger UI**: `https://api.logistics-platform.com/dispatch-service/swagger-ui.html`

---

### 5. Pricing Service
**Base Path**: `/pricing-service/api/pricing`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/calculate` | POST | Calculate shipping price |
| `/quote` | POST | Get detailed price quote |
| `/rules` | GET | List pricing rules |
| `/rules` | POST | Create pricing rule (admin) |
| `/discounts` | GET | Get available discounts |

**Example - Calculate Price**:
```json
POST /pricing-service/api/pricing/calculate
{
  "origin": {
    "latitude": 37.7749,
    "longitude": -122.4194
  },
  "destination": {
    "latitude": 37.8044,
    "longitude": -122.2712
  },
  "weight": 10.5,
  "serviceType": "EXPRESS",
  "dimensions": {
    "length": 50,
    "width": 40,
    "height": 30
  }
}
```

**Response**:
```json
{
  "basePrice": 45.00,
  "distanceFee": 12.50,
  "weightFee": 8.00,
  "serviceFee": 15.00,
  "tax": 8.05,
  "totalPrice": 88.55,
  "currency": "USD",
  "estimatedDeliveryTime": "2026-02-11T14:30:00Z"
}
```

**Swagger UI**: `https://api.logistics-platform.com/pricing-service/swagger-ui.html`

---

### 6. Geo Service
**Base Path**: `/geo-service/api/geo`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/geocode` | POST | Convert address to coordinates |
| `/reverse-geocode` | POST | Convert coordinates to address |
| `/distance` | POST | Calculate distance between points |
| `/route` | POST | Get optimal route |
| `/zones` | GET | Get delivery zones |

**Swagger UI**: `https://api.logistics-platform.com/geo-service/swagger-ui.html`

---

## B2B APIs

### 7. Shipment Service
**Base Path**: `/shipment-service/api/shipments`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | List shipments |
| `/` | POST | Create bulk shipment |
| `/{shipmentId}` | GET | Get shipment details |
| `/{shipmentId}/track` | GET | Track shipment |
| `/{shipmentId}/documents` | GET | Get shipping documents |
| `/bulk-upload` | POST | Upload CSV for bulk shipments |

**Swagger UI**: `https://api.logistics-platform.com/shipment-service/swagger-ui.html`

---

### 8. Warehouse Service
**Base Path**: `/warehouse-service/api`

#### Warehouses
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/warehouses` | GET | List warehouses |
| `/warehouses/{warehouseId}/inventory` | GET | Get warehouse inventory |
| `/warehouses/{warehouseId}/capacity` | GET | Get capacity info |

#### Inventory
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/inventory` | GET | List inventory items |
| `/inventory/{itemId}` | GET | Get item details |
| `/inventory/transfer` | POST | Transfer between warehouses |

**Swagger UI**: `https://api.logistics-platform.com/warehouse-service/swagger-ui.html`

---

### 9. Compliance Service
**Base Path**: `/compliance-service/api/compliance`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/documents` | GET | List compliance documents |
| `/documents` | POST | Upload compliance document |
| `/verify` | POST | Verify shipment compliance |
| `/regulations` | GET | Get applicable regulations |
| `/pod` | POST | Submit proof of delivery |

**Swagger UI**: `https://api.logistics-platform.com/compliance-service/swagger-ui.html`

---

### 10. Intelligent Routing Service
**Base Path**: `/intelligent-routing-service/api/routes`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/optimize` | POST | Optimize multi-stop route (VRPTW, Heterogeneous Fleet) |
| `/{routeId}` | GET | Get route details |
| `/{routeId}/eta` | GET | Get estimated arrival times |
| `/green/emissions` | POST | Calculate CO2 emissions for a route |

**Advanced Features**:
- **Time Windows**: Support for `pickupWindow` and `deliveryWindow`.
- **Fleet Diversity**: Optimized for Bikes, Vans, Trucks, and EVs.
- **Sustainability**: Integrated CO2 tracking per vehicle type.

**Swagger UI**: `https://api.logistics-platform.com/intelligent-routing-service/swagger-ui.html`

---

## B2C APIs

### 11. Parcel Service
**Base Path**: `/parcel-service/api/parcels`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | POST | Create parcel shipment |
| `/{trackingNumber}` | GET | Track parcel |
| `/{parcelId}/label` | GET | Download shipping label |
| `/{parcelId}/schedule-pickup` | POST | Schedule pickup |

**Swagger UI**: `https://api.logistics-platform.com/parcel-service/swagger-ui.html`

---

## Shared Services APIs

### 12. Notification Service
**Base Path**: `/notification-service/api/notifications`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/send` | POST | Send notification (email/SMS/push) |
| `/` | GET | Get user notifications |
| `/{notificationId}` | GET | Get notification details |
| `/{notificationId}/read` | POST | Mark as read |
| `/preferences` | GET | Get notification preferences |
| `/preferences` | PUT | Update preferences |

**Example - Send Notification**:
```json
POST /notification-service/api/notifications/send
{
  "userId": "USER-123",
  "channel": "EMAIL",
  "subject": "Order Delivered",
  "message": "Your order #ORD-456 has been delivered.",
  "metadata": {
    "orderId": "ORD-456",
    "trackingNumber": "TRK-789"
  }
}
```

**Swagger UI**: `https://api.logistics-platform.com/notification-service/swagger-ui.html`

---

### 13. Analytics Service
**Base Path**: `/analytics-service/api/analytics`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/emissions/calculate` | POST | Calculate CO2 emissions |
| `/emissions/{entityType}/{entityId}` | GET | Get emission data |
| `/emissions/total/{entityType}` | GET | Get total emissions by type |
| `/dashboard/kpis` | GET | Get KPI metrics |
| `/reports/generate` | POST | Generate custom report |

**Example - Calculate Emissions**:
```json
POST /analytics-service/api/analytics/emissions/calculate
{
  "entityId": "ORD-12345",
  "entityType": "ORDER",
  "distance": 150.5,
  "vehicleType": "TRUCK"
}
```

**Response**:
```json
{
  "entityId": "ORD-12345",
  "entityType": "ORDER",
  "distance": 150.5,
  "vehicleType": "TRUCK",
  "emissionFactor": 0.8,
  "totalEmission": 120.4,
  "calculatedAt": "2026-02-10T00:50:00Z"
}
```

**Swagger UI**: `https://api.logistics-platform.com/analytics-service/swagger-ui.html`

---

### 14. Audit Log Service
**Base Path**: `/audit-log-service/api/audit`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/logs` | GET | Query audit logs |
| `/logs/{entityType}/{entityId}` | GET | Get logs for entity |
| `/logs/user/{userId}` | GET | Get user activity logs |
| `/logs/export` | POST | Export logs (CSV/JSON) |

**Swagger UI**: `https://api.logistics-platform.com/audit-log-service/swagger-ui.html`

---

### 15. SLA Service
**Base Path**: `/sla-service/api/sla`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/definitions` | GET | List SLA definitions |
| `/definitions` | POST | Create SLA definition |
| `/instances` | GET | Get active SLA instances |
| `/breaches` | GET | Get SLA breaches |
| `/breaches/{breachId}` | GET | Get breach details |

**Swagger UI**: `https://api.logistics-platform.com/sla-service/swagger-ui.html`

---

### 16. Billing Service
**Base Path**: `/billing-service/api/billing`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/invoices` | GET | List invoices |
| `/invoices/{invoiceId}` | GET | Get invoice details |
| `/invoices/{invoiceId}/pdf` | GET | Download invoice PDF |
| `/ledger` | GET | Get account ledger |
| `/payments` | POST | Record payment |

**Swagger UI**: `https://api.logistics-platform.com/billing-service/swagger-ui.html`

---

## Error Handling

### Standard Error Response
```json
{
  "timestamp": "2026-02-10T00:50:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid order data: pickup address is required",
  "path": "/order-service/api/orders",
  "traceId": "abc123def456"
}
```

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Successful deletion |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource conflict (e.g., duplicate) |
| 422 | Unprocessable Entity | Validation failed |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server error |
| 503 | Service Unavailable | Service temporarily unavailable |

---

## Rate Limiting

### Limits
- **Default**: 1000 requests per hour per API key
- **Burst**: 100 requests per minute
- **Premium**: 10,000 requests per hour

### Headers
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 847
X-RateLimit-Reset: 1707523800
```

### Rate Limit Exceeded Response
```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 45 seconds.",
  "retryAfter": 45
}
```

---

## Code Examples

### cURL
```bash
# Login
curl -X POST https://api.logistics-platform.com/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'

# Create Order
curl -X POST https://api.logistics-platform.com/order-service/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-123",
    "pickupAddress": {...},
    "deliveryAddress": {...}
  }'
```

### JavaScript (Fetch)
```javascript
// Login
const loginResponse = await fetch('https://api.logistics-platform.com/auth-service/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'user@example.com',
    password: 'password123'
  })
});
const { accessToken } = await loginResponse.json();

// Get Orders
const ordersResponse = await fetch('https://api.logistics-platform.com/order-service/api/orders', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});
const orders = await ordersResponse.json();
```

### Python (Requests)
```python
import requests

# Login
login_response = requests.post(
    'https://api.logistics-platform.com/auth-service/api/auth/login',
    json={'username': 'user@example.com', 'password': 'password123'}
)
access_token = login_response.json()['accessToken']

# Create Order
headers = {'Authorization': f'Bearer {access_token}'}
order_data = {
    'customerId': 'CUST-123',
    'pickupAddress': {...},
    'deliveryAddress': {...}
}
order_response = requests.post(
    'https://api.logistics-platform.com/order-service/api/orders',
    headers=headers,
    json=order_data
)
order = order_response.json()
```

### Java (Spring RestTemplate)
```java
// Login
RestTemplate restTemplate = new RestTemplate();
LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
LoginResponse loginResponse = restTemplate.postForObject(
    "https://api.logistics-platform.com/auth-service/api/auth/login",
    loginRequest,
    LoginResponse.class
);

// Create Order
HttpHeaders headers = new HttpHeaders();
headers.setBearerAuth(loginResponse.getAccessToken());
HttpEntity<CreateOrderRequest> request = new HttpEntity<>(orderRequest, headers);
OrderDTO order = restTemplate.postForObject(
    "https://api.logistics-platform.com/order-service/api/orders",
    request,
    OrderDTO.class
);
```

---

## Additional Resources

- **Postman Collection**: [Download](https://api.logistics-platform.com/postman/collection.json)
- **OpenAPI Spec**: [Download](https://api.logistics-platform.com/v3/api-docs)
- **Support**: support@logistics-platform.com
- **Developer Portal**: https://developers.logistics-platform.com

---

## Versioning

APIs use URL versioning:
- **Current**: `/api/v1/...`
- **Beta**: `/api/v2/...` (preview features)

Version deprecation notices are sent 6 months in advance via email and API headers:
```http
X-API-Deprecation-Date: 2026-08-10
X-API-Deprecation-Info: https://developers.logistics-platform.com/deprecations/v1
```

---

**Last Updated**: 2026-02-13  
**API Version**: 1.1.0 (Intelligent Routing Update)
