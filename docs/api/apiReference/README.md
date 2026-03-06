# 📚 Logistics Platform — API Reference Index

All API documentation is organized by module. Each file covers all endpoints for that module with request/response examples.

## Base URL
`http://localhost:8080`

---

## 📂 API Reference Files

| Module | File | Base Paths |
|---|---|---|
| **Identity** | [identity-api.md](./identity-api.md) | `/api/v1/auth`, `/api/v1/users`, `/api/v1/tenants`, `/api/onboarding` |
| **Fulfillment** | [fulfillment-api.md](./fulfillment-api.md) | `/api/v1/orders`, `/api/v1/dispatch`, `/api/v1/routes`, `/api/v1/geo`, `/api/v1/returns` |
| **Fleet** | [fleet-api.md](./fleet-api.md) | `/api/v1/drivers`, `/api/v1/vehicles`, `/api/v1/shifts`, `/api/v1/teams`, `/api/v1/ratings` |
| **Finance** | [finance-api.md](./finance-api.md) | `/api/v1/payments`, `/api/v1/wallets`, `/api/v1/pricing`, `/api/v1/invoices` |
| **B2B & Inventory** | [b2b-api.md](./b2b-api.md) | `/api/v1/b2b/orders`, `/api/v1/shipments`, `/api/v1/warehouses` |
| **Platform Support** | [platform-support-api.md](./platform-support-api.md) | `/api/v1/audit`, `/actuator` |

---

## 🔑 Authentication

All endpoints (except `/api/v1/auth/register` and `/api/v1/auth/login`) require a JWT token:

```
Authorization: Bearer <your-jwt-token>
```

Get your token from: `POST /api/v1/auth/login`

---

## 📊 Response Format

All API responses follow this standard wrapper:

```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... },
  "timestamp": "2024-01-01T12:00:00",
  "errorCode": null
}
```

### Error Response
```json
{
  "success": false,
  "message": "Operation failed",
  "data": {
    "errorCode": "VALIDATION_ERROR",
    "message": "Field validation failed",
    "details": ["email must not be blank"]
  }
}
```

---

## 🚀 Quick Start

1. Register: `POST /api/v1/auth/register`
2. Login: `POST /api/v1/auth/login` → get JWT token
3. Use token in `Authorization: Bearer <token>` header for all subsequent requests
