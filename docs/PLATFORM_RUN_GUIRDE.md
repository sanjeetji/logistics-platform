# 🚀 Logistics Platform — Run Guide

This guide explains how to start, stop, and manage the Logistics Platform using the automation script.

---

## 📋 Prerequisites

- Docker Desktop is running
- Project is built: `mvn clean install -DskipTests` (run from project root)

---

## ⚡ Commands

```bash
# Run from project root
docker/scripts/run-platform.sh [command]
```

| Command | What it does | When to use |
|---|---|---|
| `start` | Quick start — **keeps existing data**, no image rebuild | **Daily use** — after opening Mac/Docker |
| `fresh` | Full reset — **wipes all data** + rebuilds Docker image | After entity/DB schema changes |
| `stop` | Stops all containers (data is preserved) | End of day |
| `restart` | Stop + quick start | If something is stuck |
| `logs` | Tail all container logs | Debugging |
| `build` | Maven build + Docker image rebuild only (no start) | After code changes |

---

## 🗓️ Daily Workflow

### Morning — Start the platform (fast, keeps your data)
```bash
docker/scripts/run-platform.sh start
```

### After changing a Java Entity or DB Schema
```bash
# Step 1: Rebuild the JAR
mvn clean install -DskipTests

# Step 2: Full reset (wipes DB + rebuilds image)
docker/scripts/run-platform.sh fresh
```

### End of Day — Stop everything
```bash
docker/scripts/run-platform.sh stop
```

### Something broken? Full clean restart
```bash
docker/scripts/run-platform.sh fresh
```

---

## 🏥 Health Check

```bash
docker/scripts/health-check.sh
```

Expected output when everything is running:
```
✓ Logistic App (8080)    - UP
✓ PostgreSQL (5432)      - UP
✓ Kafka (9092)           - UP
✓ Redis (6379)           - UP
✓ Elasticsearch (9200)   - UP
✓ MinIO Console (9001)   - UP
✓ pgAdmin (5050)         - UP
```

---

## 🌐 Service URLs

| Service | URL |
|---|---|
| **Logistic App API** | http://localhost:8080 |
| **pgAdmin** (DB UI) | http://localhost:5050 |
| **MinIO Console** (File Storage) | http://localhost:9001 |
| **MailHog** (Email UI) | http://localhost:8025 |
| **Kafka** | localhost:9092 |
| **PostgreSQL** | localhost:5432 |
| **Redis** | localhost:6379 |
| **Elasticsearch** | http://localhost:9200 |

---

## 🔑 Default Credentials

| Service | Username | Password |
|---|---|---|
| PostgreSQL | `logistics_user` | `logistics_pass` |
| pgAdmin | `admin@logistics.com` | `admin123` |
| MinIO | `minioadmin` | `minioadmin` |

---

## ⚠️ Important Notes

> **`start` preserves data** — your registered users, Kafka topics, and Redis cache are kept between restarts.

> **`fresh` wipes everything** — you will need to re-register users after a `fresh` start. Use it only when you've changed DB entities or the schema.

> **Profiles** — The platform runs with the `docker` profile which uses `create-drop` (dev mode). For production, use the `prod` profile which uses `validate` (safe mode, never drops tables).

---

## 🛠️ Troubleshooting

| Problem | Fix |
|---|---|
| Container fails to start | Run `fresh` to wipe stale state |
| Port already in use | `start` auto-frees all platform ports |
| DB schema mismatch error | Run `fresh` after rebuilding JAR |
| Kafka unhealthy | Run `fresh` — clears stale Zookeeper metadata |
| App not reflecting code changes | Run `build` then `start` |
