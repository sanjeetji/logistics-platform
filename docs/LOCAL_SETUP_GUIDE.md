# Local Development Setup Guide (No Docker for Services)

This guide explains how to run the Logistics Platform completely on your local machine without Docker for the application services.

> **Note**: We still recommend using Docker for *Infrastructure* (Postgres, Kafka, Redis) because installing them manually is complex. However, if you want a 100% native setup, instructions are below.

---

## 1. Prerequisites

You must have the following installed on your machine:
*   **Java 17+** (`java -version`)
*   **Maven 3.8+** (`mvn -version`)
*   **PostgreSQL 14+** (Running on port 5432)
*   **Redis 7+** (Running on port 6379)
*   **Kafka 3.5+** (Running on port 9092)
*   **RabbitMQ** (Optional, if used by legacy services)

---

## 2. Infrastructure Setup (The Hard Part)

### Option A: Hybrid (Recommended)
Run only the "hard" stuff in Docker, and your code locally.
```bash
docker-compose up -d postgres redis kafka zookeeper config-server service-discovery
```

### Option B: Pure Native (100% No Docker)
If you cannot use Docker at all, you must install and start these services manually.

1.  **PostgreSQL**:
    *   Install from [postgresql.org](https://www.postgresql.org/download/).
    *   Start the service: `brew services start postgresql` (Mac) or `sudo systemctl start postgresql` (Linux).
    *   Create Config: User `postgres`, Password `postgres`, DB `logistics`.
    *   Initialize DB: Run `docker/init-db.sql` content in your local DB.

2.  **Redis**:
    *   Install: `brew install redis`.
    *   Start: `redis-server`.

3.  **Kafka & Zookeeper**:
    *   Install: `brew install kafka`.
    *   Start Zookeeper: `zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties`.
    *   Start Kafka: `kafka-server-start /usr/local/etc/kafka/server.properties`.

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
