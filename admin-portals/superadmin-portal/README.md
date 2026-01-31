## 👑 **Super Admin Portal**

### **2. `admin-portals/superadmin-portal/README.md`**
```markdown
# Super Admin Portal

Platform administration portal for system owners and super administrators.

## 🎯 Overview
The Super Admin Portal provides comprehensive platform management capabilities for the Logistics Platform owners. It offers complete control over tenants, system configuration, monitoring, and platform-wide analytics.

## ✨ Key Features

### 📊 Platform Dashboard
- **Real-time Monitoring**: Live platform health and performance metrics
- **Revenue Analytics**: Platform-wide revenue tracking and forecasting
- **Tenant Growth**: Tenant acquisition and retention metrics
- **System Metrics**: CPU, memory, database, and service health

### 🏢 Tenant Management
- **Tenant Onboarding**: Complete tenant registration workflow
- **Subscription Management**: Plan upgrades, downgrades, and billing
- **Tenant Configuration**: Custom settings and feature flags per tenant
- **Billing & Invoicing**: Financial management across all tenants

### 👥 User Administration
- **User Management**: Platform user creation and role assignment
- **Access Control**: Fine-grained permissions and role management
- **Audit Logs**: Complete audit trail of all platform activities
- **Security Management**: Password policies, MFA enforcement

### ⚙️ System Configuration
- **Platform Settings**: Global configuration management
- **API Management**: API keys, rate limits, and access control
- **Service Configuration**: Microservices settings and scaling
- **Integration Setup**: Third-party service configurations

### 🚨 Monitoring & Alerts
- **Service Health**: Real-time monitoring of all microservices
- **Error Tracking**: Centralized error logging and debugging
- **Performance Analytics**: Response times and throughput metrics
- **Alert Configuration**: Custom alert rules and notifications

## 🏗️ Architecture

### Frontend Architecture
src/
├── components/ # Reusable UI components
│ ├── common/ # Shared components
│ ├── dashboard/ # Dashboard widgets
│ ├── tenants/ # Tenant management components
│ └── system/ # System configuration components
├── pages/ # Page components
│ ├── Dashboard/ # Main dashboard
│ ├── Tenants/ # Tenant management
│ ├── Users/ # User management
│ └── Settings/ # System settings
├── services/ # API service clients
│ ├── auth.service.ts
│ ├── tenant.service.ts
│ └── analytics.service.ts
├── store/ # State management (Redux)
│ ├── slices/ # Redux slices
│ └── store.ts
└── utils/ # Utility functions

text

### Backend Architecture
com.logistics.superadmin
├── config/ # Configuration classes
├── controller/ # REST controllers
│ ├── TenantController.java
│ ├── UserController.java
│ └── DashboardController.java
├── service/ # Business logic
│ ├── TenantService.java
│ ├── AnalyticsService.java
│ └── SystemService.java
├── repository/ # Data access
│ ├── TenantRepository.java
│ └── UserRepository.java
├── dto/ # Data transfer objects
├── security/ # Security configuration
└── exception/ # Exception handling

text

## 📡 API Endpoints

### Authentication
POST /api/v1/auth/login # Admin login
POST /api/v1/auth/logout # Logout
POST /api/v1/auth/refresh # Refresh token
GET /api/v1/auth/profile # Get admin profile
PUT /api/v1/auth/profile # Update profile
POST /api/v1/auth/change-password # Change password

text

### Tenant Management
GET /api/v1/tenants # List all tenants (paginated)
POST /api/v1/tenants # Create new tenant
GET /api/v1/tenants/{id} # Get tenant details
PUT /api/v1/tenants/{id} # Update tenant
DELETE /api/v1/tenants/{id} # Deactivate tenant
POST /api/v1/tenants/{id}/reset # Reset tenant data
GET /api/v1/tenants/{id}/stats # Tenant statistics
POST /api/v1/tenants/{id}/billing # Manage billing
GET /api/v1/tenants/export # Export tenants data

text

### User Management
GET /api/v1/users # List platform users
POST /api/v1/users # Create user
GET /api/v1/users/{id} # Get user details
PUT /api/v1/users/{id} # Update user
DELETE /api/v1/users/{id} # Delete user
POST /api/v1/users/{id}/roles # Assign roles
GET /api/v1/users/{id}/activity # User activity logs
POST /api/v1/users/invite # Invite new admin

text

### Platform Dashboard
GET /api/v1/dashboard/metrics # Platform metrics
GET /api/v1/dashboard/revenue # Revenue analytics
GET /api/v1/dashboard/usage # Platform usage statistics
GET /api/v1/dashboard/health # System health status
GET /api/v1/dashboard/alerts # Active alerts
POST /api/v1/dashboard/reports # Generate reports

text

### System Configuration
GET /api/v1/config/platform # Get platform configuration
PUT /api/v1/config/platform # Update platform configuration
GET /api/v1/config/services # Service configurations
PUT /api/v1/config/services/{service} # Update service config
GET /api/v1/config/api-keys # List API keys
POST /api/v1/config/api-keys # Generate API key
DELETE /api/v1/config/api-keys/{id} # Revoke API key

text

### Audit & Monitoring
GET /api/v1/audit/logs # Audit logs
GET /api/v1/audit/logs/{id} # Audit log details
GET /api/v1/monitoring/services # Service health
GET /api/v1/monitoring/metrics # System metrics
GET /api/v1/monitoring/errors # Error logs
POST /api/v1/monitoring/alerts # Configure alerts

text

## ⚙️ Configuration

### Application Properties
```yaml
server:
  port: 8093
  servlet:
    context-path: /superadmin

spring:
  application:
    name: superadmin-portal
  datasource:
    url: jdbc:postgresql://localhost:5432/superadmin_portal_db
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:admin123}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: ${SHOW_SQL:false}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://auth-service:8081
          jwk-set-uri: http://auth-service:8081/.well-known/jwks.json

# Platform Configuration
platform:
  superadmin:
    allowed-ips: ${ALLOWED_IPS:127.0.0.1,0.0.0.0}
    session-timeout: 3600
    mfa-required: true
    audit-log-retention-days: 90
  integration:
    auth-service-url: http://auth-service:8081
    tenant-service-url: http://tenant-service:8082
    analytics-service-url: http://analytics-service:8091
Security Configuration
java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/v1/**").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
🚀 Getting Started
Prerequisites
bash
# Required software
Java 21+
Node.js 18+ (for React frontend)
PostgreSQL 16+
Redis 7+ (for session management)
Local Development Setup
Backend Setup
bash
# Clone and navigate
cd admin-portals/superadmin-portal

# Configure database
docker run -d --name superadmin-db \
  -e POSTGRES_DB=superadmin_portal_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -p 5433:5432 \
  postgres:16-alpine

# Build and run
./mvnw clean install
./mvnw spring-boot:run

# Or with Docker
docker build -t logistics/superadmin-portal:latest .
docker run -p 8093:8093 logistics/superadmin-portal:latest
Frontend Setup (if using React)
bash
cd frontend
npm install
npm start
# Frontend runs on http://localhost:3000
Docker Compose (Full Stack)
yaml
version: '3.8'

services:
  superadmin-portal:
    build: .
    ports:
      - "8093:8093"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_URL=jdbc:postgresql://superadmin-db:5432/superadmin_portal_db
      - REDIS_HOST=redis
      - AUTH_SERVICE_URL=http://auth-service:8081
    depends_on:
      - superadmin-db
      - redis
      - auth-service

  superadmin-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: superadmin_portal_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    volumes:
      - superadmin_db_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
📊 Database Schema
Core Tables
sql
-- Super administrators
CREATE TABLE super_admins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50) DEFAULT 'SUPER_ADMIN',
    is_active BOOLEAN DEFAULT true,
    mfa_enabled BOOLEAN DEFAULT false,
    mfa_secret VARCHAR(255),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Platform audit logs
CREATE TABLE platform_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES super_admins(id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Platform configuration
CREATE TABLE platform_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(255) UNIQUE NOT NULL,
    config_value TEXT,
    data_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_encrypted BOOLEAN DEFAULT false,
    updated_by UUID REFERENCES super_admins(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API keys
CREATE TABLE api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    key_hash VARCHAR(255) UNIQUE NOT NULL,
    owner_id UUID REFERENCES super_admins(id),
    permissions JSONB DEFAULT '[]',
    expires_at TIMESTAMP,
    last_used_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_audit_logs_admin ON platform_audit_logs(admin_id);
CREATE INDEX idx_audit_logs_created ON platform_audit_logs(created_at DESC);
CREATE INDEX idx_api_keys_owner ON api_keys(owner_id);
🔒 Security Implementation
Authentication Flow








Security Features
JWT Authentication: Stateless token-based authentication

Role-Based Access: Fine-grained permission system

Multi-Factor Authentication: Required for sensitive operations

IP Whitelisting: Restrict access to trusted networks

Session Management: Secure session handling

Audit Logging: Complete audit trail

Rate Limiting: API request throttling

Data Encryption: Sensitive data encryption at rest

📱 User Interface
Dashboard Components
typescript
// Example dashboard component structure
const SuperAdminDashboard = () => {
  return (
    <div className="superadmin-dashboard">
      <PlatformHealthWidget />
      <RevenueMetricsWidget />
      <TenantGrowthWidget />
      <SystemPerformanceWidget />
      <RecentActivitiesWidget />
      <AlertNotificationsWidget />
    </div>
  );
};

// Tenant management interface
const TenantManagement = () => {
  return (
    <TenantDataGrid
      columns={['name', 'plan', 'status', 'users', 'revenue']}
      actions={['view', 'edit', 'suspend', 'billing']}
      filters={['plan', 'status', 'createdDate']}
      exportOptions={['csv', 'excel', 'pdf']}
    />
  );
};
Real-time Updates
typescript
// WebSocket for real-time dashboard updates
const usePlatformMetrics = () => {
  const [metrics, setMetrics] = useState<PlatformMetrics>({});
  
  useEffect(() => {
    const ws = new WebSocket('ws://localhost:8093/ws/platform-metrics');
    
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setMetrics(data);
    };
    
    return () => ws.close();
  }, []);
  
  return metrics;
};
🔄 Integration with Other Services
Service Dependencies
yaml
# Required microservices
dependencies:
  - auth-service:8081       # Authentication
  - tenant-service:8082     # Tenant data
  - billing-service:8083    # Financial data
  - analytics-service:8091  # Analytics
  - notification-service:8092 # Alerts
Feign Clients
java
@FeignClient(name = "tenant-service", url = "${platform.integration.tenant-service-url}")
public interface TenantServiceClient {
    
    @GetMapping("/api/v1/tenants")
    ResponseEntity<Page<TenantDto>> getTenants(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(required = false) String sortBy
    );
    
    @PostMapping("/api/v1/tenants")
    ResponseEntity<TenantDto> createTenant(@RequestBody TenantCreateRequest request);
    
    @GetMapping("/api/v1/tenants/{id}/stats")
    ResponseEntity<TenantStatsDto> getTenantStats(@PathVariable String id);
}
🧪 Testing
Test Categories
bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify -Pintegration

# API tests
npm run test:api  # or mvn test -Dtest=*ApiTest

# Security tests
npm run test:security

# Performance tests
npm run test:performance
Test Coverage
yaml
# jacoco configuration for code coverage
jacoco:
  minimum-coverage:
    line: 80%
    branch: 75%
    class: 85%
  excluded-classes:
    - "*Application"
    - "*Config"
    - "*DTO"
📈 Performance Optimization
Caching Strategy
java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            )
            .withInitialCacheConfigurations(Map.of(
                "platform-metrics", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(1)),
                "tenant-list", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5))
            ))
            .build();
    }
}
Database Optimization
sql
-- Performance indexes
CREATE INDEX idx_tenants_created ON tenants(created_at DESC);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_audit_timestamp ON platform_audit_logs(created_at DESC);

-- Query optimization
EXPLAIN ANALYZE SELECT * FROM tenants 
WHERE status = 'ACTIVE' 
ORDER BY created_at DESC 
LIMIT 50;
🚀 Deployment
Production Configuration
yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Never use update in production
    show-sql: false

security:
  jwt:
    secret: ${JWT_SECRET:change-me-in-production}
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
Kubernetes Deployment
yaml
# kubernetes/superadmin-portal/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: superadmin-portal
  namespace: logistics-platform
spec:
  replicas: 2
  selector:
    matchLabels:
      app: superadmin-portal
  template:
    metadata:
      labels:
        app: superadmin-portal
    spec:
      containers:
      - name: superadmin-portal
        image: logistics/superadmin-portal:${VERSION}
        ports:
        - containerPort: 8093
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8093
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8093
          initialDelaySeconds: 30
          periodSeconds: 5
📚 Documentation
API Documentation
Swagger UI: http://localhost:8093/swagger-ui.html

OpenAPI Spec: http://localhost:8093/v3/api-docs

Postman Collection: docs/api/superadmin-portal.postman_collection.json

User Guides
Admin User Manual

Troubleshooting Guide

Security Best Practices

🤝 Contributing
Development Guidelines
Follow the established code style and conventions

Write comprehensive tests for new features

Update documentation for API changes

Perform security review for sensitive changes

Get code review from at least one senior developer

Branch Strategy
text
main           -> Production releases
develop        -> Development integration
feature/*      -> New features
bugfix/*       -> Bug fixes
release/*      -> Release preparation
hotfix/*       -> Critical production fixes
📞 Support & Maintenance
Monitoring
Dashboard: http://localhost:8093/actuator

Metrics: Prometheus endpoints

Logs: Centralized logging with ELK stack

Alerts: Configured in Alertmanager

Backup Procedures
bash
# Database backup
pg_dump -h localhost -U admin superadmin_portal_db > backup_$(date +%Y%m%d).sql

# Configuration backup
tar -czf config_backup_$(date +%Y%m%d).tar.gz config/

# Audit logs backup
aws s3 sync /var/log/superadmin-portal/ s3://backup-bucket/logs/
Disaster Recovery
Database restoration from latest backup

Configuration restoration

Service restart with recovery mode

Data consistency verification