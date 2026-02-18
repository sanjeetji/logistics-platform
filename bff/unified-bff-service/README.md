# Unified BFF Service

## Overview
The single Backend-for-Frontend (BFF) entry point for all clients (Web, Mobile, B2B, B2C).
Aggregates data from backend microservices.

## Architecture
- **Unified Controller**: `OrderController` handles requests and delegates to specific adapters.
- **Adapters**: Connects to `order-service` (Core), `b2b-order-service` (Adapter), `parcel-service` (Adapter).

## Build & Run
```bash
mvn clean package
java -jar target/unified-bff-service-1.0.0-SNAPSHOT.jar
```
