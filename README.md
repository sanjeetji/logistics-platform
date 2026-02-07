# Logistics Platform (Global SaaS Edition)

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-green.svg) ![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)

A comprehensive, microservices-based logistics platform designed for global scale. This platform combines the best features of **B2B Logistics (Bringg model)** and **B2C Last-Mile Delivery (Porter/Uber model)** into a single unified ecosystem.

## 🌍 Global SaaS Ready
This repository represents the **Global Expansion** phase of the project, including:
-   **Multi-Currency Pricing**: Dynamic currency conversion (USD, EUR, INR) in `pricing-service`.
-   **Localization (i18n)**: Backend support for multiple languages in `driver-api`.
-   **GDPR Compliance**: Data residency strategy and PII protection readiness.
-   **Green Logistics**: CO2 emission calculation and EV-routing readiness.
-   **E-Commerce Integration**: Webhook receivers for Shopify, WooCommerce, and a Generic API for custom platforms.

## 🔌 Multi-Channel Integration

The platform provides a unified interface to ingest orders from various sources (`order-service`).

### 1. Standard API (Custom Platforms)
Use this endpoint to push orders from any custom system (ERP, POS, etc).
`POST /api/v1/integration/orders`
```json
{
  "platform": "CUSTOM_ERP",
  "externalOrderId": "ORDER_999",
  "customerEmail": "client@company.com",
  "items": [...],
  "totalPrice": 150.00
}
```

### 2. Shopify Webhook
Configure your Shopify Store to send `orders/create` webhooks to:
`POST /api/v1/integration/shopify/webhook`

### 3. WooCommerce Webhook
Configure WooCommerce to send `Order Created` webhooks to:
`POST /api/v1/integration/woocommerce/webhook`

## 🏗 Architecture

The platform follows a **Domain-Driven Design (DDD)** microservices architecture:

### Core Layer (`platform-core`)
-   **Auth Service**: OAuth2/OIDC authentication using `jjwt`.
-   **User Service**: User profile and identity management.
-   **Order Service**: Centralized order lifecycle management.
-   **Fleet Service**: Driver and vehicle management.
-   **Pricing Service**: Dynamic pricing engine with surge and multi-currency support.

### B2B Engine (`b2b-engine`)
-   **Route Service**: Route optimization and planning.
-   **Warehouse Service**: Inventory and fulfillment logic.
-   **Compliance Service**: POD (Proof of Delivery) and document management.

### B2C Engine (`b2c-engine`)
-   **Parcel Service**: On-demand package delivery.
-   **Quick Dispatch**: Instant driver matching algorithms.

### Shared Services (`shared-services`)
-   **Tracking Service**: Real-time location tracking via WebSockets and Redis Geo.
-   **Notification Service**: SMS (Twilio), Email (SendGrid), and Push notifications.
-   **Billing Service**: Invoicing and payment processing.
-   **Analytics Service**: Data aggregation and BI dashboards.

## 🚀 Getting Started

### Prerequisites
-   Java 21
-   Maven 3.9+
-   Docker & Docker Compose

### Fast Start (Docker)
1.  **Build all services**:
    ```bash
    ./run-platform.sh build
    ```
2.  **Start the platform**:
    ```bash
    ./run-platform.sh start
    ```
    This will spin up Zookeeper, Kafka, Redis, Postgres, and all 20+ microservices.

3.  **Access the API**:
    -   API Gateway: `http://localhost:8080`
    -   Service Discovery (Eureka): `http://localhost:8761`
    -   Zipkin Tracing: `http://localhost:9411`

## 🛠 Key Features

### 1. Advanced Pricing Engine
-   Calculates base fare, distance fare, time fare, and surge pricing.
-   Supports multi-currency estimates via `POST /api/v1/pricing/estimate`.

### 2. Real-Time Tracking
-   WebSocket-based live tracking.
-   Redis Geo for efficient "nearby driver" queries.

### 3. Green Logistics
-   CO2 emission calculation per trip.
-   EV-aware routing logic.

### 4. Global Compliance
-   GDPR-ready architecture (Tenant-based sharding strategy).
-   PII protection patterns.

## 🧪 Testing

Run integration tests using the provided script:
```bash
./run-integration-test.sh
```

## 📚 Documentation
-   [API Documentation (Postman)](./postman_collection.json)
-   [Global Expansion Strategy](./docs/global_expansion_strategy.md)
-   [GDPR Audit Report](./docs/gdpr_audit_report.md)

## 📄 License
This project is licensed under the MIT License.