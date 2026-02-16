# Docker Deployment Guide

This guide explains how to run the Logistics Platform using Docker.

## 1. Prerequisites
*   Docker & Docker Compose installed.
*   4GB+ RAM for Infrastructure only.
*   16GB+ RAM if running multiple services.

## 2. File Structure
*   `docker-compose.yml`: **Infrastructure Config** (Postgres, Kafka, Redis, Config Server, Registry, Gateway).
*   `docker/docker-compose-services.yml`: **Backend Services** (Auth, User, Order, etc.).

## 1. Modular Docker Configuration

We have split the configuration into modular files so you can run exactly what you need.

| File | Module | Content |
| :--- | :--- | :--- |
| **`docker-compose.yml`** | **Infrastructure** | Postgres, Kafka, Redis, Eureka, Gateway, PgAdmin. (Always Run This) |
| **`docker/docker-compose-core.yml`** | **Core Platform** | Shared services (`auth`, `user`, `notification`, `payment`, etc.). |
| **`docker/docker-compose-services.yml`** | **Legacy / Backup** | **All 50+ Services in one file**. Use this if you want a single file for everything. |

---

## 2. How to Run the Platform

### Scenario A: "I am Developing Code" (Recommended)
Run only the infrastructure in Docker, and run your specific service in your IDE.

1.  **Start Infrastructure:**
    ```bash
    docker-compose up -d
    ```

### Scenario B: "I want to work on B2B"
Run Infrastructure + Core + B2B Services.

```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2b.yml up -d --build
```

### Scenario C: "I want to work on B2C"
Run Infrastructure + Core + B2C Services.

```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2c.yml up -d --build
```

### Scenario D: "Run EVERYTHING"
**Option 1: Modular Approach (Recommended)**
```bash
docker-compose -f docker-compose.yml \
               -f docker/docker-compose-core.yml \
               -f docker/docker-compose-b2b.yml \
               -f docker/docker-compose-b2c.yml up -d --build
```

**Option 2: All-In-One File**
```bash
docker-compose -f docker-compose.yml -f docker/docker-compose-services.yml up -d --build
```

---

## 3. FAQ: "Is my latest code running?"

If you change your Java code, **Docker will NOT know about it** unless you rebuild.

### The Golden Rule: Always use `--build`
When starting services, add the `--build` flag to force Docker to recompile your JAR and create a new image.

*   **Wrong (Uses old code):** `docker-compose up -d`
*   **Correct (Compiles new code):** `docker-compose up -d --build`

### How to Verify?
Check if the image was created recently:
```bash
docker images | grep "logistics"
```
*Look at the "CREATED" column. It should say "About a minute ago" if it just rebuilt.*

To remove unused containers and free up disk space/RAM:

1.  **Stop everything:**
    ```bash
    docker-compose down
    ```

2.  **Remove Stopped Containers (Cleanup):**
    ```bash
    docker container prune -f
    ```
    *This removes all stopped containers, keeping only the running ones.*

3.  **Remove Unused Images (Deep Clean):**
    ```bash
    docker image prune -a -f
    ```
    *Warning: This deletes all images not currently used by a running container. You will have to re-download/re-build them next time.*

---

## 3. Useful Access Points

| Service | URL / Port | Credentials |
| :--- | :--- | :--- |
| **App Gateway** | `http://localhost:8080` | - |
| **Service Registry (Eureka)** | `http://localhost:8761` | - |
| **Config Server** | `http://localhost:8888` | - |
| **PostgreSQL DB** | `localhost:5432` | `postgres` / `postgres` |
| **PgAdmin (DB GUI)** | `http://localhost:5050` | `admin@logistics.com` / `admin` |
| **RabbitMQ / Kafka** | `localhost:5672` / `9092` | - |

---

## 4. Managing the Environment

## 4. Docker Profiles (Advanced)
You can modify `docker-compose.yml` to run groups of services.

## 5. Troubleshooting
*   **"OOMKilled"**: Authorization failed? No, this means "Out of Memory". Increase Docker Desktop RAM limit to 8GB+.
*   **"Connection Refused"**: Ensure `infrastructure` services (Config Server, Eureka) are healthy before starting business services.
