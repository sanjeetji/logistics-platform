# 🐳 Total Docker Operations Guide

This is the definitive guide for managing the Logistics Platform environment. It covers everything from local development to full platform orchestration.

---

## 1. Prerequisites & Setup
*   **Docker Desktop**: Running with **16GB+ RAM** allocated (Settings -> Resources).
*   **Disk Space**: 20GB+ free.
*   **Infrastructure Build**: Run this **once** or whenever you change infrastructure code:
    ```bash
    mvn clean package -DskipTests -pl infrastructure/gateway-service,infrastructure/service-discovery,infrastructure/config-server
    ```

---

## 2. Deployment Scenarios

### 🟢 Scenario A: Infrastructure Only (For Service Developers)
*Use this if you want to run one service in your IDE but need the DB, Message Broker, and Registry in Docker.*
```bash
docker-compose up -d
```
*Services included: PostgreSQL, Redis, Kafka, Zookeeper, Eureka, Config Server, Gateway, PgAdmin.*

### 🟠 Scenario B: Infrastructure + One Specific Service
*Use this to test how a single service interacts with the shared environment.*
```bash
# Example: Running only the Auth Service
docker-compose -f docker-compose.yml -f docker/docker-compose-core.yml up -d --build auth-service
```

### 🔵 Scenario C: Engine-Level Focus (B2B or B2C)
*Use this to work on a specific business vertical.*

**B2B Engine (Infra + Core + B2B):**
```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2b.yml up -d --build
```

**B2C Engine (Infra + Core + B2C):**
```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2c.yml up -d --build
```

### 🔴 Scenario D: Full Platform (All Services)
*Use this for integration testing or complete system demo. Requires significant RAM.*
```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2b.yml \
               -f docker/docker-compose-b2c.yml up -d --build
```

---

## 3. Maintenance & Lifecycle

| Action | Command | Description |
| :--- | :--- | :--- |
| **Stop** | `docker-compose stop` | Pauses containers (data safe). |
| **Shut Down** | `docker-compose down` | Removes containers/networks (data safe). |
| **Hard Reset** | `docker-compose down -v` | **Deletes all DB data** and volumes. |
| **Update Code** | `docker-compose up -d --build <service>` | Rebuilds and restarts 1 specific service. |
| **Check Logs** | `docker logs -f <container_name>` | Follow real-time logs for a service. |
| **Deep Clean** | `docker system prune -a -f` | Deletes all unused images and build cache. |

---

## 4. Port & Credential Map

| Component | URL / Port | Credentials (User/Pass) |
| :--- | :--- | :--- |
| **API Gateway** | `http://localhost:8080` | - |
| **Service Registry** | `http://localhost:8761` | - |
| **Config Server** | `http://localhost:8888` | `admin` / `admin_password` |
| **PostgreSQL** | `localhost:5432` | `logistics_user` / `logistics_pass` |
| **PgAdmin (GUI)** | `http://localhost:5050` | `admin@logistics.com` / `admin` |
| **Redis** | `localhost:6379` | `redispass` |
| **Kafka UI** | `http://localhost:9021` | - |

---

## 5. Troubleshooting Checklist

1.  **"Database connection failed"**:
    - Check if `logistics-postgres` is running: `docker ps`.
    - Ensure your service points to host `postgres` (not `localhost`) when inside Docker.
2.  **"Eureka registration taking too long"**:
    - This is normal on first start. Wait for `logistics-eureka` to show `UP` (healthy).
3.  **"Out of Memory"**:
    - Check your Docker Desktop RAM. If it hits 100%, services will crash silently.
4.  **"Changes not reflected"**:
    - Did you forget the `--build` flag? Did you run `mvn package` before building the Docker image?
