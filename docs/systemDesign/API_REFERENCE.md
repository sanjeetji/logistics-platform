# Logistics OS: API Reference Guide

This guide documents the REST API endpoints for the Unified Logistics Operating System.

## 🔐 Identity & Access Management (`identity-module`)

### Authentication
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user and receive JWT. |
| `POST` | `/api/auth/register` | Register a new user. |
| `POST` | `/api/auth/refresh` | Obtain a new access token using a refresh token. |
| `POST` | `/api/auth/logout` | Invalidate current session. |

### Tenant Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tenant/register` | Onboard a new business tenant. |
| `GET` | `/api/tenant/config` | Retrieve tenant-specific configurations. |
| `PATCH` | `/api/tenant/flags` | Update feature flags for a tenant. |

---

## 🛒 Fulfillment & Delivery (`fulfillment-module`)

### Dispatch Operations
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/dispatch/auto` | Trigger auto-dispatch for an order. |
| `GET` | `/api/dispatch/status/{orderId}` | Check current assignment status. |
| `POST` | `/api/dispatch/re-assign` | Manually trigger driver re-assignment. |

### Exceptions
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/exceptions/report` | Report a delivery exception (e.g., Damaged). |
| `GET` | `/api/exceptions/active` | List all active operational exceptions. |

---

## 🚚 Fleet & Driver Management (`fleet-module`)

### Driver Lifecycle
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/drivers` | Create a new driver profile. |
| `GET` | `/api/v1/drivers/available` | List drivers ready for assignment. |
| `PATCH` | `/api/v1/drivers/{id}/status` | Update driver status (On Duty, Off Duty, Busy). |

---

## 🏢 B2B Inventory & Warehouse (`b2b-inventory-module`)

### Warehouse Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/warehouses/stock-in` | Record incoming stock. |
| `GET` | `/api/v1/warehouses/{id}/inventory` | Get current stock levels. |
| `POST` | `/api/v1/warehouses/reserve` | Reserve stock for a B2B order. |

---

## 💰 Finance & Pricing (`finance-module`)

### Pricing & Estimates
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/pricing/estimate` | Get price estimate based on route and load. |
| `GET` | `/api/v1/pricing/surge` | Check current surge factors in a zone. |

### Wallets & Billing
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/wallets/deposit` | Add funds to a user/driver wallet. |
| `GET` | `/api/v1/billing/invoices/{orderId}` | Retrieve invoice for an order. |
