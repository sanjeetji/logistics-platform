# 🎛️ Feature Control Panel

> **Module:** Identity Module (`identity-module`)
> **Architecture:** Monolith
> **Audience:** SUPER_ADMIN (Platform Owner)
> **Status:** ✅ Fully Implemented (Feb 2026)

---

## 1. Overview

The **Feature Control Panel** gives the Platform Owner (`SUPER_ADMIN`) complete control over which features each client (tenant/organization) can access. It works like a **master switchboard** — features can be toggled ON/OFF per client at any time independently.

```
Platform (SUPER_ADMIN)
│
├── Tenant: DHL India   →  ROUTE_OPTIMIZATION ✅  DYNAMIC_PRICING ✅  AI_FORECAST ❌
├── Tenant: Porter      →  ROUTE_OPTIMIZATION ✅  DYNAMIC_PRICING ❌  AI_FORECAST ❌
└── Tenant: BlueDart    →  ROUTE_OPTIMIZATION ❌  DYNAMIC_PRICING ✅  AI_FORECAST ✅
```

> **Organization = Tenant = Client.** All three terms mean the same thing. Feature flags are toggled per `organizationId` (also called `tenantId`).

---

## 2. How Feature Resolution Works

```
1. Check tenant-specific override → {"1": true, "5": false} (highest priority)
2. No override exists?            → Fall back to globallyEnabled default
```

---

## 3. Master Feature List (21 Features — Auto-Seeded on Startup)

All features are automatically inserted into the database on first boot (idempotent — safe to restart).

| Category | Feature Key | Default |
|---|---|---|
| FLEET | `REAL_TIME_TRACKING` | **ON** |
| FLEET | `ROUTE_OPTIMIZATION` | OFF |
| FLEET | `MULTI_VEHICLE_SUPPORT` | OFF |
| FLEET | `DRIVER_MANAGEMENT` | **ON** |
| ORDERS | `B2B_ORDERS` | **ON** |
| ORDERS | `B2C_ORDERS` | OFF |
| ORDERS | `BULK_ORDER_UPLOAD` | OFF |
| ORDERS | `CUSTOM_SLA` | OFF |
| WAREHOUSE | `WAREHOUSE_MANAGEMENT` | OFF |
| PRICING | `DYNAMIC_PRICING` | OFF |
| PRICING | `SURGE_PRICING` | OFF |
| ANALYTICS | `ANALYTICS_DASHBOARD` | OFF |
| ANALYTICS | `EXPORT_REPORTS` | OFF |
| NOTIFICATIONS | `NOTIFICATIONS_EMAIL` | **ON** |
| NOTIFICATIONS | `NOTIFICATIONS_SMS` | OFF |
| NOTIFICATIONS | `NOTIFICATIONS_PUSH` | OFF |
| PLATFORM | `API_ACCESS` | OFF |
| PLATFORM | `WEBHOOK_SUPPORT` | OFF |
| PLATFORM | `PAYROLL_INTEGRATION` | OFF |
| AI | `ORCHESTRATION_SAGA` | OFF |
| AI | `AI_DEMAND_FORECAST` | OFF |

---

## 4. Complete API Reference

### Security
| Endpoint pattern | Required role |
|---|---|
| `GET/POST/PUT /api/v1/admin/**` | `SUPER_ADMIN` only |
| `GET /api/v1/features/my-features` | Any authenticated user |
| `GET/POST/PUT /api/v1/tenants/**` | `SUPER_ADMIN` only |

---

### 4.1 Master Feature List (SUPER_ADMIN)

#### Get All Features
```
GET /api/v1/admin/features
Authorization: Bearer <SUPER_ADMIN_JWT>
```
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "id": 1,
      "featureKey": "ROUTE_OPTIMIZATION",
      "featureName": "Route Optimization",
      "description": "AI-based route optimization to reduce delivery time & cost",
      "category": "FLEET",
      "globallyEnabled": false
    }
  ],
  "timestamp": "2026-02-23T12:00:00",
  "errorCode": null
}
```

#### Create a New Feature
```
POST /api/v1/admin/features
Authorization: Bearer <SUPER_ADMIN_JWT>
```
```json
{
  "featureKey": "CUSTOM_BRANDING",
  "featureName": "Custom Branding",
  "description": "Allow white-labeling of the platform UI",
  "category": "PLATFORM",
  "globallyEnabled": false
}
```

#### Update Global Default (ON/OFF for everyone)
```
PUT /api/v1/admin/features/{featureKey}/global
Authorization: Bearer <SUPER_ADMIN_JWT>
Body: { "globallyEnabled": true }
```

---

### 4.2 Per-Tenant Feature Control ⭐ (The Control Panel)

#### Get All Features for a Tenant — with ON/OFF state
```
GET /api/v1/admin/features/tenant/{tenantId}
```
```json
{
  "success": true,
  "data": {
    "tenantId": 1,
    "tenantName": "DHL India",
    "subscriptionTier": "GOLD",
    "features": [
      { "featureKey": "ROUTE_OPTIMIZATION",  "featureName": "Route Optimization",  "category": "FLEET",      "description": "...", "enabled": true  },
      { "featureKey": "DYNAMIC_PRICING",     "featureName": "Dynamic Pricing",     "category": "PRICING",    "description": "...", "enabled": false },
      { "featureKey": "ANALYTICS_DASHBOARD", "featureName": "Analytics Dashboard", "category": "ANALYTICS",  "description": "...", "enabled": true  }
    ]
  }
}
```

#### Enable a Single Feature for a Tenant
```
POST /api/v1/admin/features/{featureKey}/enable/{tenantId}
```
```json
{ "success": true, "message": "Feature ROUTE_OPTIMIZATION enabled for tenant 1" }
```

#### Disable a Single Feature for a Tenant
```
POST /api/v1/admin/features/{featureKey}/disable/{tenantId}
```

#### Bulk Toggle Features ⭐ — Control Panel "Save All" Button
```
PUT /api/v1/admin/features/tenant/{tenantId}/bulk
```
```json
{
  "updates": [
    { "featureKey": "ROUTE_OPTIMIZATION", "enabled": true  },
    { "featureKey": "DYNAMIC_PRICING",    "enabled": false },
    { "featureKey": "AI_DEMAND_FORECAST", "enabled": true  }
  ]
}
```
Response:
```json
{ "success": true, "message": "Feature flags updated for tenant 1" }
```

---

### 4.3 Tenant Self-Service (Any Authenticated User)

#### Get My Enabled Features — Called by Frontend on Login
```
GET /api/v1/features/my-features
Authorization: Bearer <USER_JWT>
X-Tenant-Id: 1          ← required (or ?tenantId=1 as query param)
```
```json
{
  "success": true,
  "data": {
    "tenantId": 1,
    "enabledFeatures": [
      "REAL_TIME_TRACKING",
      "B2B_ORDERS",
      "DRIVER_MANAGEMENT",
      "NOTIFICATIONS_EMAIL"
    ]
  }
}
```
> **Frontend uses this list on login to show/hide menu items, pages, and action buttons. If a feature key is absent, the UI element is hidden.**

---

## 5. Error Responses

### ❌ FEATURE_DISABLED — HTTP 403

Fires when a tenant calls an API for a feature that is **currently OFF** for them.

> **When does a client actually see this error?**
> If a feature is OFF from day 1, the frontend never renders that UI (because it's not in `my-features`), so the client never calls that API. This error only reaches a client in **edge cases**:
> - Feature was ON → SUPER_ADMIN revoked it while client session was active
> - Subscription/plan expired → features auto-disabled mid-session
> - Frontend has stale cached session and still shows a button that's now OFF
> - Developer bypassing the UI and calling the API directly (Postman, curl, integration)

```json
{
  "success": false,
  "message": "Feature 'ROUTE_OPTIMIZATION' is not available for your account.",
  "data": {
    "featureKey": "ROUTE_OPTIMIZATION",
    "tenantId": 1,
    "action": "If you believe this is an error, please contact support."
  },
  "errorCode": "FEATURE_DISABLED",
  "timestamp": "2026-02-23T12:00:00"
}
```

### ❌ ACCESS_DENIED — HTTP 403

Non-SUPER_ADMIN calling an admin-only endpoint:

```json
{
  "success": false,
  "message": "Access denied: you do not have permission to perform this action.",
  "data": null,
  "errorCode": "ACCESS_DENIED",
  "timestamp": "2026-02-23T12:00:00"
}
```

### ❌ FEATURE_NOT_FOUND — HTTP 404

Invalid feature key passed:

```json
{
  "success": false,
  "message": "Feature not found: UNKNOWN_KEY",
  "data": null,
  "errorCode": "FEATURE_NOT_FOUND",
  "timestamp": "2026-02-23T12:00:00"
}
```

### ❌ TENANT_NOT_FOUND — HTTP 404
```json
{
  "success": false,
  "message": "Tenant not found: 999",
  "errorCode": "TENANT_NOT_FOUND"
}
```

### ❌ FEATURE_ALREADY_EXISTS — HTTP 409
```json
{
  "success": false,
  "message": "Feature key already exists: ROUTE_OPTIMIZATION",
  "errorCode": "FEATURE_ALREADY_EXISTS"
}
```

---

## 6. How to Guard Any Service Method with a Feature Check

Use `FeatureGuardService` — inject it once, call one line.

### Step 1 — Inject
```java
@Service
@RequiredArgsConstructor
public class RouteOptimizationService {
    private final FeatureGuardService featureGuard;
}
```

### Step 2 — Add Guard at the Top of the Method
```java
public RouteResult optimize(Long tenantId, OrderRequest order) {

    featureGuard.require("ROUTE_OPTIMIZATION", tenantId); // ← throws HTTP 403 if OFF

    // ... rest of logic runs only if feature is ON
}
```

### Step 3 — Client Gets Structured Error Automatically
```json
{
  "success": false,
  "message": "Feature 'ROUTE_OPTIMIZATION' is not available for your account.",
  "errorCode": "FEATURE_DISABLED"
}
```

### Conditional Behavior (No Exception)
```java
// Use isEnabled() when you want to adapt behavior, not block entirely:
if (featureGuard.isEnabled("SURGE_PRICING", tenantId)) {
    price = price * surgeMultiplier;
}
```

### Feature Key Reference for Guards
| Feature Key | Use In |
|---|---|
| `ROUTE_OPTIMIZATION` | Route planning services |
| `DYNAMIC_PRICING` | Pricing engine |
| `ANALYTICS_DASHBOARD` | Analytics/report services |
| `B2B_ORDERS` | B2B order controller/service |
| `B2C_ORDERS` | B2C order controller/service |
| `DRIVER_MANAGEMENT` | Driver management services |
| `WAREHOUSE_MANAGEMENT` | Warehouse operations |
| `REAL_TIME_TRACKING` | GPS/tracking services |
| `AI_DEMAND_FORECAST` | ML/prediction services |
| `API_ACCESS` | External API key middleware |
| `WEBHOOK_SUPPORT` | Webhook event publisher |

---

## 7. System Flow

```
SUPER_ADMIN Dashboard
│
├── GET /api/v1/admin/features/tenant/1
│       └─ Returns all 21 features with ON/OFF per tenant 1
│
├── [Toggle switches in UI]
│
├── PUT /api/v1/admin/features/tenant/1/bulk
│       └─ Updates tenantOverrides JSON in DB
│       └─ Evicts Redis cache for changed feature+tenant keys
│
Tenant User (on login)
│
├── GET /api/v1/features/my-features  (X-Tenant-Id: 1)
│       └─ Returns: ["ROUTE_OPTIMIZATION", "DRIVER_MANAGEMENT", ...]
│       └─ Frontend renders only enabled menu items/pages
│
Tenant User (using a feature)
│
├── POST /api/orders/route-optimize
│       └─ Service: featureGuard.require("ROUTE_OPTIMIZATION", tenantId)
│
│   Feature ON  → ✅ Proceeds normally
│   Feature OFF → ❌ HTTP 403 FEATURE_DISABLED + structured error body
```

---

## 8. Database Schema

```sql
-- feature_flags table
id               BIGINT       PRIMARY KEY AUTO_INCREMENT
feature_key      VARCHAR(100) UNIQUE NOT NULL        -- "ROUTE_OPTIMIZATION"
feature_name     VARCHAR(200) NOT NULL               -- "Route Optimization"
description      TEXT                               -- human-readable description
category         VARCHAR(50)                        -- "FLEET", "PRICING", "AI", etc.
globally_enabled BOOLEAN      DEFAULT FALSE          -- platform-wide default
tenant_overrides JSON                               -- {"1": true, "5": false, "12": true}
created_at       TIMESTAMP
updated_at       TIMESTAMP
```

---

## 9. Implemented Files

| File | Type | Purpose |
|---|---|---|
| `tenant/model/FeatureFlag.java` | ✏️ Modified | Added `category` field |
| `tenant/repository/FeatureFlagRepository.java` | ✏️ Modified | 4 new query methods |
| `tenant/service/FeatureFlagService.java` | ✏️ Rewritten | 8 methods with `@CacheEvict` |
| `tenant/service/FeatureGuardService.java` | 🆕 New | `require()` / `isEnabled()` guards for services |
| `tenant/controller/FeatureFlagController.java` | 🆕 New | 8 REST endpoints |
| `tenant/dto/FeatureFlagDto.java` | 🆕 New | Master list response DTO |
| `tenant/dto/TenantFeatureStatusDto.java` | 🆕 New | Per-tenant ON/OFF view DTO |
| `tenant/dto/BulkFeatureUpdateRequest.java` | 🆕 New | Bulk toggle request DTO |
| `tenant/dto/FeatureFlagCreateRequest.java` | 🆕 New | Create/update feature DTO |
| `tenant/dto/MyFeaturesResponse.java` | 🆕 New | Tenant self-service response DTO |
| `tenant/config/FeatureDataSeeder.java` | 🆕 New | Seeds 21 features on startup (idempotent) |
| `tenant/exception/FeatureNotEnabledException.java` | 🆕 New | Custom HTTP 403 exception |
| `tenant/exception/FeatureControlPanelExceptionHandler.java` | 🆕 New | Global handler — all 5 error codes |
| `auth/config/SecurityConfig.java` | ✏️ Modified | SUPER_ADMIN security rules added |

---

## 10. Postman Test Checklist

| # | Request | Expected Result |
|---|---|---|
| 1 | `POST /api/v1/auth/login` (SUPER_ADMIN) | JWT token returned |
| 2 | `GET /api/v1/admin/features` | All 21 seeded features returned |
| 3 | `GET /api/v1/admin/features/tenant/1` | All 21 features with ON/OFF for tenant 1 |
| 4 | `POST /api/v1/admin/features/ROUTE_OPTIMIZATION/enable/1` | Feature turned ON |
| 5 | `GET /api/v1/admin/features/tenant/1` | `ROUTE_OPTIMIZATION` shows `enabled: true` |
| 6 | `PUT /api/v1/admin/features/tenant/1/bulk` (multiple toggles) | All applied in one transaction |
| 7 | `GET /api/v1/features/my-features` (tenant user, X-Tenant-Id: 1) | Only enabled feature keys returned |
| 8 | Call guarded service with feature OFF | HTTP 403, `errorCode: "FEATURE_DISABLED"` |
| 9 | Non-SUPER_ADMIN calls `/api/v1/admin/**` | HTTP 403, `errorCode: "ACCESS_DENIED"` |
| 10 | Enable/disable with bad featureKey | HTTP 404, `errorCode: "FEATURE_NOT_FOUND"` |

---

## 11. Future: Subscription Tier Auto-Grant

| Tier | Features Auto-Enabled |
|---|---|
| `FREE` | `REAL_TIME_TRACKING`, `B2B_ORDERS`, `NOTIFICATIONS_EMAIL` |
| `BRONZE` | FREE + `ANALYTICS_DASHBOARD`, `DRIVER_MANAGEMENT` |
| `SILVER` | BRONZE + `ROUTE_OPTIMIZATION`, `BULK_ORDER_UPLOAD`, `API_ACCESS` |
| `GOLD` | SILVER + `DYNAMIC_PRICING`, `SURGE_PRICING`, `AI_DEMAND_FORECAST` |

> SUPER_ADMIN can manually override any tier-based setting at any time.
