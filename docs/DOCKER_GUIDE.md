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
15. [GitHub Actions CI/CD](#15-github-actions-cicd-build-in-cloud--no-docker-desktop-required)
16. [**Production Deployment (deploy-prod.yml)**](#16-production-deployment-deploy-prodyml-)
17. [**Automation Script (`run-platform.sh`)**](#17-automation-script-run-platformsh-)

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
| `postgres`          | `postgis/postgis:15-3.4-alpine`                     | `5432`            | Primary database (with PostGIS)|
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
docker pull postgis/postgis:15-3.4-alpine
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

### Actuator Health API (`/actuator/health`)

The Spring Boot application exposes a detailed Actuator health check endpoint at `http://localhost:8080/actuator/health`. This API provides a comprehensive overview of the application's internal state, including database connectivity, Kafka stream threads, Elasticsearch cluster status, Redis, disk space, and more. 

---

---

## 15. GitHub Actions CI/CD (Build in Cloud — No Docker Desktop Required)

The project uses **GitHub Actions** to automatically build and push Docker images to
**GitHub Container Registry (GHCR)** on every push to `main`.

This means you can pull a pre-built image from GHCR **without ever running Docker Desktop locally**.

---

### CI/CD Workflow Overview

| Workflow File | Trigger | What It Does |
|---|---|---|
| `ci.yml` | Push to any branch / PR | Builds Maven project, runs tests, uploads reports |
| `docker-build.yml` | Push to `main` / git tags | Builds Docker image, pushes to GHCR, runs Trivy scan |
| `deploy-dev.yml` | Push to `develop` / manual | SSH-deploys to dev server (skips if no server configured) |
| `deploy-prod.yml` | Push of `v*.*.*` tag / manual | **Production deploy** with approval gate, health check, auto-rollback |

---

### GHCR Image — Pull Without Building Locally

After CI runs, the image is available at:

```bash
ghcr.io/sanjeetji/logistics-platform/logistic-app:latest
```

Pull it directly:

```bash
docker pull ghcr.io/sanjeetji/logistics-platform/logistic-app:latest
```

If the package is private, authenticate first with your GitHub Personal Access Token (PAT):

```bash
echo "YOUR_GITHUB_PAT" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
docker pull ghcr.io/sanjeetji/logistics-platform/logistic-app:latest
```

> Generate a PAT at: **GitHub → Settings → Developer settings → Personal access tokens**
> with scope: `read:packages`

---

### Run Locally with GHCR Image (No Build Needed)

Instead of building locally, override the `logistic-app` image in docker-compose:

```bash
# Set the GHCR image as the app image
export LOGISTIC_APP_IMAGE=ghcr.io/sanjeetji/logistics-platform/logistic-app:latest

# Start infra + use the pre-built GHCR image
cd docker
docker compose up -d
```

Or add this override inline:

```bash
docker compose -f docker/docker-compose.yml \
  run -e SPRING_PROFILES_ACTIVE=docker \
  --image ghcr.io/sanjeetji/logistics-platform/logistic-app:latest \
  logistic-app
```

---

### Use Docker WITHOUT Docker Desktop — Install Colima (macOS)

**Colima** is a free, lightweight Docker runtime for macOS that completely replaces Docker Desktop. It is highly recommended for single developers because it saves disk space and allows you to easily reclaim CPU/RAM when you aren't actively developing.

#### 1. Full Installation Setup

```bash
# Install Colima + Docker CLI tools (one time)
brew install colima docker docker-compose

# Add Homebrew to your PATH (if not already there)
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
source ~/.zprofile
```

#### 2. Daily Workflow & Commands

```bash
# Start the Docker VM (allocates 8GB RAM, 4 CPUs)
colima start --memory 8 --cpu 4

# Verify it is running
docker ps

# Start the Logistics Platform
./docker/scripts/run-platform.sh start
```

#### 3. Resource Management (CPU, RAM, and Disk Space)

Colima gives you total control over how resources are used:

| State | Action / Command | Resource Impact |
| --- | --- | --- |
| **Working** | `colima start -m 8 -c 4`<br>`./docker/scripts/run-platform.sh start` | **CPU/RAM:** actively used by Postgres, Kafka, etc.<br>**Disk:** Space used to store databases/images. |
| **After Work** | `./docker/scripts/run-platform.sh stop`<br>`colima stop` | **CPU/RAM:** 100% Freed. The background VM safely shuts down.<br>**Disk:** Data is PRESERVED. You won't lose your local databases. |
| **Full Wipe** | `./docker/scripts/run-platform.sh stop`<br>`colima delete` | **CPU/RAM:** 100% Freed.<br>**Disk:** 100% Freed. The VM and all your databases are destroyed. You will start completely fresh next time you run `colima start`. |

After `colima start`, all standard `docker` and `docker compose` commands work exactly the same as they would with Docker Desktop.

---

### GitHub Actions Secrets Setup

Go to: **GitHub → Your Repo → Settings → Secrets and variables → Actions**

| Secret Name | Required For | Value |
|---|---|---|
| `NVD_API_KEY` | OWASP dependency scan in CI | Get free key at nvd.nist.gov/developers |
| `DEV_SERVER_HOST` | Auto-deploy to dev server | Your server IP or hostname |
| `DEV_SERVER_USER` | Auto-deploy to dev server | SSH user (e.g. `ubuntu`) |
| `DEV_SSH_PRIVATE_KEY` | Auto-deploy to dev server | Contents of your `~/.ssh/id_rsa` |
| `PROD_SERVER_HOST` | Production deployment | Your prod server IP or hostname |
| `PROD_SERVER_USER` | Production deployment | SSH user (e.g. `ubuntu`) |
| `PROD_SSH_PRIVATE_KEY` | Production deployment | Contents of your prod server's private key |

> `GITHUB_TOKEN` is **automatically provided** by GitHub — no setup needed for GHCR push.

---

### View CI Results

- **Actions tab**: `https://github.com/sanjeetji/logistics-platform/actions`
- **GHCR packages**: `https://github.com/sanjeetji/logistics-platform/pkgs/container/logistics-platform%2Flogistic-app`

---

## 16. Production Deployment (`deploy-prod.yml`) 🚀

The `deploy-prod.yml` workflow is the **only path to production**.
It is triggered by a versioned git tag and enforces a **manual approval gate** before any
container is touched on the production server.

---

### Deployment Flow

```
git tag v1.2.0 && git push --tags
        │
        ▼
[Preflight] → Verify image exists in GHCR
        │
        ▼
[Approval Gate] → Reviewer clicks "Approve" in GitHub Actions UI
        │
        ▼
[SSH to Prod Server]
  1. Sync docker-compose files
  2. Save current image tag (for rollback)
  3. Pull new image from GHCR
  4. docker compose up -d --no-deps logistic-app
        │
        ▼
[Health Check] → GET /actuator/health → { "status": "UP" }
        │
     ┌──┴──┐
   Pass   Fail
     │      │
     ▼      ▼
  ✅ Done  ♻️ Auto-Rollback to previous image
```

---

### Step 1 — Set Up GitHub Environment (one time)

1. Go to **GitHub → Repository → Settings → Environments**
2. Click **New environment** → name it `production`
3. Enable **Required reviewers** → add yourself (or your team)
4. **Restrict to**: `main` branch only
5. Add these secrets inside the `production` environment:

| Secret | Value |
|---|---|
| `PROD_SERVER_HOST` | Your prod server IP |
| `PROD_SERVER_USER` | SSH user (e.g. `ubuntu`) |
| `PROD_SSH_PRIVATE_KEY` | Private SSH key `~/.ssh/id_rsa` |

> ⚠️ Production secrets live under **Environments → production**, NOT under the general Actions secrets.

---

### Step 2 — Set Up the Production Server (one time)

SSH into your production server and run:

```bash
# Create the deploy directory
sudo mkdir -p /opt/logistics-platform
sudo chown $USER:$USER /opt/logistics-platform
cd /opt/logistics-platform

# Create your production .env file with real credentials
cat > .env << 'EOF'
PROD_DB_URL=jdbc:postgresql://postgres:5432/logistics_postgres
PROD_DB_USER=logistics_user
PROD_DB_PASSWORD=CHANGE_ME_STRONG_PASSWORD
PROD_REDIS_HOST=redis
PROD_REDIS_PASSWORD=CHANGE_ME_REDIS_PASSWORD
PROD_KAFKA_BROKERS=kafka:29092
PROD_JWT_SECRET=CHANGE_ME_64_CHAR_RANDOM_STRING
PROD_MAIL_HOST=smtp.yourprovider.com
PROD_MAIL_PORT=587
PROD_MAIL_USERNAME=noreply@yourdomain.com
PROD_MAIL_PASSWORD=CHANGE_ME_MAIL_PASSWORD
PROD_AWS_REGION=ap-south-1
PROD_AWS_ACCESS_KEY_ID=YOUR_AWS_KEY
PROD_AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET
EOF
chmod 600 .env

# Start infrastructure services (first time only)
docker compose \
  -f docker/docker-compose.yml \
  -f docker/docker-compose.prod.yml \
  --env-file .env \
  up -d postgres redis kafka zookeeper elasticsearch
```

---

### Step 3 — Release a New Version

```bash
# On your local machine — after merging to main
git checkout main
git pull

# Create a semantic version tag
git tag v1.2.0 -m "Release v1.2.0 — description of changes"
git push origin v1.2.0
```

This will:
1. Trigger `deploy-prod.yml` in GitHub Actions
2. Run a preflight check (verify image exists in GHCR)
3. **Pause for manual approval** — go to Actions → the running workflow → click **Review deployments** → **Approve**
4. SSH into prod, pull the new image, restart the container
5. Run health checks and auto-rollback if they fail

---

### Step 4 — Monitor the Deployment

```bash
# Watch actions in real-time
https://github.com/sanjeetji/logistics-platform/actions

# On prod server — follow app logs during deploy
ssh ubuntu@YOUR_PROD_IP
docker logs -f logistics-app

# Check health manually
curl http://YOUR_PROD_IP:8080/actuator/health
```

---

### Manual Emergency Deploy (any tag)

Go to **Actions → Deploy to Production → Run workflow** and enter any image tag (e.g. `v1.1.0` for a rollback to a known-good version).

```bash
# Or trigger via GitHub CLI
gh workflow run deploy-prod.yml --field image_tag=v1.1.0
```

---

### Production Rollback

The workflow **auto-rolls back** if the health check fails. For a manual rollback:

```bash
# Option 1 — Tag an older commit and push
git tag v1.1.1 <commit-sha>
git push origin v1.1.1
# Then approve the deployment in GitHub Actions

# Option 2 — Manual rollback on server
ssh ubuntu@YOUR_PROD_IP
cd /opt/logistics-platform
export LOGISTIC_APP_IMAGE=ghcr.io/sanjeetji/logistics-platform/logistic-app:v1.1.0
docker compose \
  -f docker/docker-compose.yml \
  -f docker/docker-compose.prod.yml \
  up -d --no-deps --pull never logistic-app
```

---

### Production Secrets Checklist

Before your first production release, confirm ALL of these are set:

- [ ] `PROD_SERVER_HOST` — in GitHub Environment `production` secrets
- [ ] `PROD_SERVER_USER` — in GitHub Environment `production` secrets
- [ ] `PROD_SSH_PRIVATE_KEY` — in GitHub Environment `production` secrets
- [ ] `.env` file on server with all `PROD_*` variables
- [ ] SSL certificates in `docker/nginx/ssl/` on prod server
- [ ] GitHub Environment `production` has required reviewers set
- [ ] At least one Docker image tagged `v*.*.*` exists in GHCR

---

## 17. Automation Script (`run-platform.sh`) 🛠️

The platform includes a powerful automation script located at `./docker/scripts/run-platform.sh` to simplify your daily workflow and handle common environment issues automatically.

### Commands Reference

| Command | Usage | Description |
|---|---|---|
| `start` | `./docker/scripts/run-platform.sh start` | Quick start. Keeps data, performs pre-flight checks, and **self-heals** Kafka. |
| `fresh` | `./docker/scripts/run-platform.sh fresh` | Full reset. Wipes all data, rebuilds app image, and starts everything. |
| `stop` | `./docker/scripts/run-platform.sh stop` | Gracefully stops all platform containers. |
| `restart`| `./docker/scripts/run-platform.sh restart`| Performs a `stop` followed by a `start`. |
| `doctor` | `./docker/scripts/run-platform.sh doctor` | **Diagnostics**. Checks resources, port conflicts, and service health. |
| `logs` | `./docker/scripts/run-platform.sh logs` | Tails logs from all platform containers. |
| `build` | `./docker/scripts/run-platform.sh build` | Maven build + Docker image rebuild (no start). |

### 🛠️ Self-Healing Automation
The `start` command is now "intelligent." It recognizes common failures:
- **Kafka Cluster ID Mismatch**: If detected, the script automatically clears stale volumes and resets Kafka/Zookeeper for you.
- **Resource Validation**: Warns you if Colima is running with insufficient CPU or RAM.

### 🏥 Platform Doctor
If things feel "stuck," run the doctor:
```bash
./docker/scripts/run-platform.sh doctor
```
It will provide a clear report on Docker status, port usage, and infrastructure health, suggesting specific fixes for any issues found.

---

> 💡 **Tip**: Run `docker system df` regularly to monitor disk usage.
> The build cache grows fast — prune it with `docker builder prune -f` when not needed.

---

> 💡 **Tip**: Use `gh workflow run deploy-prod.yml` (GitHub CLI) to trigger deployments from your terminal without opening a browser.
