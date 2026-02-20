# API Documentation - Logistics Platform (Logistic Platform)

**Base URL**: `http://localhost:8080`

All services are now served from this single base URL.

---

## 📋 Service Endpoints

### 🔐 Authentication
*   `POST /api/auth/login`
*   `POST /api/auth/register`

### 🛒 Order Management
*   `POST /api/orders`
*   `GET /api/orders/{id}`

### 🚚 Fleet & Dispatch
*   `POST /api/drivers`
*   `GET /api/dispatch/status/{orderId}`

### 🏢 B2B & Tenancy
*   `POST /api/tenant/register`
*   `POST /api/b2b/shipments`

### 📦 Warehouse & Inventory
*   `GET /api/inventory/{sku}`
*   `POST /api/warehouse/stock`

---

## 📚 Interactive Documentation

Since the application is a logistic_platform, all Swagger/OpenAPI documentation is aggregated at a single URL:

**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

This UI contains definitions for ALL modules (Auth, Order, Fleet, etc.).
