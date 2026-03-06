# Local Development Setup Guide (Logistic Platform)

This guide explains how to run the Logistics Platform **locally** without Docker for the application.

> **Recommendation**: Use Docker for Infrastructure (Postgres, Kafka, Redis) and run the App natively.

---

## 1. Prerequisites

*   **Java 21+**
*   **Maven 3.8+**
*   **Infrastructure Running**:
    *   PostgreSQL (Port 5432)
    *   Redis (Port 6379)
    *   Kafka (Port 9092)

## 2. Start Infrastructure (Docker Method)

The easiest way to get the database and brokers running:

```bash
docker compose up -d postgres redis kafka zookeeper
```

## 3. Build the Project

```bash
mvn clean install -DskipTests
```

## 4. Run the Application

You only need to run **ONE** application now.

### Option A: Command Line
```bash
cd logistic_platform-app
mvn spring-boot:run
```

### Option B: IDE (IntelliJ / VS Code)
1.  Open the project.
2.  Navigate to `logistic-app/src/main/java/com/logistics/platform/LogisticApplication.java`.
3.  Right-click -> **Run 'LogisticApplication'**.

## 5. Verify

*   **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
*   **API Docs (Swagger)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 6. Profiles

*   `dev` (Default): Connects to localhost infrastructure.
*   `prod`: For production settings.

To run with a specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
