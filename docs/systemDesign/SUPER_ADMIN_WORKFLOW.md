# Platform Owner (Super Admin) Workflow

The **Super Admin** is the highest authority in the Logistics Platform, responsible for the global orchestration of tenants, system health, and fiscal oversight (subscription tiers).

---

## 1. Registration & Bootstrapping

### A. The "First Admin" Protocol
In the current development phase, a Super Admin is registered via the standard `/api/auth/register` endpoint by specifying the `SUPER_ADMIN` user type.

```json
{
  "firstName": "Platform",
  "lastName": "Owner",
  "email": "root@logistics-platform.io",
  "password": "secure-password",
  "userType": "SUPER_ADMIN"
}
```

> [!WARNING]
> In production, this endpoint is restricted via IP whitelisting or requires a "System Bootstrap Key" passed in headers to prevent unauthorized creation of platform owners.

### B. Identity Representation
*   **Security Context**: Maps `UserType.SUPER_ADMIN` to `ROLE_SUPER_ADMIN`.
*   **Tenant Isolation**: Super Admins are typically mapped to a virtual `tenantId = SYSTEM` or have a `null` organization context, allowing them to bypass the `TenantContext` filters used by Hibernate.

---

## 2. Global Tenant Management

The Super Admin controls the lifecycle of every logistics company (Tenant) on the platform.

### A. Onboarding Flow
1.  **Creation**: Super Admin calls `POST /api/v1/tenants` to create a new entry.
2.  **Configuration**: Defines the `domain` (e.g., `porter.logistics.com`) and `industryType`.
3.  **Provisioning**: The system automatically initializes a schema-isolated context for the new tenant.

### B. Subscription Control
Super Admins are the only users authorized to modify a tenant's `SubscriptionTier`.

| Action | Impact |
| :--- | :--- |
| **Tier Upgrade** | Moving a tenant from `FREE` to `GOLD` updates their `monthlyOrderLimit` from 0 to unlimited. |
| **Suspension** | Setting `active = false` on a tenant record globally blocks all API access for that tenant's users. |

---

## 3. Platform Monitoring & Control Tower

### A. Cross-Tenant Oversight
The **Control Tower Service** provides the Super Admin with a "God's Eye View":
*   **Global Heatmap**: Aggregates `HeatmapData` from all active tenants to identify regional demand spikes.
*   **System Exceptions**: Monitor the `ExceptionRepository` for cross-cutting infrastructure failures (e.g., Kafka lag affecting all tenants).

### B. Manual Interventions
If a tenant's dispatch engine is stuck, a Super Admin can intervene:
*   **Endpoint**: `PUT /api/control-tower/system/retry-saga/{orderId}`
*   **Action**: Manually pushes a stalled SAGA step forward or triggers a global compensation.

---

## 4. Technical Implementation Detail

### Role Enforcement Pattern
Security is enforced at the method level using Spring Security's `@PreAuthorize`.

```java
// Example in TenantService or AdminController
@PreAuthorize("hasRole('SUPER_ADMIN')")
public Tenant updateSubscription(Long tenantId, SubscriptionTier newTier) {
    // Logic to update and broadcast tier change event
}
```

### Eventual Consistency & Global Cache
When a Super Admin changes a global config or tenant status, an `OrganizationConfigChangedEvent` is published to Redis/Kafka, ensuring all instances of `identity-module` and `fulfillment-module` invalidate their local `TenantContext` caches immediately.
