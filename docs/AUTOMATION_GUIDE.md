# 🚀 Automation Guide — Logistics Platform

**Complete reference for all automation, from local development to production deployment.**  
This is the single source of truth for how the platform is built, run, tested and deployed.

---

## 📑 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Local Development — Quick Start](#2-local-development--quick-start)
3. [Daily Developer Workflow](#3-daily-developer-workflow)
4. [Scripts Reference](#4-scripts-reference)
5. [CI/CD Pipeline (GitHub Actions)](#5-cicd-pipeline-github-actions)
6. [Docker Image Management (GHCR)](#6-docker-image-management-ghcr)
7. [Production Deployment](#7-production-deployment)
8. [Rollback Procedures](#8-rollback-procedures)
9. [Database Management](#9-database-management)
10. [Monitoring & Health Checks](#10-monitoring--health-checks)
11. [Troubleshooting](#11-troubleshooting)
12. [Secrets & Environment Variables Reference](#12-secrets--environment-variables-reference)

---

## 1. Project Overview

### What This Platform Is
A **Spring Boot monolith** (`logistic-app`) with a full infrastructure stack, managed with Docker Compose and automated with GitHub Actions.

### Tech Stack

| Layer | Technology |
|---|---|
| Application | Spring Boot 3.x, Java 21 |
| Database | PostgreSQL 15 (PostGIS) |
| Cache | Redis 7 |
| Messaging | Apache Kafka 7.5 |
| Search | Elasticsearch 8.12 |
| Object Storage | MinIO |
| Email (Dev) | MailHog |
| Reverse Proxy | Nginx |
| Container Registry | GitHub Container Registry (GHCR) |
| CI/CD | GitHub Actions |

### Repository Structure (Relevant to Automation)

```
logistics-platform/
├── .github/
│   └── workflows/
│       ├── ci.yml              ← Build + test on every push/PR
│       ├── docker-build.yml    ← Build & push Docker image to GHCR
│       ├── deploy-dev.yml      ← Auto-deploy to dev server
│       └── deploy-prod.yml     ← Production deployment (manual approval)
├── docker/
│   ├── docker-compose.yml      ← Base compose (all services)
│   ├── docker-compose.dev.yml  ← Dev overrides (remote debug on :5005)
│   ├── docker-compose.prod.yml ← Prod overrides (image from GHCR, resource limits)
│   ├── docker-compose.ci.yml   ← CI-only infra (ephemeral, RAM-backed)
│   ├── scripts/                ← Local automation scripts
│   └── nginx/nginx.conf        ← Reverse proxy config
├── logistic-app/
│   └── Dockerfile              ← App container image
└── docs/
    ├── AUTOMATION_GUIDE.md     ← 📍 You are here
    └── DOCKER_GUIDE.md         ← Docker deep-dive reference
```

---

## 2. Local Development — Quick Start

### Prerequisites

```bash
# Required tools
java --version        # Must be Java 21
mvn --version         # Maven 3.9+
docker --version      # Docker 24+ or Colima
docker compose version # Docker Compose v2

# Optional but recommended
brew install jq       # For integration test scripts
brew install gh       # GitHub CLI (for workflow triggers)
```

> **No Docker Desktop?** Use [Colima](https://github.com/abiosoft/colima) — free, lightweight, saves ~4 GB:
> ```bash
> brew install colima docker docker-compose
> colima start --memory 8 --cpu 4
> ```

### First-Time Setup

```bash
# 1. Clone the project
git clone https://github.com/sanjeetji/logistics-platform.git
cd logistics-platform

# 2. Build the Java project (required before first Docker run)
mvn clean package -DskipTests

# 3. Start everything
./docker/scripts/run-platform.sh start

# 4. Verify everything is healthy
./docker/scripts/health-check.sh
```

**Access Points After Startup:**

| Service | URL | Credentials |
|---|---|---|
| **API** | http://localhost:8080 | Bearer token (from auth) |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | — |
| **Health** | http://localhost:8080/actuator/health | — |
| pgAdmin | http://localhost:5050 | admin@logistics.com / admin123 |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin |
| MailHog UI | http://localhost:8025 | — |

---

## 3. Daily Developer Workflow

### Scenario 1 — Daily Startup (Normal, Data Preserved)

```bash
# After rebooting your Mac or stopping Docker:
./docker/scripts/run-platform.sh start
```

This does **not** rebuild anything. Your database data is preserved from the last session.

### Scenario 2 — After a Schema / Entity Change

```bash
# Wipes all data volumes, rebuilds the JAR + Docker image, starts fresh:
./docker/scripts/run-platform.sh fresh
```

Use this when:
- You added/changed a JPA entity
- You need a clean database state
- Something is stuck and you want a guaranteed clean slate

### Scenario 3 — Code Change (App Only, Infra Running)

```bash
# Re-compile + rebuild just the app image, then restart it:
./docker/scripts/run-platform.sh build
./docker/scripts/run-platform.sh restart
```

### Scenario 4 — IDE Says "Cannot Resolve Symbol"

```bash
# Regenerates all Lombok / MapStruct sources so your IDE can find them:
./docker/scripts/fix_ide_errors.sh

# Then in IntelliJ: File → Invalidate Caches / Restart → Just Restart
# In VS Code: Cmd+Shift+P → "Developer: Reload Window"
```

### Scenario 5 — Run App in IDE, Docker for Infra Only

```bash
# Start only infrastructure (no app container):
cd docker
docker compose up -d postgres redis kafka zookeeper minio elasticsearch mailhog pgadmin

# Then run LogisticApplication.java in IntelliJ with profile: dev
# The app will connect to Docker services on localhost
```

---

## 4. Scripts Reference

All scripts live in `docker/scripts/` and can be run from the project root.

### `run-platform.sh` — Master Control Script

```bash
./docker/scripts/run-platform.sh [command]
```

| Command | What It Does | When to Use |
|---|---|---|
| `start` | Quick start, data preserved, no rebuild | Every day, after reboot |
| `fresh` | Full reset: wipe data + rebuild image | After schema changes |
| `stop` | Stop all containers, keep volumes | End of workday |
| `restart` | `stop` + `start` | When containers act weird |
| `build` | Maven build + Docker image rebuild only | After code changes (no restart) |
| `logs` | Tail all container logs | Debugging |

### `health-check.sh` — Service Health Status

```bash
./docker/scripts/health-check.sh
```

Checks: App (Actuator), PostgreSQL, Kafka, Redis, Elasticsearch, MinIO, pgAdmin.

### `clean.sh` — Nuclear Docker Cleanup

```bash
./docker/scripts/clean.sh
```

Removes all containers, images, and volumes for this project. Use when Docker is in a bad state.

### `postgres-backup.sh` — Database Backup

```bash
./docker/scripts/postgres-backup.sh
# Creates: backups/postgres/logistics_backup_YYYYMMDD_HHMMSS.sql.gz
```

### `postgres-restore.sh` — Database Restore

```bash
./docker/scripts/postgres-restore.sh backups/postgres/logistics_backup_20260226_120000.sql.gz
# ⚠️ Will prompt for confirmation — this overwrites the current database
```

### `postgres-connect.sh` — psql Shell

```bash
./docker/scripts/postgres-connect.sh                  # logistics_postgres (default)
./docker/scripts/postgres-connect.sh other_database   # specific database
```

### `run-integration-test.sh` — API Integration Test

```bash
# Requires: platform running + jq installed
./docker/scripts/run-integration-test.sh

# Tests: Auth login → Create Driver → Create Order → Verify Dispatch Assignment
```

### `create_test_dbs.sh` — Create Test Databases

```bash
./docker/scripts/create_test_dbs.sh
# Creates all service-specific test databases inside the running postgres container.
# No host PostgreSQL tools required — uses docker exec internally.
```

### `postgres-start.sh` — Start Postgres Only (no full infra)

```bash
./docker/scripts/postgres-start.sh
# Starts only PostgreSQL + pgAdmin, without Kafka/Redis/Elasticsearch.
# Useful when you only need a database for quick SQL work.
```

---

## 5. CI/CD Pipeline (GitHub Actions)

> 📘 **Workflow Strategy:** We use a Pull Request-based workflow. See the [**Git & GitHub Workflow Guide**](GIT_WORKFLOW_GUIDE.md) for how to create branches, test locally, and merge PRs.

### Workflow Overview

```
Code Push / PR
      │
      ▼
┌─────────────────────────────────────────────┐
│  ci.yml — Runs on EVERY push to any branch │
│                                             │
│  Job 1: Build (mvn clean package -skip)    │
│  Job 2: Tests (mvn test)                   │
│           ├── JaCoCo coverage report       │
│           └── Test report artifacts        │
│  Job 3: Trigger Docker Build (main only)   │
└─────────────────────────────────────────────┘
                     │ (only on push to main or v* tag)
                     ▼
┌─────────────────────────────────────────────┐
│  docker-build.yml                           │
│                                             │
│  1. Build JAR                               │
│  2. Build Docker image (Buildx, GHA cache) │
│  3. Push to GHCR with multiple tags:        │
│     • latest  (main branch)                │
│     • main-abc1234 (branch+SHA)            │
│     • v1.2.3  (git tag)                    │
│  4. Trivy security scan → GitHub Security  │
└─────────────────────────────────────────────┘
```

### Workflow Files Summary

| File | Trigger | Key Output |
|---|---|---|
| `ci.yml` | Push to any branch, PRs | Test results, coverage, JAR artifact |
| `docker-build.yml` | Push to `main`, `v*.*.*` tags | Docker image in GHCR, security scan |
| `deploy-dev.yml` | Push to `develop`, manual | App deployed to dev server via SSH |
| `deploy-prod.yml` | Push of `v*.*.*` tag, manual | **Production deploy** with approval + rollback |

### Triggering Workflows

```bash
# Normal code push — triggers ci.yml automatically
git push origin feature/my-feature

# Merge to main — triggers ci.yml + docker-build.yml
git push origin main

# Push a release tag — triggers docker-build.yml + deploy-prod.yml
git tag v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0

# Manual workflow trigger via GitHub CLI
gh workflow run docker-build.yml
gh workflow run deploy-dev.yml
gh workflow run deploy-prod.yml --field image_tag=v1.2.0
```

### Viewing CI Results

```
GitHub Actions:  https://github.com/sanjeetji/logistics-platform/actions
GHCR Packages:  https://github.com/sanjeetji/logistics-platform/pkgs/container/logistics-platform%2Flogistic-app
Security Scan:  https://github.com/sanjeetji/logistics-platform/security/code-scanning
```

---

## 6. Docker Image Management (GHCR)

### Image Location

```
ghcr.io/sanjeetji/logistics-platform/logistic-app
```

### Available Tags

| Tag Pattern | When Created | Example |
|---|---|---|
| `latest` | Every push to `main` | `logistic-app:latest` |
| `main-<sha>` | Every push to `main` | `logistic-app:main-abc1234` |
| `v1.2.3` | Git tag `v1.2.3` pushed | `logistic-app:v1.2.3` |
| `1.2` | Git tag `v1.2.x` pushed | `logistic-app:1.2` |

### Pull an Image Locally

```bash
# Public — no auth needed
docker pull ghcr.io/sanjeetji/logistics-platform/logistic-app:latest

# Private — authenticate first
echo "YOUR_GITHUB_PAT" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
docker pull ghcr.io/sanjeetji/logistics-platform/logistic-app:v1.2.0
```

> Generate a PAT at: **GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)**  
> Scope needed: `read:packages`

### Run Locally Using the CI-Built Image (No Local Build)

```bash
export LOGISTIC_APP_IMAGE=ghcr.io/sanjeetji/logistics-platform/logistic-app:latest

docker compose \
  -f docker/docker-compose.yml \
  up -d
```

---

## 7. Production Deployment

### Architecture

```
Internet
    │
    ▼
Nginx (443/80)  ←── SSL termination
    │
    ▼
logistic-app (8080)  ←── Spring Boot
    │
    ├── postgres (5432)
    ├── redis (6379)
    ├── kafka (29092)
    └── elasticsearch (9200)
```

### How a Release Works — Step by Step

**Step 1: Merge your changes to `main` and ensure CI passes.**

**Step 2: Create and push a semantic version tag.**

```bash
git checkout main && git pull
git tag v1.2.0 -m "Release v1.2.0 — brief description"
git push origin v1.2.0
```

This automatically:
- Triggers `docker-build.yml` → builds image tagged `v1.2.0` and pushes to GHCR
- Triggers `deploy-prod.yml` → starts the deployment pipeline

**Step 3: Approve the deployment in GitHub Actions.**

> In GitHub: **Actions → Deploy to Production → Review deployments → Approve**

After approval, the workflow automatically:
1. ✅ Verifies image `v1.2.0` exists in GHCR (preflight)
2. 📂 Syncs `docker-compose.yml` and `docker-compose.prod.yml` to the server
3. 💾 Records the current image tag (for rollback)
4. 📥 Pulls image `v1.2.0` on the production server
5. 🔄 Restarts the `logistic-app` container with zero downtime
6. 🏥 Polls `/actuator/health` for up to 2 minutes
7. ♻️ Auto-rolls back to the previous image if health check fails

---

### One-Time Production Server Setup

SSH into your production server and run:

```bash
# Create directories
sudo mkdir -p /opt/logistics-platform
sudo chown $USER:$USER /opt/logistics-platform
cd /opt/logistics-platform

# Clone the repo (for docker-compose files)
git clone https://github.com/sanjeetji/logistics-platform.git .

# Create the .env file (KEEP THIS SECRET — never commit it)
cat > .env << 'EOF'
# Database
PROD_DB_URL=jdbc:postgresql://postgres:5432/logistics_postgres
PROD_DB_USER=logistics_user
PROD_DB_PASSWORD=CHANGE_ME_STRONG_PASSWORD

# Redis
PROD_REDIS_HOST=redis
PROD_REDIS_PASSWORD=CHANGE_ME_REDIS_PASSWORD

# Kafka
PROD_KAFKA_BROKERS=kafka:29092

# JWT
PROD_JWT_SECRET=CHANGE_ME_64_CHAR_RANDOM_STRING_AT_LEAST_256_BITS

# Email
PROD_MAIL_HOST=smtp.yourprovider.com
PROD_MAIL_PORT=587
PROD_MAIL_USERNAME=noreply@yourdomain.com
PROD_MAIL_PASSWORD=CHANGE_ME

# AWS / MinIO
PROD_AWS_REGION=ap-south-1
PROD_AWS_ACCESS_KEY_ID=YOUR_KEY
PROD_AWS_SECRET_ACCESS_KEY=YOUR_SECRET
EOF

chmod 600 .env

# Start infrastructure services (one time)
docker compose \
  -f docker/docker-compose.yml \
  -f docker/docker-compose.prod.yml \
  --env-file .env \
  up -d postgres redis kafka zookeeper elasticsearch

# Verify infra is healthy (wait ~60 seconds)
sleep 60
docker ps
```

---

### GitHub Environment Setup (One Time)

1. Go to: **GitHub Repo → Settings → Environments → New environment**
2. Name: `production`
3. Enable **Required reviewers** → add yourself
4. Restrict to `main` branch
5. Add secrets:

| Secret Key | Value |
|---|---|
| `PROD_SERVER_HOST` | `your.prod.server.ip` |
| `PROD_SERVER_USER` | `ubuntu` |
| `PROD_SSH_PRIVATE_KEY` | Contents of `~/.ssh/id_rsa` (the key that can SSH to prod) |

> ⚠️ These secrets go in **Environments → production** — NOT in the general Actions secrets.

---

### Production Compose Commands (on Prod Server)

```bash
# Check what's running
docker compose \
  -f docker/docker-compose.yml \
  -f docker/docker-compose.prod.yml \
  ps

# View app logs
docker logs -f logistics-app

# View all logs
docker compose -f docker/docker-compose.yml logs -f

# Restart only the app (without touching infra)
docker compose -f docker/docker-compose.yml restart logistic-app
```

---

## 8. Rollback Procedures

### Automatic Rollback (via CI/CD)
If the health check fails during a `deploy-prod.yml` run, the workflow automatically rolls back to the previous image. No action needed.

### Manual Rollback — Option A (Recommended): Tag and Deploy

```bash
# Find the last known good tag
gh release list
# or: git tag --sort=-creatordate | head -5

# Trigger a new deployment with that tag
gh workflow run deploy-prod.yml --field image_tag=v1.1.0
# Then approve in GitHub Actions UI
```

### Manual Rollback — Option B: Direct Server Command

```bash
ssh ubuntu@YOUR_PROD_IP
cd /opt/logistics-platform

# Set image to the previous version
export LOGISTIC_APP_IMAGE=ghcr.io/sanjeetji/logistics-platform/logistic-app:v1.1.0

# Restart with that image
docker compose \
  -f docker/docker-compose.yml \
  -f docker/docker-compose.prod.yml \
  up -d --no-deps --pull never logistic-app

# Verify health
curl -s http://localhost:8080/actuator/health | jq
```

---

## 9. Database Management

### Backup (Local Dev)

```bash
./docker/scripts/postgres-backup.sh
# Saves to: backups/postgres/logistics_backup_YYYYMMDD_HHMMSS.sql.gz
```

### Restore (Local Dev)

```bash
./docker/scripts/postgres-restore.sh backups/postgres/logistics_backup_20260226_120000.sql.gz
# ⚠️ Will confirm before overwriting — then restarts platform
```

### Manual psql (Local Dev)

```bash
./docker/scripts/postgres-connect.sh
# or any specific db:
./docker/scripts/postgres-connect.sh logistics_postgres
```

### Backup on Production Server

```bash
ssh ubuntu@YOUR_PROD_IP
cd /opt/logistics-platform

# Create a timestamped backup
docker compose -f docker/docker-compose.yml exec -T postgres \
  pg_dumpall -U logistics_user | gzip > /var/backups/logistics_$(date +%Y%m%d).sql.gz

echo "Backup size: $(du -h /var/backups/logistics_$(date +%Y%m%d).sql.gz | cut -f1)"
```

### Create Test Databases (for running tests locally)

```bash
./docker/scripts/create_test_dbs.sh
```

---

## 10. Monitoring & Health Checks

### Application Health

```bash
# Quick health check
curl http://localhost:8080/actuator/health | jq

# Full health with details (if actuator is open)
curl http://localhost:8080/actuator/health/readiness | jq
curl http://localhost:8080/actuator/health/liveness | jq
```

### All Services Health (Local)

```bash
./docker/scripts/health-check.sh
```

### Docker Container Status

```bash
# Running containers + status
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Resource usage (CPU, memory)
docker stats --no-stream
```

### Application Logs

```bash
# All logs
./docker/scripts/run-platform.sh logs

# App only
docker logs -f logistics-app --tail=100

# Specific service
docker compose -f docker/docker-compose.yml logs -f kafka
```

### GitHub Actions Build Status

```bash
# View recent runs
gh run list --limit 10

# Watch a running workflow
gh run watch
```

---

## 11. Troubleshooting

### App Won't Start — Port Already in Use

```bash
# Find what's using port 8080
lsof -i :8080

# Kill it
kill -9 $(lsof -ti :8080)

# Or restart platform (handles this automatically)
./docker/scripts/run-platform.sh start
```

### Database Connection Refused

```bash
# Check PostgreSQL is healthy
docker compose -f docker/docker-compose.yml exec postgres pg_isready -U logistics_user

# Restart just postgres
docker compose -f docker/docker-compose.yml restart postgres

# Check logs
docker logs logistics-postgres --tail=50
```

### Out of Disk Space (Docker)

```bash
# Check Docker disk usage
docker system df

# Clean just build cache (safe, doesn't delete images)
docker builder prune -f

# Clean all unused images + build cache
docker image prune -a -f
docker builder prune -a -f

# Nuclear cleanup (removes everything)
./docker/scripts/clean.sh
```

### IDE Can't Find Classes (Lombok/MapStruct)

```bash
./docker/scripts/fix_ide_errors.sh
# Then: IntelliJ → File → Invalidate Caches / Restart
```

### pgAdmin Can't Connect to PostgreSQL

```bash
./docker/scripts/fix-pgadmin.sh
# Use hostname: postgres (not localhost, not postgres-db)
```

### CI Build Failing on GitHub Actions

```bash
# View latest run
gh run list --limit 5
gh run view <run-id> --log-failed
```

---

## 12. Secrets & Environment Variables Reference

### Local Development (docker-compose.yml)

| Variable | Value | Where Used |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/logistics_postgres` | App |
| `SPRING_DATASOURCE_USERNAME` | `logistics_user` | App |
| `SPRING_DATASOURCE_PASSWORD` | `logistics_pass` | App |
| `SPRING_DATA_REDIS_HOST` | `redis` | App |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka:29092` | App |
| `SPRING_PROFILES_ACTIVE` | `docker` | App |

### GitHub Actions Secrets

| Secret | Environment | Purpose |
|---|---|---|
| `NVD_API_KEY` | Repository | OWASP dependency scan |
| `DEV_SERVER_HOST` | Repository | Dev server IP |
| `DEV_SERVER_USER` | Repository | Dev SSH user |
| `DEV_SSH_PRIVATE_KEY` | Repository | Dev SSH key |
| `PROD_SERVER_HOST` | `production` env | Prod server IP |
| `PROD_SERVER_USER` | `production` env | Prod SSH user |
| `PROD_SSH_PRIVATE_KEY` | `production` env | Prod SSH key |

> `GITHUB_TOKEN` is auto-provided by GitHub — no setup needed.

### Production Server `.env` File

Located at `/opt/logistics-platform/.env` on the production server.
**Never commit this file.** It is git-ignored.

| Key | Description |
|---|---|
| `PROD_DB_PASSWORD` | PostgreSQL password |
| `PROD_REDIS_PASSWORD` | Redis password |
| `PROD_JWT_SECRET` | JWT signing secret (min 256-bit) |
| `PROD_MAIL_*` | SMTP config for email notifications |
| `PROD_AWS_*` | AWS / MinIO credentials for file storage |
| `LOGISTIC_APP_IMAGE` | Set by CI during deploy, e.g. `ghcr.io/.../logistic-app:v1.2.0` |

---

> 📘 **See also:**
> - [`GIT_WORKFLOW_GUIDE.md`](GIT_WORKFLOW_GUIDE.md) — How to use Git, test locally, and create PRs.
> - [`DOCKER_GUIDE.md`](DOCKER_GUIDE.md) — Docker commands deep dive, disk cleanup, image/volume management
> - [`docker/scripts/README.md`](../docker/scripts/README.md) — Scripts quick reference
> - [`DATABASE_SETUP_GUIDE.md`](DATABASE_SETUP_GUIDE.md) — Initial DB schema and migrations
