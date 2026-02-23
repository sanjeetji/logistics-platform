# Platform Support Module API Reference Guide

All endpoints for Audit Logs, Notifications, Analytics, and platform-level support features.

## Base URL
`http://localhost:8080`

---

## 1. Audit Log Controller (`/api/v1/audit`)
*Complete audit trail for all platform actions.*

### Get Audit Logs for Entity
* **Endpoint**: `GET /api/v1/audit/entity/{entityType}/{entityId}`
* **Example**: `GET /api/v1/audit/entity/ORDER/ORD-001`
* **Response**: List of audit events for that entity.

### Get Audit Logs by User
* **Endpoint**: `GET /api/v1/audit/user/{userId}`
* **Response**: All actions performed by the user.

### Get Audit Logs by Tenant
* **Endpoint**: `GET /api/v1/audit/tenant/{tenantId}`
* **Response**: All audit events within a tenant.

### Get Recent Audit Logs
* **Endpoint**: `GET /api/v1/audit/recent?limit=50`
* **Auth**: `ADMIN` / `SUPER_ADMIN` only.
* **Response**: Most recent audit entries.

---

## 2. System Health (`/actuator`)
*Spring Boot Actuator endpoints for monitoring.*

### Health Check
* **Endpoint**: `GET /actuator/health`
* **Response**: `{"status": "UP"}`

### App Info
* **Endpoint**: `GET /actuator/info`
* **Response**: Build version and metadata.

### Metrics
* **Endpoint**: `GET /actuator/metrics`
* **Response**: JVM, HTTP, and custom metrics.

### Prometheus Metrics
* **Endpoint**: `GET /actuator/prometheus`
* **Response**: Metrics in Prometheus scrape format.

---

## Audit Log Event Structure

Each audit log entry contains:

| Field | Description |
|---|---|
| `userId` | Who performed the action |
| `entityType` | Type of resource (ORDER, USER, DRIVER, etc.) |
| `entityId` | ID of the affected resource |
| `action` | Action performed (CREATE, UPDATE, DELETE, LOGIN, etc.) |
| `oldValue` | JSON of the previous state |
| `newValue` | JSON of the new state |
| `ipAddress` | Caller's IP address |
| `userAgent` | Browser/client identifier |
| `status` | `SUCCESS` or `FAILURE` |
| `timestamp` | When the action occurred |
