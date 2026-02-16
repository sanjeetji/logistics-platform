# API Documentation - Logistics Platform

**Version**: 1.0.0  
**Last Updated**: February 16, 2026  
**Base URL**: `http://localhost:8080` (API Gateway)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [B2C Customer Journey](#b2c-customer-journey)
- [B2B Enterprise Journey](#b2b-enterprise-journey)
- [Service Endpoints](#service-endpoints)
- [Postman Collection](#postman-collection)
- [Swagger Documentation](#swagger-documentation)

---

## 🎯 Overview

This document provides complete API flows for both B2C (customer) and B2B (enterprise) clients, covering the entire logistics journey from order creation to delivery completion.

### API Gateway

All requests should go through the API Gateway:
- **URL**: `http://localhost:8080`
- **Rate Limiting**: 1000 requests/minute per user
- **Authentication**: JWT Bearer Token

### Service Ports (Direct Access)

| Service | Port | Swagger UI |
|---------|------|------------|
| API Gateway | 8080 | http://localhost:8080/swagger-ui.html |
| Auth Service | 8081 | http://localhost:8081/swagger-ui.html |
| Order Service | 8085 | http://localhost:8085/swagger-ui.html |
| Route Optimization | 8110 | http://localhost:8110/swagger-ui.html |
| Tracking Service | 8095 | http://localhost:8095/swagger-ui.html |
| Pricing Service | 8087 | http://localhost:8087/swagger-ui.html |

---

## 🔐 Authentication

### 1. Register User

**Endpoint**: `POST /api/auth/register`

**Request**:
```json
{
  "email": "customer@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "userType": "CUSTOMER"
}
```

**Response**:
```json
{
  "userId": "usr_123456",
  "email": "customer@example.com",
  "message": "Registration successful. Please verify your email."
}
```

### 2. Login

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "email": "customer@example.com",
  "password": "SecurePass123!"
}
```

**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": "usr_123456",
    "email": "customer@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["CUSTOMER"]
  }
}
```

### 3. Refresh Token

**Endpoint**: `POST /api/auth/refresh`

**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 🛒 B2C Customer Journey

### Complete Flow: Order Creation → Delivery → Payment

#### Step 1: Get Price Estimate

**Endpoint**: `POST /api/pricing/estimate`

**Headers**:
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Request**:
```json
{
  "pickupLocation": {
    "latitude": 37.7749,
    "longitude": -122.4194,
    "address": "123 Market St, San Francisco, CA 94103"
  },
  "dropoffLocation": {
    "latitude": 37.8044,
    "longitude": -122.2712,
    "address": "456 Broadway, Oakland, CA 94607"
  },
  "packageDetails": {
    "weight": 5.5,
    "dimensions": {
      "length": 30,
      "width": 20,
      "height": 15
    },
    "category": "ELECTRONICS"
  },
  "serviceType": "SAME_DAY"
}
```

**Response**:
```json
{
  "estimateId": "est_789012",
  "basePrice": 25.00,
  "surcharges": {
    "peakHour": 5.00,
    "fragile": 3.00
  },
  "totalPrice": 33.00,
  "currency": "USD",
  "estimatedDuration": "2-3 hours",
  "distance": 12.5,
  "validUntil": "2026-02-16T18:50:00Z"
}
```

#### Step 2: Create Order

**Endpoint**: `POST /api/orders`

**Request**:
```json
{
  "estimateId": "est_789012",
  "pickupLocation": {
    "latitude": 37.7749,
    "longitude": -122.4194,
    "address": "123 Market St, San Francisco, CA 94103",
    "contactName": "John Doe",
    "contactPhone": "+1234567890",
    "instructions": "Ring doorbell"
  },
  "dropoffLocation": {
    "latitude": 37.8044,
    "longitude": -122.2712,
    "address": "456 Broadway, Oakland, CA 94607",
    "contactName": "Jane Smith",
    "contactPhone": "+0987654321",
    "instructions": "Leave at reception"
  },
  "packageDetails": {
    "weight": 5.5,
    "dimensions": {
      "length": 30,
      "width": 20,
      "height": 15
    },
    "category": "ELECTRONICS",
    "description": "Laptop",
    "value": 1200.00
  },
  "serviceType": "SAME_DAY",
  "paymentMethod": "CARD",
  "scheduledPickupTime": "2026-02-16T14:00:00Z"
}
```

**Response**:
```json
{
  "orderId": "ORD-2026021600123",
  "status": "PENDING",
  "trackingNumber": "TRK123456789",
  "estimatedPickupTime": "2026-02-16T14:00:00Z",
  "estimatedDeliveryTime": "2026-02-16T16:30:00Z",
  "price": {
    "subtotal": 28.00,
    "tax": 2.52,
    "total": 30.52,
    "currency": "USD"
  },
  "paymentStatus": "PENDING",
  "createdAt": "2026-02-16T13:45:00Z"
}
```

#### Step 3: Track Order

**Endpoint**: `GET /api/tracking/{trackingNumber}`

**Response**:
```json
{
  "trackingNumber": "TRK123456789",
  "orderId": "ORD-2026021600123",
  "status": "IN_TRANSIT",
  "currentLocation": {
    "latitude": 37.7897,
    "longitude": -122.3972,
    "address": "Downtown San Francisco",
    "timestamp": "2026-02-16T14:45:00Z"
  },
  "driver": {
    "name": "Mike Johnson",
    "phone": "+1555123456",
    "vehicleNumber": "CA-1234",
    "rating": 4.8
  },
  "timeline": [
    {
      "status": "CREATED",
      "timestamp": "2026-02-16T13:45:00Z",
      "location": "San Francisco, CA"
    },
    {
      "status": "ASSIGNED",
      "timestamp": "2026-02-16T13:50:00Z",
      "location": "San Francisco, CA"
    },
    {
      "status": "PICKED_UP",
      "timestamp": "2026-02-16T14:15:00Z",
      "location": "123 Market St"
    },
    {
      "status": "IN_TRANSIT",
      "timestamp": "2026-02-16T14:30:00Z",
      "location": "Downtown SF"
    }
  ],
  "estimatedDeliveryTime": "2026-02-16T16:30:00Z",
  "liveTracking": {
    "enabled": true,
    "websocketUrl": "ws://localhost:8095/tracking/live/TRK123456789"
  }
}
```

#### Step 4: Real-Time Location Updates (WebSocket)

**WebSocket Endpoint**: `ws://localhost:8095/tracking/live/{trackingNumber}`

**Message Format**:
```json
{
  "trackingNumber": "TRK123456789",
  "latitude": 37.7897,
  "longitude": -122.3972,
  "speed": 35.5,
  "heading": 270,
  "timestamp": "2026-02-16T14:45:30Z",
  "estimatedArrival": "2026-02-16T16:28:00Z"
}
```

#### Step 5: Confirm Delivery

**Endpoint**: `POST /api/orders/{orderId}/confirm-delivery`

**Request**:
```json
{
  "deliveryCode": "1234",
  "signature": "base64_encoded_signature",
  "photo": "base64_encoded_photo",
  "notes": "Package delivered successfully",
  "timestamp": "2026-02-16T16:25:00Z"
}
```

**Response**:
```json
{
  "orderId": "ORD-2026021600123",
  "status": "DELIVERED",
  "deliveredAt": "2026-02-16T16:25:00Z",
  "proofOfDelivery": {
    "signature": "https://cdn.logistics.com/pod/sig_123.jpg",
    "photo": "https://cdn.logistics.com/pod/photo_123.jpg"
  },
  "message": "Delivery confirmed successfully"
}
```

#### Step 6: Rate & Review

**Endpoint**: `POST /api/ratings`

**Request**:
```json
{
  "orderId": "ORD-2026021600123",
  "driverId": "drv_456789",
  "rating": 5,
  "review": "Excellent service! Driver was very professional.",
  "categories": {
    "timeliness": 5,
    "communication": 5,
    "packageHandling": 5
  }
}
```

**Response**:
```json
{
  "ratingId": "rat_111222",
  "orderId": "ORD-2026021600123",
  "rating": 5,
  "status": "SUBMITTED",
  "message": "Thank you for your feedback!"
}
```

---

## 🏢 B2B Enterprise Journey

### Complete Flow: Bulk Shipment → Warehouse → Tracking → Analytics

#### Step 1: Enterprise Registration

**Endpoint**: `POST /api/tenant/register`

**Request**:
```json
{
  "companyName": "Acme Corporation",
  "businessType": "E_COMMERCE",
  "contactPerson": {
    "firstName": "Sarah",
    "lastName": "Williams",
    "email": "sarah@acme.com",
    "phone": "+1234567890",
    "designation": "Logistics Manager"
  },
  "businessAddress": {
    "street": "789 Enterprise Blvd",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94105",
    "country": "USA"
  },
  "taxId": "12-3456789",
  "estimatedMonthlyVolume": 5000,
  "serviceLevel": "ENTERPRISE"
}
```

**Response**:
```json
{
  "tenantId": "tnt_enterprise_001",
  "companyName": "Acme Corporation",
  "apiKey": "ak_live_1234567890abcdef",
  "apiSecret": "as_live_abcdef1234567890",
  "status": "ACTIVE",
  "billingPlan": "ENTERPRISE",
  "features": [
    "BULK_UPLOAD",
    "API_ACCESS",
    "DEDICATED_SUPPORT",
    "ANALYTICS_DASHBOARD",
    "CUSTOM_BRANDING"
  ],
  "createdAt": "2026-02-16T10:00:00Z"
}
```

#### Step 2: Create Bulk Shipment

**Endpoint**: `POST /api/b2b/shipments/bulk`

**Headers**:
```
Authorization: Bearer {accessToken}
X-API-Key: ak_live_1234567890abcdef
Content-Type: application/json
```

**Request**:
```json
{
  "shipmentBatch": {
    "batchId": "BATCH-20260216-001",
    "warehouseId": "wh_sf_001",
    "shipments": [
      {
        "referenceId": "ORDER-001",
        "recipient": {
          "name": "Customer A",
          "phone": "+1111111111",
          "address": "100 Main St, Oakland, CA 94601"
        },
        "package": {
          "weight": 2.5,
          "dimensions": {"length": 20, "width": 15, "height": 10},
          "value": 150.00
        },
        "serviceType": "STANDARD",
        "priority": "NORMAL"
      },
      {
        "referenceId": "ORDER-002",
        "recipient": {
          "name": "Customer B",
          "phone": "+2222222222",
          "address": "200 Park Ave, Berkeley, CA 94704"
        },
        "package": {
          "weight": 3.0,
          "dimensions": {"length": 25, "width": 18, "height": 12},
          "value": 200.00
        },
        "serviceType": "EXPRESS",
        "priority": "HIGH"
      }
    ]
  },
  "routeOptimization": {
    "enabled": true,
    "objectives": ["MINIMIZE_DISTANCE", "MINIMIZE_TIME"],
    "constraints": {
      "maxStopsPerRoute": 25,
      "vehicleCapacity": 500
    }
  }
}
```

**Response**:
```json
{
  "batchId": "BATCH-20260216-001",
  "status": "PROCESSING",
  "totalShipments": 2,
  "shipments": [
    {
      "referenceId": "ORDER-001",
      "shipmentId": "SHP-001-123",
      "trackingNumber": "TRK-B2B-001",
      "status": "CREATED"
    },
    {
      "referenceId": "ORDER-002",
      "shipmentId": "SHP-002-124",
      "trackingNumber": "TRK-B2B-002",
      "status": "CREATED"
    }
  ],
  "routeOptimization": {
    "optimizationId": "opt_789",
    "status": "IN_PROGRESS",
    "estimatedCompletion": "2026-02-16T11:05:00Z"
  },
  "estimatedCost": {
    "subtotal": 85.00,
    "discount": 8.50,
    "total": 76.50,
    "currency": "USD"
  }
}
```

#### Step 3: Get Optimized Routes

**Endpoint**: `GET /api/route-optimization/{optimizationId}`

**Response**:
```json
{
  "optimizationId": "opt_789",
  "status": "COMPLETED",
  "routes": [
    {
      "routeId": "route_001",
      "driverId": "drv_123",
      "vehicleId": "veh_456",
      "stops": [
        {
          "sequence": 1,
          "shipmentId": "SHP-002-124",
          "address": "200 Park Ave, Berkeley, CA 94704",
          "estimatedArrival": "2026-02-16T14:30:00Z",
          "serviceTime": 5
        },
        {
          "sequence": 2,
          "shipmentId": "SHP-001-123",
          "address": "100 Main St, Oakland, CA 94601",
          "estimatedArrival": "2026-02-16T15:00:00Z",
          "serviceTime": 5
        }
      ],
      "metrics": {
        "totalDistance": 25.5,
        "totalDuration": 90,
        "totalStops": 2,
        "utilizationRate": 0.45
      }
    }
  ],
  "optimizationMetrics": {
    "totalDistance": 25.5,
    "totalDuration": 90,
    "costSavings": 15.00,
    "co2Reduction": 2.5
  }
}
```

#### Step 4: Track Bulk Shipments

**Endpoint**: `GET /api/b2b/shipments/batch/{batchId}/tracking`

**Response**:
```json
{
  "batchId": "BATCH-20260216-001",
  "totalShipments": 2,
  "summary": {
    "pending": 0,
    "in_transit": 2,
    "delivered": 0,
    "failed": 0
  },
  "shipments": [
    {
      "shipmentId": "SHP-001-123",
      "trackingNumber": "TRK-B2B-001",
      "status": "IN_TRANSIT",
      "currentLocation": "Downtown Oakland",
      "estimatedDelivery": "2026-02-16T15:00:00Z"
    },
    {
      "shipmentId": "SHP-002-124",
      "trackingNumber": "TRK-B2B-002",
      "status": "IN_TRANSIT",
      "currentLocation": "Berkeley",
      "estimatedDelivery": "2026-02-16T14:30:00Z"
    }
  ]
}
```

#### Step 5: Get Analytics Dashboard

**Endpoint**: `GET /api/analytics/dashboard`

**Query Parameters**:
- `tenantId`: tnt_enterprise_001
- `startDate`: 2026-02-01
- `endDate`: 2026-02-16
- `metrics`: deliveries,revenue,performance

**Response**:
```json
{
  "tenantId": "tnt_enterprise_001",
  "period": {
    "start": "2026-02-01T00:00:00Z",
    "end": "2026-02-16T23:59:59Z"
  },
  "metrics": {
    "deliveries": {
      "total": 5234,
      "successful": 5102,
      "failed": 132,
      "successRate": 97.48
    },
    "revenue": {
      "total": 125680.50,
      "currency": "USD",
      "averageOrderValue": 24.01,
      "growth": 12.5
    },
    "performance": {
      "averageDeliveryTime": 145,
      "onTimeDeliveryRate": 94.2,
      "customerSatisfaction": 4.6
    },
    "routes": {
      "totalRoutes": 234,
      "averageStopsPerRoute": 22.4,
      "distanceSaved": 1250.5,
      "costSavings": 3750.00
    }
  },
  "trends": {
    "daily": [
      {
        "date": "2026-02-16",
        "deliveries": 342,
        "revenue": 8210.50
      }
    ]
  }
}
```

#### Step 6: Generate Invoice

**Endpoint**: `GET /api/billing/invoices/{invoiceId}`

**Response**:
```json
{
  "invoiceId": "INV-202602-001",
  "tenantId": "tnt_enterprise_001",
  "billingPeriod": {
    "start": "2026-02-01",
    "end": "2026-02-28"
  },
  "lineItems": [
    {
      "description": "Standard Deliveries (5102 shipments)",
      "quantity": 5102,
      "unitPrice": 15.00,
      "amount": 76530.00
    },
    {
      "description": "Express Deliveries (132 shipments)",
      "quantity": 132,
      "unitPrice": 25.00,
      "amount": 3300.00
    },
    {
      "description": "Route Optimization Service",
      "quantity": 1,
      "unitPrice": 500.00,
      "amount": 500.00
    }
  ],
  "subtotal": 80330.00,
  "discount": 8033.00,
  "tax": 6506.43,
  "total": 78803.43,
  "currency": "USD",
  "status": "PENDING",
  "dueDate": "2026-03-15",
  "downloadUrl": "https://api.logistics.com/invoices/INV-202602-001.pdf"
}
```

---

## 📡 Service Endpoints Reference

### Auth Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/logout` | User logout |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/forgot-password` | Request password reset |
| POST | `/api/auth/reset-password` | Reset password |
| GET | `/api/auth/verify-email` | Verify email address |

### Order Service (Port 8085)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders/{orderId}` | Get order details |
| GET | `/api/orders` | List user orders |
| PUT | `/api/orders/{orderId}` | Update order |
| DELETE | `/api/orders/{orderId}` | Cancel order |
| POST | `/api/orders/{orderId}/confirm-delivery` | Confirm delivery |
| GET | `/api/orders/{orderId}/history` | Get order history |

### Pricing Service (Port 8087)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/pricing/estimate` | Get price estimate |
| POST | `/api/pricing/calculate` | Calculate final price |
| GET | `/api/pricing/zones` | Get pricing zones |
| GET | `/api/pricing/surcharges` | Get active surcharges |

### Tracking Service (Port 8095)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tracking/{trackingNumber}` | Track shipment |
| GET | `/api/tracking/{trackingNumber}/history` | Get tracking history |
| WS | `/tracking/live/{trackingNumber}` | Real-time location updates |
| POST | `/api/tracking/location` | Update driver location |

### Route Optimization Service (Port 8110)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/route-optimization/optimize` | Optimize routes |
| GET | `/api/route-optimization/{optimizationId}` | Get optimization result |
| POST | `/api/route-optimization/what-if` | What-if analysis |
| POST | `/api/route-optimization/simulate` | Route simulation |
| POST | `/api/route-optimization/re-route` | Dynamic re-routing |

### B2B Shipment Service (Port 8118)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/b2b/shipments` | Create shipment |
| POST | `/api/b2b/shipments/bulk` | Create bulk shipments |
| GET | `/api/b2b/shipments/{shipmentId}` | Get shipment details |
| GET | `/api/b2b/shipments/batch/{batchId}` | Get batch details |
| PUT | `/api/b2b/shipments/{shipmentId}/status` | Update shipment status |

### Warehouse Service (Port 8119)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/warehouses` | List warehouses |
| GET | `/api/warehouses/{warehouseId}` | Get warehouse details |
| GET | `/api/warehouses/{warehouseId}/inventory` | Get inventory |
| POST | `/api/warehouses/{warehouseId}/inbound` | Record inbound shipment |
| POST | `/api/warehouses/{warehouseId}/outbound` | Record outbound shipment |

### Payment Service (Port 8096)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/process` | Process payment |
| GET | `/api/payments/{paymentId}` | Get payment details |
| POST | `/api/payments/refund` | Process refund |
| GET | `/api/payments/methods` | Get payment methods |

### Notification Service (Port 8097)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications/send` | Send notification |
| GET | `/api/notifications` | Get user notifications |
| PUT | `/api/notifications/{notificationId}/read` | Mark as read |
| GET | `/api/notifications/preferences` | Get notification preferences |

### Analytics Service (Port 8108)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/dashboard` | Get dashboard metrics |
| GET | `/api/analytics/reports/{reportId}` | Get specific report |
| POST | `/api/analytics/reports/generate` | Generate custom report |
| GET | `/api/analytics/real-time` | Get real-time metrics |

---

## 📮 Postman Collection

### Import Instructions

1. Download the Postman collection: [Logistics-Platform-API.postman_collection.json](./postman/Logistics-Platform-API.postman_collection.json)
2. Open Postman
3. Click "Import" → "Upload Files"
4. Select the downloaded JSON file
5. Collection will be imported with all endpoints

### Environment Variables

Create a new environment with these variables:

```json
{
  "base_url": "http://localhost:8080",
  "auth_token": "",
  "refresh_token": "",
  "tenant_id": "",
  "api_key": ""
}
```

---

## 📊 Swagger Documentation

### Access Swagger UI

Each service exposes Swagger UI at `/swagger-ui.html`:

- **API Gateway**: http://localhost:8080/swagger-ui.html
- **Auth Service**: http://localhost:8081/swagger-ui.html
- **Order Service**: http://localhost:8085/swagger-ui.html
- **Route Optimization**: http://localhost:8110/swagger-ui.html
- **Tracking Service**: http://localhost:8095/swagger-ui.html

### OpenAPI Spec

Download OpenAPI 3.0 specifications:

- **API Gateway**: http://localhost:8080/v3/api-docs
- **Order Service**: http://localhost:8085/v3/api-docs
- **Route Optimization**: http://localhost:8110/v3/api-docs

---

## 🔄 Complete User Flows

### B2C Flow Diagram

```
Customer Registration
    ↓
Login & Authentication
    ↓
Get Price Estimate
    ↓
Create Order
    ↓
Payment Processing
    ↓
Order Assigned to Driver
    ↓
Real-Time Tracking
    ↓
Delivery Confirmation
    ↓
Rate & Review
```

### B2B Flow Diagram

```
Enterprise Registration
    ↓
API Key Generation
    ↓
Bulk Shipment Upload
    ↓
Route Optimization
    ↓
Warehouse Processing
    ↓
Driver Assignment
    ↓
Batch Tracking
    ↓
Delivery Completion
    ↓
Analytics & Reporting
    ↓
Invoice Generation
```

---

## 📞 Support

For API support:
- **Email**: api-support@logistics-platform.com
- **Documentation**: http://docs.logistics-platform.com
- **Status Page**: http://status.logistics-platform.com

---

**Last Updated**: February 16, 2026  
**API Version**: 1.0.0
