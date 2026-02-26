# 🐳 Docker Guide — Logistics Platform

A complete reference for managing Docker in the **Logistics Platform** project:
build, run, monitor, clean, and troubleshoot everything in one place.

---

## 📑 Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Project Docker Architecture](#2-project-docker-architecture)
3. [Quick Start](#3-quick-start)
4. [Build Commands](#4-build-commands)
5. [Run / Stop / Restart Commands](#5-run--stop--restart-commands)
6. [Logs & Monitoring](#6-logs--monitoring)
7. [Disk Usage — `docker system df`](#7-disk-usage--docker-system-df)
8. [Cleaning Docker (Step-by-Step)](#8-cleaning-docker-step-by-step)
9. [Image Management](#9-image-management)
10. [Volume Management](#10-volume-management)
11. [Network Management](#11-network-management)
12. [Development Mode (Hybrid)](#12-development-mode-hybrid)
13. [Useful One-Liners](#13-useful-one-liners)
14. [Port Reference](#14-port-reference)

---

## 1. Prerequisites

| Requirement    | Details                                    |
|----------------|--------------------------------------------|
| Docker Desktop | Installed and running (macOS)              |
| Free Disk      | At least **10 GB** recommended             |
| Free RAM       | At least **8 GB** recommended              |
| Free Ports     | See [Port Reference](#14-port-reference)   |
| Maven          | `mvn` available (for building the JAR)     |

---

## 2. Project Docker Architecture

### Services defined in `docker/docker-compose.yml`

| Service             | Image                                               | Port(s)           | Purpose                        |
|---------------------|-----------------------------------------------------|-------------------|--------------------------------|
| `postgres`          | `postgis/postgis:15-3.3-alpine`                     | `5432`            | Primary database (with PostGIS)|
| `pgadmin`           | `dpage/pgadmin4:latest`                             | `5050`            | DB admin UI                    |
| `zookeeper`         | `confluentinc/cp-zookeeper:7.5.0`                   | `2181`            | Kafka coordinator              |
| `kafka`             | `confluentinc/cp-kafka:7.5.0`                       | `9092`, `29092`   | Event streaming                |
| `redis`             | `redis:7-alpine`                                    | `6379`            | Cache & session store          |
| `minio`             | `minio/minio:latest`                                | `9000`, `9001`    | Object / file storage          |
| `mailhog`           | `mailhog/mailhog:latest`                            | `1025`, `8025`    | Local SMTP + email UI          |
| `elasticsearch`     | `docker.elastic.co/elasticsearch/elasticsearch:8.12.0` | `9200`         | Search engine                  |
| `logistic-app`      | Built from `logistic-app/Dockerfile`                | `8080`            | Spring Boot application        |

### Custom Dockerfiles

| File                        | Base Image              | Description                    |
|-----------------------------|-------------------------|--------------------------------|
| `logistic-app/Dockerfile`   | `eclipse-temurin:21-jre`| Main Spring Boot app image     |
| `ml-service/Dockerfile`     | `python:3.11-slim`      | Python ML service              |
| `docker/images/Dockerfile.common` | —                 | Shared base layer              |

---

## 3. Quick Start

### ▶️ Build JAR first (required before first Docker run)

```bash
# From project root
mvn clean package -DskipTests
```

### ▶️ Start All Services (infra + app)

```bash
cd docker
docker compose up -d
```

### ▶️ Start Infrastructure Only (for IDE development)

```bash
cd docker
docker compose up -d postgres redis kafka zookeeper minio elasticsearch mailhog pgadmin
```

### ⏹️ Stop All Services

```bash
cd docker
docker compose down
```

### ⏹️ Stop and Remove Volumes (⚠️ deletes all data)

```bash
cd docker
docker compose down -v
```

---

## 4. Build Commands

### Build the Spring Boot app image only

```bash
# From project root
docker build -f logistic-app/Dockerfile -t logistics-platform/logistic-app:latest .
```

### Build the ML service image only

```bash
# From project root
docker build -f ml-service/Dockerfile -t logistics-platform/ml-service:latest ./ml-service
```

### Rebuild images (no cache) via docker compose

```bash
cd docker
docker compose build --no-cache
```

### Rebuild a single service image

```bash
cd docker
docker compose build --no-cache logistic-app
```

---

## 5. Run / Stop / Restart Commands

```bash
# Start all services in background
docker compose up -d

# Start a specific service
docker compose up -d postgres

# Stop all running services
docker compose stop

# Stop a specific service
docker compose stop kafka

# Restart a specific service
docker compose restart logistic-app

# Remove stopped containers (keeps volumes)
docker compose down

# Remove stopped containers + volumes
docker compose down -v

# Remove stopped containers + volumes + images built by compose
docker compose down -v --rmi all
```

---

## 6. Logs & Monitoring

```bash
# View logs of all services
docker compose logs

# Follow (tail) logs of all services
docker compose logs -f

# Follow logs of a specific service
docker compose logs -f logistic-app

# Last 100 lines of a specific service
docker compose logs --tail=100 kafka

# List all running containers with status
docker ps

# List all containers (including stopped)
docker ps -a

# Show resource usage (CPU, memory, network, I/O)
docker stats

# Show resource usage (snapshot, no stream)
docker stats --no-stream

# Inspect a container's configuration
docker inspect logistics-app
```

---

## 7. Disk Usage — `docker system df`

This is the **most important command** to understand how much space Docker is consuming on your machine.

```bash
docker system df
```

### Sample Output

```
TYPE            TOTAL     ACTIVE    SIZE      RECLAIMABLE
Images          12        5         13.72GB   8.5GB (62%)
Containers      8         8         120MB     0B (0%)
Local Volumes   6         6         4.539GB   0B (0%)
Build Cache     75        0         22.6GB    22.6GB
```

### Column Meanings

| Column        | Meaning                                                    |
|---------------|------------------------------------------------------------|
| `TOTAL`       | Total number of objects                                    |
| `ACTIVE`      | Objects currently used by running containers               |
| `SIZE`        | Total disk space consumed                                  |
| `RECLAIMABLE` | Space that can be freed by pruning unused objects          |

### Verbose breakdown (per image/container)

```bash
docker system df -v
```

---

## 8. Cleaning Docker (Step-by-Step)

> **Follow this order** to safely clean Docker and then restore only the project-required images.

---

### Step 1 — Check what you have (before cleaning)

```bash
docker system df
docker images
docker ps -a
docker volume ls
```

---

### Step 2 — Stop all running containers

```bash
docker stop $(docker ps -q)
```

---

### Step 3 — Remove all stopped containers

```bash
docker container prune -f
```

---

### Step 4 — Remove all unused images

```bash
# Remove dangling images (untagged <none>:<none>)
docker image prune -f

# Remove ALL unused images (not referenced by any container) — recommended
docker image prune -a -f
```

---

### Step 5 — Remove unused volumes

```bash
# Safe: removes only volumes not attached to any container
docker volume prune -f

# Force remove ALL volumes (including orphaned named volumes missed by prune)
docker volume rm $(docker volume ls -q)
```

> ⚠️ **WARNING**: This deletes all database data stored in volumes.
> Use `docker volume rm $(docker volume ls -q)` when `docker volume prune` still shows leftover space.

---

### Step 6 — Remove unused networks

```bash
docker network prune -f
```

---

### Step 7 — Remove build cache

```bash
docker builder prune -f

# Remove ALL build cache (including cache that could speed up future builds)
docker builder prune -a -f
```

---

### ☢️ Nuclear Option — Clean EVERYTHING at once

This single command removes **all** stopped containers, unused networks, dangling images, and build cache.

```bash
docker system prune -f
```

To also remove **all unused images** and **volumes**:

```bash
docker system prune -a -f --volumes
```

> ⚠️ **CAUTION**: This will delete all Docker data that is not attached to a running container.
> You will need to re-pull all images and re-build your project images afterward.

---

### Step 8 — Verify cleanup

```bash
docker system df
docker images
docker ps -a
docker volume ls
```

Expected output after full clean:
```
TYPE            TOTAL     ACTIVE    SIZE      RECLAIMABLE
Images          0         0         0B        0B
Containers      0         0         0B        0B
Local Volumes   0         0         0B        0B
Build Cache     0         0         0B        0B
```

### ⚠️ Docker Desktop Internal Cache (macOS)

After a full clean you may still see something like:
```
Images     0    0    768.1MB    768.1MB (100%)
```

This is **normal on macOS**. Docker Desktop runs inside a Linux VM and reserves a fixed portion of disk for its own system images (`docker/desktop-*` base layers). These **cannot be deleted** via `docker rmi` and are not your project images.

```bash
# Confirm no user images exist (output will be empty)
docker images -a

# If truly nothing shows, the 768MB is Docker Desktop's own VM overhead — safe to ignore
```

The only way to reclaim this space is to **uninstall and reinstall Docker Desktop**, but it is not necessary for normal development.

---

### Step 9 — Restore only project images

After cleaning, pull and build only what this project needs:

```bash
# Step 1: Build the Spring Boot JAR
mvn clean package -DskipTests

# Step 2: Pull infrastructure images + build app image
cd docker
docker compose pull          # pulls postgres, redis, kafka, etc.
docker compose build         # builds logistic-app from Dockerfile

# Step 3: Start everything
docker compose up -d
```

---

## 9. Image Management

```bash
# List all images
docker images

# List images with full details
docker images -a

# List only image IDs
docker images -q

# Remove a specific image by name
docker rmi logistics-platform/logistic-app:latest

# Remove a specific image by ID
docker rmi <IMAGE_ID>

# Force remove an image (even if used by stopped containers)
docker rmi -f <IMAGE_ID>

# Remove all images (⚠️ nuclear)
docker rmi -f $(docker images -q)

# Pull a specific project-related image
docker pull postgis/postgis:15-3.3-alpine
docker pull confluentinc/cp-kafka:7.5.0
docker pull redis:7-alpine
docker pull minio/minio:latest
docker pull docker.elastic.co/elasticsearch/elasticsearch:8.12.0
docker pull mailhog/mailhog:latest
docker pull dpage/pgadmin4:latest

# Tag the app image for a registry (e.g., Docker Hub)
docker tag logistics-platform/logistic-app:latest your-dockerhub-user/logistic-app:1.0.0

# Push to registry
docker push your-dockerhub-user/logistic-app:1.0.0
```

---

## 10. Volume Management

```bash
# List all volumes
docker volume ls

# Inspect a specific volume
docker volume inspect logistics-platform_postgres-data

# Remove a specific volume (⚠️ deletes data)
docker volume rm logistics-platform_postgres-data

# Remove all unused volumes
docker volume prune -f

# Remove ALL volumes (⚠️ nuclear — deletes all DB data)
docker volume rm $(docker volume ls -q)
```

### Project Volumes

| Volume Name               | Used By      | Contains              |
|---------------------------|--------------|----------------------|
| `postgres-data`           | postgres     | All database data     |
| `redis-data`              | redis        | Cache / sessions      |
| `kafka-data`              | kafka        | Event log data        |
| `minio-data`              | minio        | Uploaded files/assets |
| `elasticsearch-data`      | elasticsearch| Search index data     |
| `pgadmin-data`            | pgadmin      | PgAdmin settings      |

---

## 11. Network Management

```bash
# List all Docker networks
docker network ls

# Inspect the project network
docker network inspect logistics-network

# Remove unused networks
docker network prune -f
```

The project uses a single bridge network: **`logistics-network`**. All services communicate internally using their service names as hostnames (e.g., `postgres`, `kafka`, `redis`).

---

## 12. Development Mode (Hybrid)

Run **infrastructure in Docker** + **Spring Boot app in your IDE** (for fast iteration and debugging):

### Step 1 — Start only the infrastructure services

```bash
cd docker
docker compose up -d postgres redis kafka zookeeper minio elasticsearch mailhog pgadmin
```

### Step 2 — Run Spring Boot app locally

Start `LogisticApplication.java` in IntelliJ / your IDE with profile `dev`.

The app will connect to:

| Service       | Host        | Port   | Credentials                        |
|---------------|-------------|--------|------------------------------------|
| PostgreSQL    | `localhost` | `5432` | user: `logistics_user` / `logistics_pass` |
| Redis         | `localhost` | `6379` | no auth                            |
| Kafka         | `localhost` | `9092` | no auth                            |
| MinIO         | `localhost` | `9000` | `minioadmin` / `minioadmin`        |
| Elasticsearch | `localhost` | `9200` | no auth                            |
| MailHog (UI)  | `localhost` | `8025` | no auth                            |

### Step 3 — Enable remote debugging (optional)

In `docker-compose.dev.yml`, the app runs with JDWP on port `5005`.  
Add a Remote JVM Debug run config in IntelliJ pointing to `localhost:5005`.

---

## 13. Useful One-Liners

```bash
# See which images are used by running containers
docker ps --format "table {{.Image}}\t{{.Names}}\t{{.Status}}"

# Enter a running container shell
docker exec -it logistics-app bash
docker exec -it logistics-postgres psql -U logistics_user -d logistics_postgres

# Connect to Redis CLI
docker exec -it logistics-redis redis-cli

# See real-time resource usage (CPU/RAM per container)
docker stats --no-stream

# Check health status of all containers
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Copy a file from a container to your host
docker cp logistics-app:/app.jar ./app-backup.jar

# Export a Docker volume as a tar backup
docker run --rm -v logistics-platform_postgres-data:/data -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup.tar.gz /data

# Kill all running containers
docker kill $(docker ps -q)

# Remove all stopped containers
docker rm $(docker ps -aq)
```

---

## 14. Port Reference

| Port   | Service       | URL / Notes                          |
|--------|---------------|--------------------------------------|
| `8080` | Logistic App  | http://localhost:8080                |
| `8080/swagger-ui` | API Docs | http://localhost:8080/swagger-ui.html |
| `5432` | PostgreSQL    | Connect via any SQL client           |
| `5050` | PgAdmin       | http://localhost:5050                |
| `6379` | Redis         | `redis-cli -h localhost -p 6379`     |
| `9092` | Kafka         | Bootstrap server for producers/consumers |
| `9200` | Elasticsearch | http://localhost:9200                |
| `9000` | MinIO API     | S3-compatible endpoint               |
| `9001` | MinIO Console | http://localhost:9001                |
| `1025` | MailHog SMTP  | SMTP server for local email          |
| `8025` | MailHog UI    | http://localhost:8025                |
| `8092` | ML Service    | http://localhost:8092/health         |

---

> 💡 **Tip**: Run `docker system df` regularly to monitor disk usage.
> The build cache grows fast — prune it with `docker builder prune -f` when not needed.
