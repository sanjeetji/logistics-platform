# Logistics Platform Scripts Guide

This guide explains how to use the automation scripts located in `docker/scripts/` to manage the Logistics Platform.

## 📂 Script Location
All scripts are located in: `docker/scripts/`

You should run these scripts from the **project root** directory or from the `docker/scripts/` directory. If running from root, use:
```bash
./docker/scripts/script_name.sh [arguments]
```

## 🛠️ Available Scripts

### 1. Operation & Management

#### `run-platform.sh`
The main entry point for managing the entire platform.

**Usage:**
```bash
./docker/scripts/run-platform.sh [COMMAND] [OPTIONS]
```

**Commands:**
- `start`: Start the entire platform (all services).
- `stop`: Stop the platform.
- `restart`: Restart the platform.
- `build`: Build the project (Maven) and Docker images.
- `logs`: Follow logs of all services.
- `status`: Check status of containers.
- `prune`: Clean up unused Docker resources.

**Options:**
- `--env=[dev|prod]`: Select environment (default: `dev`).

#### `run-service.sh`
Run **specific services** along with the required infrastructure (Databases, Redis, RabbitMQ, Service Discovery, Config Server).

**Usage:**
```bash
./docker/scripts/run-service.sh [service_name...]
```

**Example:**
```bash
./docker/scripts/run-service.sh order-service payment-service
```

#### `start-all-services.sh`
A legacy sequence-based startup script. Prefer `run-platform.sh start` unless you need specific staged startup.

### 2. Testing & Quality Assurance

#### `run-integration-test.sh`
Runs integration tests (Order -> Dispatch -> Fleet flow) against a running platform.

**Usage:**
```bash
./docker/scripts/run-integration-test.sh
```
*Prerequisite: The platform (or at least Order, Dispatch, Fleet, Auth services) must be running.*

#### `run-load-tests.sh`
Runs Gatling load tests against the platform.

**Usage:**
```bash
./docker/scripts/run-load-tests.sh [test-type]
```

**Test Types:**
- `quick`: Simple smoke test.
- `orders`: Order processing load.
- `websocket`: WebSocket connection load.
- `drivers`: Driver location streaming.
- `comprehensive`: All scenarios.

#### `create_test_dbs.sh`
Creates test databases for all services. Useful for local development or setting up a test environment.

**Usage:**
```bash
./docker/scripts/create_test_dbs.sh
```
*Prerequisite: `psql` must be installed and accessible.*

## 📝 Tips
- Always ensure Docker Desktop is running.
- If you encounter "Permission denied", run `chmod +x docker/scripts/*.sh`.
- The scripts automatically load environment variables from `.env`.
