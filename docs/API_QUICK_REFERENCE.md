# API Quick Reference Guide

**Quick access to all API endpoints organized by service**

---

## 🔗 Quick Links

- [Full API Documentation](./API_DOCUMENTATION.md)
- [Postman Collection](./postman/Logistics-Platform-API.postman_collection.json)
- [Swagger UI](http://localhost:8080/swagger-ui.html)

---

## 🚀 Getting Started

### 1. Start Services

```bash
./scripts/start-services.sh
```

### 2. Access Swagger UI

- **API Gateway**: http://localhost:8080/swagger-ui.html
- **Order Service**: http://localhost:8085/swagger-ui.html
- **Route Optimization**: http://localhost:8110/swagger-ui.html

### 3. Import Postman Collection

1. Open Postman
2. Import `postman/Logistics-Platform-API.postman_collection.json`
3. Set environment variables:
   - `base_url`: http://localhost:8080
   - `auth_token`: (auto-filled after login)

---

## 📊 Service Endpoints Summary

### Authentication (8081)
- `POST /api/auth/register` - Register user
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token

### Orders (8085)
- `POST /api/orders` - Create order
- `GET /api/orders/{orderId}` - Get order
- `GET /api/orders` - List orders
- `POST /api/orders/{orderId}/confirm-delivery` - Confirm delivery

### Pricing (8087)
- `POST /api/pricing/estimate` - Get price estimate
- `POST /api/pricing/calculate` - Calculate price

### Tracking (8095)
- `GET /api/tracking/{trackingNumber}` - Track shipment
- `WS /tracking/live/{trackingNumber}` - Real-time updates

### Route Optimization (8110)
- `POST /api/route-optimization/optimize` - Optimize routes
- `POST /api/route-optimization/what-if` - What-if analysis
- `POST /api/route-optimization/simulate` - Simulate route

### B2B Shipments (8118)
- `POST /api/b2b/shipments/bulk` - Create bulk shipments
- `GET /api/b2b/shipments/batch/{batchId}` - Get batch details

### Analytics (8108)
- `GET /api/analytics/dashboard` - Get dashboard metrics
- `POST /api/analytics/reports/generate` - Generate report

---

## 🎯 Common Workflows

### B2C Customer Order

```
1. POST /api/auth/login
2. POST /api/pricing/estimate
3. POST /api/orders
4. GET /api/tracking/{trackingNumber}
5. POST /api/orders/{orderId}/confirm-delivery
6. POST /api/ratings
```

### B2B Bulk Shipment

```
1. POST /api/tenant/register
2. POST /api/b2b/shipments/bulk
3. GET /api/route-optimization/{optimizationId}
4. GET /api/b2b/shipments/batch/{batchId}/tracking
5. GET /api/analytics/dashboard
```

---

## 🔐 Authentication

All requests (except register/login) require:

```
Authorization: Bearer {accessToken}
```

Get token from login response and use in subsequent requests.

---

## 📝 Example Requests

### Create Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "pickupLocation": {
      "latitude": 37.7749,
      "longitude": -122.4194,
      "address": "123 Market St, SF"
    },
    "dropoffLocation": {
      "latitude": 37.8044,
      "longitude": -122.2712,
      "address": "456 Broadway, Oakland"
    },
    "packageDetails": {
      "weight": 5.5,
      "category": "ELECTRONICS"
    },
    "serviceType": "SAME_DAY"
  }'
```

### Track Order

```bash
curl http://localhost:8080/api/tracking/TRK123456789 \
  -H "Authorization: Bearer {token}"
```

---

## 📞 Support

- **Documentation**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Status**: `./scripts/check-status.sh`
