# Identity Module API Reference Guide

This document provides a exhaustive reference for all API endpoints in the Identity Module.

## Base URL
`http://localhost:8080`

---

## 1. Authentication Controller (`/api/v1/auth`)
*Handles user registration, login, token management, and profile retrieval.*

### Register User
*   **Endpoint**: `POST /api/v1/auth/register`
*   **Request Body**:
    ```json
    {
      "firstName": "Sanjeet",
      "lastName": "Kumar",
      "email": "sanjeet@example.com",
      "password": "Password123!",
      "userType": "SUPER_ADMIN",
      "organizationId": ""
    }
    ```
*   **Field Comments**:
    *   `userType`: `SUPER_ADMIN`, `ADMIN`, `USER`, `DRIVER`, `DISPATCHER`, `WAREHOUSE_STAFF`, `SUPPORT_AGENT`.
    *   `organizationId`: 
        *   **For SUPER_ADMIN**: Not required. Can be null/empty or omitted. 
        *   **For others**: Required to associate the user with a specific Logistics Company.
*   **Response**: `200 OK` with `ApiResponse<UserDto>`.

### Login
*   **Endpoint**: `POST /api/v1/auth/login`
*   **Request Body**: `{"email": "...", "password": "..."}`
*   **Response**: `200 OK` with JWT Token in `data`.

### Refresh Token
*   **Endpoint**: `POST /api/v1/auth/refresh`
*   **Request Body**: `{"refreshToken": "..."}`
*   **Response**: `200 OK` with new Access and Refresh tokens.

### Logout
*   **Endpoint**: `POST /api/v1/auth/logout`
*   **Header**: `Authorization: Bearer <token>`
*   **Response**: `200 OK` (Token blacklisted).

### Forgot Password
*   **Endpoint**: `POST /api/v1/auth/forgot-password`
*   **Request Body**: `{"email": "..."}`
*   **Response**: `200 OK` (Email sent).

### Reset Password
*   **Endpoint**: `POST /api/v1/auth/reset-password`
*   **Request Body**: `{"token": "...", "newPassword": "..."}`
*   **Response**: `200 OK`.

### Switch Tenant context
*   **Endpoint**: `POST /api/v1/auth/switch-tenant`
*   **Request Body**: `{"targetOrganizationId": "..."}`
*   **Response**: `200 OK` with new JWT Token scoped to the target tenant.

### Change Password
*   **Endpoint**: `POST /api/v1/auth/change-password`
*   **Request Body**: `{"currentPassword": "...", "newPassword": "..."}`
*   **Auth**: Required.
*   **Response**: `200 OK`.

### Get My Profile
*   **Endpoint**: `GET /api/v1/auth/me`
*   **Auth**: Required.
*   **Response**: `200 OK` with current user's profile info.

---

## 2. Onboarding Controller (`/api/onboarding`)
*Manages the self-service flow for new logistics companies joining the platform.*

### Start Onboarding
*   **Endpoint**: `POST /api/onboarding/start`
*   **Request Body**: `{"companyName": "...", "adminEmail": "...", "businessModel": "B2B"}`
*   **Response**: `200 OK` with `onboardingId`.

### Setup Subscription
*   **Endpoint**: `POST /api/onboarding/{onboardingId}/subscription`
*   **Request Body**: `{"tier": "PREMIUM", "paymentMethodId": "..."}`
*   **Response**: `200 OK`.

### Complete Onboarding
*   **Endpoint**: `POST /api/onboarding/{onboardingId}/complete`
*   **Response**: `200 OK`. Finalizes account setup and tenant creation.

### Get Onboarding Status
*   **Endpoint**: `GET /api/onboarding/{onboardingId}`
*   **Response**: `200 OK` with progress status.

---

## 3. Tenant Management Controller (`/api/v1/tenants`)
*Administrative APIs for managing Logistics Companies (Tenants).*

### List All Tenants
*   **Endpoint**: `GET /api/v1/tenants`
*   **Auth**: `SUPER_ADMIN` only.
*   **Response**: `200 OK` with array of `TenantDto`.

### Get Tenant by ID
*   **Endpoint**: `GET /api/v1/tenants/{id}`
*   **Response**: `200 OK` with `TenantDto`.

### Create Tenant
*   **Endpoint**: `POST /api/v1/tenants`
*   **Request Body**: `TenantDto` (name, businessModel, status, etc.)
*   **Response**: `200 OK`.

### Update Tenant
*   **Endpoint**: `PUT /api/v1/tenants/{id}`
*   **Request Body**: Updated `TenantDto`.
*   **Response**: `200 OK`.

---

## 4. User Management Controller (`/api/v1/users`)
*Standard CRUD operations for user entities.*

### Get User by ID
*   **Endpoint**: `GET /api/v1/users/{id}`
*   **Response**: User entity.

### Get User by Email
*   **Endpoint**: `GET /api/v1/users/email/{email}`
*   **Response**: User entity.

### List Users by Tenant
*   **Endpoint**: `GET /api/v1/users/tenant/{tenantId}`
*   **Response**: List of users belonging to the tenant.

### List Active Users by Tenant
*   **Endpoint**: `GET /api/v1/users/tenant/{tenantId}/active`
*   **Response**: List of active users only.

### Create User
*   **Endpoint**: `POST /api/v1/users`
*   **Request Body**: `User` entity.
*   **Response**: `201 Created`.

### Update User
*   **Endpoint**: `PUT /api/v1/users/{id}`
*   **Request Body**: Updated `User` fields.
*   **Response**: `200 OK`.

### Deactivate User
*   **Endpoint**: `PUT /api/v1/users/{id}/deactivate`
*   **Response**: `200 OK`.

---

## 5. User Preferences Controller (`/api/v1/preferences`)
*Manages user settings like theme, language, and notifications.*

### Get User Preferences
*   **Endpoint**: `GET /api/v1/preferences/user/{userId}`
*   **Response**: `UserPreferences` object.

### Update User Preferences
*   **Endpoint**: `PUT /api/v1/preferences/user/{userId}`
*   **Request Body**: Partial or full `UserPreferences` update.
*   **Response**: `200 OK`.

---

## 6. System Health (`/actuator`)

### Health Check
*   **Endpoint**: `GET /actuator/health`
*   **Description**: Returns service health status.
*   **Response**: `{"status": "UP"}`
