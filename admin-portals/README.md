. admin-portals/README.md
markdown
# Admin Portals

Administrative interfaces for the Logistics Platform management.

## 🎯 Overview
This directory contains the administration portals for managing the Logistics Platform at different levels:
- **Super Admin Portal**: Platform-level administration (for platform owners)
- **Tenant Admin Portal**: Tenant-level administration (for client administrators)

## 📁 Portal Structure
admin-portals/
├── superadmin-portal/ # Platform administration portal
│ ├── README.md
│ ├── pom.xml
│ ├── src/
│ └── docker/
├── tenantadmin-portal/ # Client administration portal
│ ├── README.md
│ ├── pom.xml
│ ├── src/
│ └── docker/
└── shared/ # Shared portal components
├── components/
├── styles/
└── utils/

text

## 🎨 Technology Stack

### Frontend
- **Framework**: React 18+ with TypeScript (or Vaadin for Java-based UI)
- **State Management**: Redux Toolkit / React Context
- **UI Components**: Material-UI / Ant Design / PrimeReact
- **Charts**: Recharts / Chart.js
- **Maps**: Leaflet / Mapbox GL
- **Forms**: Formik / React Hook Form
- **Tables**: Material-UI DataGrid / React Table

### Backend (Each Portal)
- **Framework**: Spring Boot 4.0.2
- **Security**: Spring Security with JWT
- **API Client**: Spring Cloud OpenFeign
- **Templates**: Thymeleaf (if server-side rendering)
- **WebSocket**: For real-time updates
- **Caching**: Redis

## 🔐 Authentication & Authorization

### Super Admin Portal
- **Access**: Platform owners and super administrators
- **Permissions**: Full system access
- **Multi-factor**: Required for sensitive operations
- **Audit**: Complete audit trail logging

### Tenant Admin Portal
- **Access**: Tenant administrators and managers
- **Permissions**: Tenant-scoped access only
- **Roles**: ADMIN, MANAGER, DISPATCHER, VIEWER
- **Tenant Isolation**: Data restricted to tenant scope

## 📱 Features Comparison

| Feature | Super Admin Portal | Tenant Admin Portal |
|---------|-------------------|---------------------|
| Tenant Management | ✅ Full CRUD | ❌ View only (own tenant) |
| Platform Monitoring | ✅ Full access | ❌ Limited metrics |
| Billing Management | ✅ All tenants | ✅ Own tenant only |
| User Management | ✅ Platform users | ✅ Tenant users only |
| System Configuration | ✅ Full access | ❌ None |
| Audit Logs | ✅ All activities | ✅ Tenant activities only |
| Reports | ✅ Platform-wide | ✅ Tenant-specific |

## 🚀 Development Setup

### Prerequisites
```bash
# Node.js (for React frontend)
node --version  # >= 18.x
npm --version   # >= 9.x

# Java
java --version  # >= 21

# Database
docker run -d --name portal-postgres -p 5432:5432 \
  -e POSTGRES_PASSWORD=admin123 \
  -e POSTGRES_USER=admin \
  -e POSTGRES_DB=portal_db \
  postgres:16-alpine
Installation
bash
# Clone the repository
git clone <repository-url>
cd logistics-platform/admin-portals

# Install dependencies for each portal
cd superadmin-portal
npm install  # or mvn clean install for Java
cd ../tenantadmin-portal
npm install  # or mvn clean install for Java
Running Locally
bash
# Option 1: Run both portals
docker-compose up

# Option 2: Run individually
cd superadmin-portal
npm start  # Frontend on :3000
./mvnw spring-boot:run  # Backend on :8093

cd ../tenantadmin-portal
npm start  # Frontend on :3001
./mvnw spring-boot:run  # Backend on :8094
🔧 Configuration
Environment Variables
bash
# Super Admin Portal
REACT_APP_API_URL=http://localhost:8093/api
REACT_APP_WS_URL=ws://localhost:8093/ws
REACT_APP_AUTH_URL=http://localhost:8081
REACT_APP_ENVIRONMENT=development

# Tenant Admin Portal
REACT_APP_API_URL=http://localhost:8094/api
REACT_APP_WS_URL=ws://localhost:8094/ws
REACT_APP_AUTH_URL=http://localhost:8081
REACT_APP_TENANT_ID=${CURRENT_TENANT_ID}
Database Configuration
yaml
# application.yml for each portal
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/${PORTAL_NAME}_db
    username: admin
    password: admin123
    hikari:
      maximum-pool-size: 10
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
📡 API Integration
Required Services
Auth Service: Authentication and user management

Tenant Service: Tenant information and settings

Order Service: Order data and statistics

Tracking Service: Real-time driver locations

Analytics Service: Dashboard metrics and reports

Notification Service: Admin notifications

API Endpoints
typescript
// Example API client structure
interface PortalApiClient {
  // Authentication
  login(credentials: LoginRequest): Promise<AuthResponse>;
  logout(): Promise<void>;
  refreshToken(): Promise<TokenResponse>;
  
  // Tenant Management (Super Admin only)
  getTenants(): Promise<Tenant[]>;
  createTenant(tenant: TenantCreateRequest): Promise<Tenant>;
  updateTenant(id: string, updates: TenantUpdateRequest): Promise<Tenant>;
  
  // User Management
  getUsers(): Promise<User[]>;
  inviteUser(invitation: UserInviteRequest): Promise<void>;
  updateUserRole(userId: string, role: UserRole): Promise<User>;
  
  // Dashboard Data
  getDashboardMetrics(): Promise<DashboardMetrics>;
  getRecentOrders(): Promise<Order[]>;
  getDriverStatus(): Promise<DriverStatus[]>;
  
  // Reports
  generateReport(request: ReportRequest): Promise<Report>;
  exportData(format: 'csv' | 'excel' | 'pdf'): Promise<Blob>;
}
🎯 Portal Features
Super Admin Portal Features
Platform Dashboard

Real-time platform health monitoring

Revenue and usage analytics

Tenant growth metrics

System performance indicators

Tenant Management

Tenant onboarding wizard

Subscription plan management

Tenant configuration

Billing and invoicing

System Administration

User role management

API key management

System configuration

Audit log viewer

Monitoring & Alerts

Service health monitoring

Error tracking and debugging

Performance metrics

Alert configuration

Tenant Admin Portal Features
Tenant Dashboard

Order volume and status

Driver performance metrics

Customer satisfaction scores

Revenue and cost analytics

Order Management

Order creation and editing

Bulk order processing

Order tracking and status updates

Proof of delivery management

Driver Management

Driver onboarding and profiles

Shift scheduling

Performance monitoring

Payment and settlement

Customer Management

Customer database

Address management

Communication history

Feedback and reviews

🚀 Deployment
Docker Deployment
yaml
# docker-compose.yml
version: '3.8'

services:
  superadmin-portal:
    build: ./superadmin-portal
    ports:
      - "8093:8093"
    environment:
      - DB_URL=jdbc:postgresql://postgres:5432/superadmin_portal_db
      - AUTH_SERVICE_URL=http://auth-service:8081
    depends_on:
      - postgres
      - auth-service

  tenantadmin-portal:
    build: ./tenantadmin-portal
    ports:
      - "8094:8094"
    environment:
      - DB_URL=jdbc:postgresql://postgres:5432/tenantadmin_portal_db
      - AUTH_SERVICE_URL=http://auth-service:8081
    depends_on:
      - postgres
      - auth-service

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_MULTIPLE_DATABASES: superadmin_portal_db,tenantadmin_portal_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
Kubernetes Deployment
yaml
# kubernetes/portals/
├── superadmin-portal/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   └── configmap.yaml
├── tenantadmin-portal/
│   ├── deployment.yaml
│   └── service.yaml
└── shared/
    ├── postgres.yaml
    └── redis.yaml
🔒 Security Considerations
Authentication
JWT-based authentication

Session management with Redis

Multi-factor authentication for super admins

SSO integration support

Authorization
Role-based access control (RBAC)

Tenant data isolation

API rate limiting

IP whitelisting for sensitive operations

Data Protection
Encrypted database fields

Secure password policies

Audit logging for all admin actions

Regular security audits

📊 Monitoring & Analytics
Logging
yaml
logging:
  level:
    com.logistics.portal: DEBUG
  file:
    name: logs/portal-${spring.application.name}.log
    max-size: 10MB
    max-history: 30
Metrics
Portal response times

User activity tracking

Error rates and debugging

Performance bottlenecks

Alerts
Failed login attempts

Suspicious activities

System errors

Performance degradation

🧪 Testing
Test Categories
bash
# Unit Tests
npm test  # or mvn test

# Integration Tests
npm run test:integration

# E2E Tests (Cypress)
npm run cypress:open

# Security Tests
npm run security:scan
Test Data
typescript
// Mock data for development
const mockTenants = [
  {
    id: 'tenant-1',
    name: 'ABC Logistics',
    plan: 'ENTERPRISE',
    users: 45,
    status: 'ACTIVE'
  }
];

const mockOrders = [
  {
    id: 'order-123',
    customer: 'John Doe',
    status: 'DELIVERED',
    amount: 1250.00,
    driver: 'Driver Smith'
  }
];
📱 Responsive Design
Breakpoints
css
/* Mobile-first responsive design */
@media (min-width: 768px) { /* Tablet */ }
@media (min-width: 1024px) { /* Desktop */ }
@media (min-width: 1440px) { /* Large Desktop */ }
Supported Devices
Desktop browsers (Chrome, Firefox, Safari, Edge)

Tablets (iPad, Android tablets)

Mobile devices (responsive design)

🔄 Continuous Integration
GitHub Actions
yaml
# .github/workflows/portal-ci.yml
name: Portal CI/CD

on:
  push:
    paths:
      - 'admin-portals/**'
      - '.github/workflows/portal-ci.yml'

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run tests
        run: |
          cd admin-portals/superadmin-portal
          npm install
          npm test
          
          cd ../tenantadmin-portal
          npm install
          npm test
📚 Documentation
Developer Documentation
API Documentation

Component Library

State Management

Testing Guide

User Documentation
Admin User Guide

Tenant Admin Guide

Troubleshooting

🤝 Contributing
Development Workflow
Create feature branch from main

Implement changes with tests

Update documentation

Create pull request

Code review and merge

Code Standards
Follow ESLint/Prettier configuration

Write comprehensive tests

Document new components and APIs

Maintain responsive design

📞 Support
Issue Reporting
GitHub Issues: portal-issues

Email: portals-support@logistics-platform.com

Slack: #admin-portals channel

Maintenance
Regular security updates

Performance optimization

Feature enhancements

Bug fixes and patches