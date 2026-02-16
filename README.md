# Logistics Platform - Enterprise Microservices System

**Version**: 1.0.0-SNAPSHOT  
**Last Updated**: February 16, 2026  
**Total Services**: 44 microservices

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Monitoring](#monitoring)
- [Contributing](#contributing)

---

## 🎯 Overview

The Logistics Platform is a comprehensive enterprise-grade microservices system designed for end-to-end logistics and supply chain management. It supports B2C, B2B, and parcel delivery operations with advanced features including real-time tracking, route optimization, automated billing, and streaming analytics.

### Key Features

- **Multi-Tenant Architecture**: Support for multiple organizations
- **Real-Time Tracking**: WebSocket-based live location updates
- **Advanced Route Optimization**: ML-powered ETA prediction, multi-objective optimization
- **Dynamic Re-Routing**: 6 trigger types for adaptive routing
- **Automated Billing**: Subscription-based and usage-based billing
- **Streaming Analytics**: Real-time metrics and dashboards
- **Event-Driven**: Kafka-based event streaming
- **API Gateway**: Centralized routing with rate limiting
- **Service Discovery**: Eureka-based service registry
- **Distributed Tracing**: End-to-end request tracing

---

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                        │
│                     (Rate Limiting, Routing)                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼────────┐  ┌───────▼────────┐
│  Platform Core │  │  Shared Services │  │   B2B Engine   │
│   (8 services) │  │   (28 services)  │  │  (4 services)  │
└────────────────┘  └──────────────────┘  └────────────────┘
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼────────┐  ┌───────▼────────┐
│   PostgreSQL   │  │     Kafka       │  │     Redis      │
│   (Database)   │  │  (Event Stream) │  │    (Cache)     │
└────────────────┘  └──────────────────┘  └────────────────┘
```

### Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.3.5, Spring Cloud 2023.0.3 |
| **Language** | Java 21 |
| **Build Tool** | Maven 3.9+ |
| **Database** | PostgreSQL 15+ |
| **Message Broker** | Apache Kafka 3.5+ |
| **Cache** | Redis 7.0+ |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Config Server** | Spring Cloud Config |
| **Monitoring** | Micrometer, Prometheus |
| **Logging** | SLF4J, Logback |
| **Documentation** | Swagger/OpenAPI 3.0 |

---

## 📦 Services

### Infrastructure Services (4)

| Service | Port | Description |
|---------|------|-------------|
| **config-server** | 8888 | Centralized configuration management |
| **service-discovery** | 8761 | Eureka service registry |
| **api-gateway** | 8080 | API gateway with routing and rate limiting |
| **configuration-service** | 8084 | Dynamic configuration management |

### Platform Core (8)

| Service | Port | Description |
|---------|------|-------------|
| **auth-service** | 8081 | Authentication and authorization |
| **tenant-service** | 8082 | Multi-tenant management |
| **fleet-service** | 8083 | Fleet and vehicle management |
| **order-service** | 8085 | Order management and lifecycle |
| **dispatch-service** | 8086 | Order dispatch and assignment |
| **pricing-service** | 8087 | Dynamic pricing and quotes |
| **customer-service** | 8088 | Customer management |
| **driver-app-service** | 8089 | Driver mobile app backend |

### Shared Services (28)

| Service | Port | Description |
|---------|------|-------------|
| **orchestration-service** | 8090 | Workflow orchestration |
| **wallet-service** | 8091 | Digital wallet management |
| **promo-code-service** | 8092 | Promotions and discounts |
| **loyalty-service** | 8093 | Loyalty program management |
| **team-service** | 8094 | Team and role management |
| **tracking-service** | 8095 | Real-time location tracking |
| **payment-service** | 8096 | Payment processing |
| **notification-service** | 8097 | Multi-channel notifications |
| **billing-service** | 8098 | Automated billing |
| **document-service** | 8099 | Document management |
| **geo-service** | 8100 | Geocoding and geofencing |
| **audit-log-service** | 8101 | Audit trail and compliance |
| **chat-service** | 8102 | In-app messaging |
| **integration-service** | 8103 | Third-party integrations |
| **master-data-service** | 8104 | Master data management |
| **payout-service** | 8105 | Driver payouts |
| **rating-service** | 8106 | Ratings and reviews |
| **search-service** | 8107 | Elasticsearch-based search |
| **streaming-analytics-service** | 8108 | Real-time analytics |
| **user-management-service** | 8109 | User management |
| **route-optimization-service** | 8110 | Advanced route optimization |
| **tenant-onboarding-service** | 8111 | Tenant onboarding automation |
| **shift-management-service** | 8112 | Driver shift management |
| **edi-integration-service** | 8113 | EDI integrations |
| **sla-service** | 8114 | SLA monitoring |
| **exception-management-service** | 8115 | Exception handling |
| **location-hub-service** | 8116 | Location data aggregation |
| **control-tower-service** | 8117 | Operations dashboard |

### B2B Engine (4)

| Service | Port | Description |
|---------|------|-------------|
| **shipment-service** | 8118 | B2B shipment management |
| **warehouse-service** | 8119 | Warehouse operations |
| **inventory-service** | 8120 | Inventory management |
| **compliance-service** | 8121 | Regulatory compliance |

### Additional Services (4)

| Service | Port | Description |
|---------|------|-------------|
| **b2b-order-service** | 8122 | B2B order processing |
| **parcel-service** | 8123 | Parcel tracking |
| **returns-service** | 8124 | Returns management |
| **webhook-worker-service** | 8125 | Webhook processing |

### BFF Services (3)

| Service | Port | Description |
|---------|------|-------------|
| **driver-app-bff** | 8126 | Driver app BFF |
| **customer-app-bff** | 8127 | Customer app BFF |
| **unified-bff-service** | 8128 | Unified BFF |

### Rules Engine (1)

| Service | Port | Description |
|---------|------|-------------|
| **rules-engine-service** | 8129 | Business rules engine |

---

## 🔧 Prerequisites

### Required Software

- **Java 21** or higher
- **Maven 3.9+**
- **PostgreSQL 15+**
- **Apache Kafka 3.5+**
- **Redis 7.0+**
- **Docker** (optional, for containerized deployment)
- **Docker Compose** (optional)

### Environment Variables

Create a `.env` file in the project root:

```bash
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=logistics
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Google Maps API
GOOGLE_MAPS_API_KEY=your_api_key_here

# ML Service
ML_SERVICE_URL=http://localhost:5000

# SMTP
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

---

## 🚀 Quick Start

### Option 1: Run Without Docker

#### 1. Start Infrastructure Services

```bash
# Start PostgreSQL
brew services start postgresql@15

# Start Kafka (with Zookeeper)
brew services start zookeeper
brew services start kafka

# Start Redis
brew services start redis
```

#### 2. Build All Services

```bash
cd logistics-platform
mvn clean install -DskipTests
```

#### 3. Start Services in Order

See [ImportantCommands.md](./ImportantCommands.md) for detailed startup sequence.

### Option 2: Run With Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Or start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

---

## ⚙️ Configuration

### Application Profiles

- **default**: Local development
- **dev**: Development environment
- **staging**: Staging environment
- **prod**: Production environment

### Config Server

All service configurations are managed centrally via `config-server` (port 8888).

Configuration files are located in: `infrastructure/config-server/src/main/resources/config/`

---

## 📚 API Documentation

### Swagger UI

Access Swagger UI for each service:

```
http://localhost:{port}/swagger-ui.html
```

Example:
- API Gateway: http://localhost:8080/swagger-ui.html
- Order Service: http://localhost:8085/swagger-ui.html
- Route Optimization: http://localhost:8110/swagger-ui.html

### API Endpoints

See [API_DOCUMENTATION.md](./docs/API_DOCUMENTATION.md) for complete API reference.

---

## 📊 Monitoring

### Health Checks

```bash
# Check service health
curl http://localhost:{port}/actuator/health

# Check all registered services
curl http://localhost:8761/eureka/apps
```

### Metrics

Metrics are exposed via Micrometer:

```
http://localhost:{port}/actuator/metrics
http://localhost:{port}/actuator/prometheus
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

Copyright © 2026 Logistics Platform. All rights reserved.

---

## 📞 Support

For support, email support@logistics-platform.com or create an issue in the repository.