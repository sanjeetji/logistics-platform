# Local Development Setup Guide (No Docker for Services)

This guide explains how to run the Logistics Platform completely on your local machine without Docker for the application services.

> **Note**: We still recommend using Docker for *Infrastructure* (Postgres, Kafka, Redis) because installing them manually is complex. However, if you want a 100% native setup, instructions are below.

---

## 1. Prerequisites (Local Environment)

Since you are running the platform **locally without Docker**, you must install and configure all infrastructure components manually on your machine.

### Required Software
*   **Java 17+** (`java -version`) - Required for running services.
*   **Maven 3.8+** (`mvn -version`) - Required for building the project.
*   **PostgreSQL 14+** - Primary database.
    *   *GUI Tool*: **PgAdmin 4** or **DBeaver** (Required to manage DB, view tables manually).
*   **Redis 7+** - Caching and session management.
    *   *GUI Tool*: **RedisInsight** (Optional, for debugging cache).
*   **Kafka 3.5+** - Event streaming.
    *   *GUI Tool*: **Conduktor** or **Offset Explorer** (Optional, to view topics/messages).
*   **RabbitMQ** - (Optional, check if legacy services require it).

> **Crucial Difference vs Docker**: When running locally, **YOU** are responsible for installing, starting, and connecting these tools. When using Docker, these are provided automatically as containers.

---

## 2. Infrastructure Setup (Manual Steps)

### A. PostgreSQL Setup
1.  **Install**: Download from [postgresql.org](https://www.postgresql.org/download/) or use `brew install postgresql` (Mac).
2.  **Start Service**: `brew services start postgresql`.
3.  **Configure User/DB**:
    *   User: `postgres`
    *   Password: `password` (or update `application.yml` in config-server to match yours).
    *   Database: `logistics_db` (Create this using PgAdmin).
4.  **Verify**: Open PgAdmin, connect to localhost:5432, and ensure you can see the database.

### B. Redis Setup
1.  **Install**: `brew install redis`.
2.  **Start**: `brew services start redis`.
3.  **Verify**: Run `redis-cli ping` -> Should reply `PONG`.

### C. Kafka & Zookeeper Setup
1.  **Install**: `brew install kafka`.
2.  **Start Zookeeper**: `zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties`.
3.  **Start Kafka**: `kafka-server-start /usr/local/etc/kafka/server.properties`.

---

## 3. Configuration

By default, services expect infrastructure at `localhost`, so standard configuration usually works. However, check `application.yml` in `platform-core/config-server` to ensure it points to your local resources.

---

## 4. Building the Project

Before running anything, build the entire project to generate necessary JARs.

```bash
mvn clean install -DskipTests
```

---

## 5. Running the Services (Execution Order)

You must start services in this specific order. Open a new terminal tab (or window) for each group.

### Terminal 1: Infrastructure Layers (If not using Docker)
*(Ignore if you ran Option A above)*

### Terminal 2: Config & Discovery (Essential)
These must be running before anything else.
```bash
# 1. Config Server
java -jar platform-core/config-server/target/config-server-*.jar

# 2. Service Discovery (Eureka) -> Wait for Config Server to start first!
java -jar platform-core/service-discovery/target/service-discovery-*.jar
```

### Terminal 3: API Gateway
```bash
java -jar infrastructure/api-gateway/target/api-gateway-*.jar
```

### Terminal 4: Core Authentication
```bash
java -jar platform-core/auth-service/target/auth-service-*.jar
```

### Terminal 5: B2C Vertical (Consumer Operations)
*Run these for the customer-facing app flows.*
```bash
java -jar platform-core/user-service/target/user-service-*.jar &
java -jar platform-core/order-service/target/order-service-*.jar &
java -jar platform-core/fleet-service/target/fleet-service-*.jar &
```

### Terminal 6: B2B Vertical (Business Operations)
*Run these for the enterprise/tenant flows.*
```bash
java -jar platform-core/tenant-service/target/tenant-service-*.jar &
java -jar platform-core/b2b-order-service/target/b2b-order-service-*.jar &
java -jar platform-core/inventory-service/target/inventory-service-*.jar &
```

---

## 6. Accessing the Platform

| Service | URL |
| :--- | :--- |
| **Eureka Dashboard** | [http://localhost:8761](http://localhost:8761) |
| **API Gateway** | [http://localhost:8080](http://localhost:8080) |
| **Config Server** | [http://localhost:8888](http://localhost:8888) |

---

## 7. Troubleshooting

*   **"Connection Refused"**: Check if Config Server or Eureka is actually running.
*   **"Port already in use"**: You might have a detached "java" process or a Docker container still running. Run `killall java` or `docker-compose down`.
*   **"Profile Not Active"**: You may need to explicit set `--spring.profiles.active=local` when running JARs.
    ```bash
    java -jar target/myapp.jar --spring.profiles.active=local
    ```
