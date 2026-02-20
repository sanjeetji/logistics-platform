# 🐳 Docker Operations Guide (Logistic Platform Edition)

This guide covers how to run the **Logistics Platform Logistic Platform** using Docker.

---

## 1. Prerequisites & Setup

*   **Docker Desktop**: Installed and running.
*   **Disk Space**: 10GB+ free.
*   **Ports**: Ensure 8080 (App), 5432 (DB), 6379 (Redis), 9092 (Kafka) are free.

### Included Infrastructure
*   **Database**: PostgreSQL 16
*   **Cache**: Redis 7
*   **Messaging**: Kafka + Zookeeper
*   **Object Storage**: MinIO

---

## 2. Quick Start

### 🟢 Run Everything (App + Infra)
This gets you a fully working environment.

```bash
./docker/scripts/run-platform.sh start
```
*   **App URL**: http://localhost:8080
*   **PgAdmin**: http://localhost:5050
*   **MinIO**: http://localhost:9001

### 🔴 Stop Everything
```bash
./docker/scripts/run-platform.sh stop
```

---

## 3. Development Mode (Hybrid)

If you want to run the **Java App in your IDE** (for debugging) but keep the **Database/Kafka in Docker**:

1.  **Start Infrastructure Only**:
    ```bash
    docker compose up -d postgres redis kafka zookeeper minio
    ```

2.  **Run Keys**:
    *   Postgres: `localhost:5432` (User: `logistics_user`, Pass: `logistics_pass`)
    *   Redis: `localhost:6379`
    *   Kafka: `localhost:9092`

3.  **Run App in IDE**:
    Start `LogisticApplication.java`. It will connect to the exposed Docker ports.

---

## 4. Maintenance Commands

| Action | Command | Description |
| :--- | :--- | :--- |
| **Build Project** | `./docker/scripts/run-platform.sh build` | Recompile & rebuild Docker image. |
| **Check Status** | `./docker/scripts/check-status.sh` | See what's running. |
| **View Logs** | `./docker/scripts/run-platform.sh logs` | See all logs. |
| **Clean All** | `./docker/scripts/clean.sh` | **WARNING**: Deletes all data. |

---

---

## 6. Docker Image Management

To keep your development environment clean and efficient, use the following commands.

### 📋 Identify Required Images
To see only the images currently required and used by the Logistics Platform:
```bash
docker ps --format "table {{.Image}}\t{{.Names}}\t{{.Status}}"
```
*This identifies exactly which images are powering your running containers.*

### 🗑️ Cleanup Unused Images
To safely delete all images that are not being used by any container (dangling layers + unused build images):
```bash
docker image prune -a
```
*This will reclaim significant disk space by removing old build-time images like Maven or base JRE layers.*

### 🛠️ Identifying "Required" vs "Unused"
*   **Required**: Images like `postgis`, `redis`, `kafka`, and `docker-logistic-app`. These are specified in the `docker-compose.yml`.
*   **Unused**: Any image with a `<none>` tag or old base images (like `maven` or `eclipse-temurin`) that were only needed during the build process.
