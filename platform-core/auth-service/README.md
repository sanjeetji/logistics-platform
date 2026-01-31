markdown
# Auth Service

Central authentication and authorization service for the Logistics Platform.

## Purpose
- Multi-tenant user authentication (login, registration, password management)
- JWT token generation, validation, and refresh
- Role-based access control (RBAC) with permissions
- OAuth2.0 and social login integration
- Session management and security policies

## Technology Stack
- Spring Boot 4.0.2
- Spring Security 6.2+
- Spring Data JPA with PostgreSQL
- Redis for token blacklisting and caching
- JJWT for JWT tokens
- Spring Validation
- MapStruct for DTO mapping

## API Endpoints
POST /api/v1/auth/login - User login (email/password)
POST /api/v1/auth/register - New user registration
POST /api/v1/auth/refresh - Refresh access token
POST /api/v1/auth/logout - User logout (blacklist token)
POST /api/v1/auth/forgot-password - Password reset request
POST /api/v1/auth/reset-password - Password reset confirmation
GET /api/v1/auth/me - Get current user profile
PUT /api/v1/auth/profile - Update user profile
GET /api/v1/auth/tenants - Get user accessible tenants
POST /api/v1/auth/switch-tenant - Switch active tenant

text

## Configuration
- Port: 8081
- Database: `logistics_auth_db`
- Redis: Token blacklist (TTL: 7 days)
- JWT Secret: Environment variable `JWT_SECRET`
- Token Expiry: Access=15min, Refresh=7days

## Dependencies
- `common-dto` for AuthRequest/AuthResponse
- `common-exceptions` for global exception handling
- PostgreSQL driver
- Redis client

## Database Schema
users
├── id (UUID)
├── email (unique)
├── password_hash
├── first_name
├── last_name
├── phone
├── status (ACTIVE, INACTIVE, LOCKED)
├── created_at
└── updated_at

user_roles
├── user_id
├── role_id
└── tenant_id

tenants
├── id (UUID)
├── name
├── domain
├── subscription_plan
├── status
└── settings (JSONB)

text

## Local Development
```bash
cd platform-core/auth-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
Testing
bash
# Unit tests
mvn test

# Integration tests
mvn verify -Pintegration-test

# With test containers
mvn test -Dspring.profiles.active=test
Docker Deployment
bash
# Build
docker build -t logistics/auth-service:1.0.0 .

# Run
docker run -p 8081:8081 \
  -e DB_URL=jdbc:postgresql://postgres:5432/logistics_auth \
  -e REDIS_HOST=redis \
  logistics/auth-service:1.0.0
Environment Variables
bash
DB_URL=jdbc:postgresql://localhost:5432/logistics_auth
DB_USERNAME=postgres
DB_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key-change-in-production
JWT_EXPIRATION=900000
Monitoring
Health: GET /actuator/health

Metrics: GET /actuator/metrics

Prometheus: GET /actuator/prometheus

text

### **2. `platform-core/tenant-service/README.md`**
```markdown
# Tenant Service

Tenant management and onboarding service for multi-tenant architecture.

## Purpose
- Tenant registration and onboarding
- Tenant configuration management
- Subscription and billing integration
- Tenant isolation policies
- Domain and subdomain management

## Features
- Multi-tier subscription plans (Free, Pro, Enterprise)
- Custom tenant configuration (JSON schema)
- Automatic provisioning (DB schema, storage, queues)
- Tenant analytics and usage tracking
- White-label customization

## API Endpoints
POST /api/v1/tenants - Create new tenant
GET /api/v1/tenants - List tenants (admin only)
GET /api/v1/tenants/{id} - Get tenant details
PUT /api/v1/tenants/{id} - Update tenant
DELETE /api/v1/tenants/{id} - Deactivate tenant
POST /api/v1/tenants/{id}/users - Add user to tenant
GET /api/v1/tenants/{id}/users - List tenant users
POST /api/v1/tenants/{id}/config - Update tenant config
GET /api/v1/tenants/{id}/stats - Get tenant statistics
POST /api/v1/tenants/onboard - Complete tenant onboarding

text

## Database Schema
tenants
├── id (UUID)
├── name
├── slug (unique)
├── domain
├── contact_email
├── contact_phone
├── subscription_plan (FREE, PRO, ENTERPRISE)
├── subscription_status (ACTIVE, SUSPENDED, CANCELLED)
├── config (JSONB - custom settings)
├── created_at
├── trial_ends_at
└── billing_details (JSONB)

tenant_users
├── tenant_id
├── user_id
├── role (OWNER, ADMIN, MANAGER, DISPATCHER, DRIVER)
└── joined_at

text

## Integration Points
- Auth Service: User-tenant association
- Billing Service: Subscription management
- Notification Service: Onboarding emails
- Analytics Service: Usage tracking