### **2. `platform-core/tenant-service/README.md`**
```markdown
# Tenant Service

Tenant management and onboarding service for multi-tenant SaaS architecture.

## 🎯 Purpose
Manages tenant lifecycle from registration to deactivation, including configuration management, user association, and subscription integration in a multi-tenant logistics platform.

## ✨ Features
- **Tenant Onboarding**: Complete registration workflow with email verification
- **Multi-tier Subscriptions**: FREE, PRO, ENTERPRISE plans with feature flags
- **Custom Configuration**: Tenant-specific settings (JSON schema validation)
- **User Management**: Add/remove users with role assignments (OWNER, ADMIN, etc.)
- **Billing Integration**: Seamless integration with billing service
- **Analytics**: Tenant usage tracking and reporting
- **White-labeling**: Custom branding and domain configuration

## 🏗️ Architecture
Tenant Service → PostgreSQL (tenant data)
→ Redis (caching)
→ RabbitMQ (events)
→ Auth Service (user validation)
→ Billing Service (subscriptions)

text

## 📡 API Endpoints

### Tenant Management
POST /api/v1/tenants # Create new tenant
GET /api/v1/tenants # List tenants (paginated)
GET /api/v1/tenants/{tenantId} # Get tenant details
PUT /api/v1/tenants/{tenantId} # Update tenant
PATCH /api/v1/tenants/{tenantId}/status # Update tenant status
DELETE /api/v1/tenants/{tenantId} # Soft delete tenant

text

### Tenant Configuration
GET /api/v1/tenants/{tenantId}/config # Get configuration
PUT /api/v1/tenants/{tenantId}/config # Update configuration
POST /api/v1/tenants/{tenantId}/config/validate # Validate configuration

text

### User Management
POST /api/v1/tenants/{tenantId}/users # Add user to tenant
GET /api/v1/tenants/{tenantId}/users # List tenant users
GET /api/v1/tenants/{tenantId}/users/{userId} # Get user details
PUT /api/v1/tenants/{tenantId}/users/{userId} # Update user role
DELETE /api/v1/tenants/{tenantId}/users/{userId} # Remove user from tenant

text

### Onboarding Workflow
POST /api/v1/tenants/onboard/initiate # Start onboarding
POST /api/v1/tenants/onboard/verify-email # Verify email
POST /api/v1/tenants/onboard/company # Add company details
POST /api/v1/tenants/onboard/billing # Setup billing
POST /api/v1/tenants/onboard/complete # Complete onboarding
GET /api/v1/tenants/onboard/{tenantId}/status # Check onboarding status

text

## 🗄️ Database Schema

### Tenants Table
```sql
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    domain VARCHAR(255),
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20),
    subscription_plan VARCHAR(20) DEFAULT 'FREE',
    subscription_status VARCHAR(20) DEFAULT 'ACTIVE',
    config JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trial_ends_at TIMESTAMP,
    billing_details JSONB,
    is_active BOOLEAN DEFAULT true
);
Tenant Users Table
sql
CREATE TABLE tenant_users (
    tenant_id UUID REFERENCES tenants(id),
    user_id UUID NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invited_by UUID,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    PRIMARY KEY (tenant_id, user_id)
);
⚙️ Configuration
yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/logistics_tenants
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

app:
  tenant:
    default-plan: FREE
    trial-days: 14
    max-users:
      FREE: 5
      PRO: 50
      ENTERPRISE: 500
🔄 Integration Points
With Auth Service
Validate user existence before adding to tenant

Synchronize user status changes

JWT token validation for tenant context

With Billing Service
Create subscription on tenant creation

Update subscription on plan change

Handle billing events (payment success/failure)

With Notification Service
Send onboarding emails

Tenant status change notifications

User invitation emails

Event Publishing
java
// Example events
TenantCreatedEvent
TenantUpdatedEvent
UserAddedToTenantEvent
TenantSubscriptionChangedEvent
🚀 Getting Started
Local Development
bash
# 1. Navigate to service
cd platform-core/tenant-service

# 2. Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Or using Maven wrapper
./mvnw spring-boot:run
Docker
bash
# Build image
docker build -t logistics/tenant-service:1.0.0 .

# Run container
docker run -p 8082:8082 \
  -e DB_URL=jdbc:postgresql://postgres:5432/logistics_tenants \
  -e REDIS_HOST=redis \
  logistics/tenant-service:1.0.0
Environment Variables
bash
DB_URL=jdbc:postgresql://localhost:5432/logistics_tenants
DB_USERNAME=postgres
DB_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
JWT_SECRET=your-jwt-secret-key
🧪 Testing
bash
# Unit tests
mvn test

# Integration tests
mvn verify -Pintegration

# With coverage
mvn jacoco:report

# Test specific package
mvn test -Dtest=TenantServiceTest
📊 Monitoring
Health endpoint: GET /actuator/health

Metrics: GET /actuator/metrics

Info: GET /actuator/info

Prometheus: GET /actuator/prometheus

🔧 Database Migrations
bash
# Using Flyway (if configured)
mvn flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf

# Or using Liquibase
mvn liquibase:update
📈 Tenant Plans Comparison
Feature	FREE	PRO	ENTERPRISE
Max Users	5	50	500
Storage	1 GB	10 GB	100 GB
API Rate Limit	100/hr	1000/hr	10000/hr
Support	Community	Email	24/7 Phone
Custom Domain	❌	✅	✅
White-labeling	❌	Basic	Full
SLA	❌	99.5%	99.9%
🚨 Error Handling
Common error responses:

400 Bad Request: Invalid input data

401 Unauthorized: Invalid or missing authentication

403 Forbidden: Insufficient permissions

404 Not Found: Tenant not found

409 Conflict: Tenant slug/domain already exists

429 Too Many Requests: Rate limit exceeded

📝 Notes
Tenant slugs are immutable once created

Soft delete implementation for compliance

Audit logging for all tenant modifications

Rate limiting per tenant basis

Configuration validation using JSON schema