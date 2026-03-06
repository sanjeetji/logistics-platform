#!/bin/bash

# ==============================================================================
# Logistics Platform Automation Script (Logistic App Edition)
# ==============================================================================

# --- Colors ---
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."
export PATH=$PATH:/usr/local/bin

# Maven executable — use Homebrew Maven if available, else fall back to PATH
MVN="/opt/homebrew/bin/mvn"
if [ ! -f "$MVN" ]; then MVN="mvn"; fi

log_info()    { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC} $1"; }

COMMAND=$1

# --- Shared: Containers & Ports to clean up ---
CONTAINERS=(
  "logistics-kafka"
  "logistics-zookeeper"
  "logistics-postgres"
  "logistics-redis"
  "logistics-elasticsearch"
  "logistics-minio"
  "logistics-mailhog"
  "logistics-pgadmin"
  "logistics-app"
)

PORTS=(
  "5432"   # PostgreSQL
  "5050"   # pgAdmin
  "2181"   # Zookeeper
  "9092"   # Kafka (external)
  "29092"  # Kafka (internal)
  "6379"   # Redis
  "9000"   # MinIO API
  "9001"   # MinIO Console
  "1025"   # MailHog SMTP
  "8025"   # MailHog UI
  "9200"   # Elasticsearch
  "8080"   # Logistic App
)

VOLUMES=(
  "docker_postgres-data"
  "docker_zookeeper-data"
  "docker_zookeeper-log"
  "docker_kafka-data"
  "docker_redis-data"
  "docker_elasticsearch-data"
  "docker_minio-data"
)

# --- Infrastructure Helpers ---

check_environment() {
  log_info "Performing pre-flight checks..."
  
  # Check if Docker is running
  if ! docker info >/dev/null 2>&1; then
    log_error "Docker is not running! Please start Colima (e.g., 'colima start -m 8 -c 4') or Docker Desktop."
    exit 1
  fi

  # Check Colima resources if applicable
  if command -v colima >/dev/null 2>&1 && colima status >/dev/null 2>&1; then
    if command -v jq >/dev/null 2>&1; then
      COLIMA_JSON=$(colima list -j | head -n 1)
      CPU=$(echo "$COLIMA_JSON" | jq -r '.cpus')
      MEM_BYTES=$(echo "$COLIMA_JSON" | jq -r '.memory')
      MEM=$((MEM_BYTES / 1024 / 1024 / 1024))
      
      log_info "  Colima detected: ${CPU} CPUs, ${MEM}Gi Memory."
      if [ "${CPU}" -lt 4 ] || [ "${MEM}" -lt 8 ]; then
         log_warn "  Low resources detected. Recommend at least 4 CPUs and 8Gi Memory."
      fi
    else
      log_warn "  Colima detected but 'jq' not found. Skipping resource check."
    fi
  fi
}

self_heal_kafka() {
  log_info "Checking Kafka health..."
  KAFKA_LOGS=$(docker logs logistics-kafka 2>&1 | tail -n 50)
  if echo "$KAFKA_LOGS" | grep -q "InconsistentClusterIdException"; then
    log_warn "Detected Kafka Cluster ID mismatch! Attempting self-healing..."
    docker stop logistics-kafka logistics-zookeeper >/dev/null 2>&1
    docker rm logistics-kafka logistics-zookeeper >/dev/null 2>&1
    for vol in docker_kafka-data docker_zookeeper-data docker_zookeeper-log logistics-platform_kafka-data logistics-platform_zookeeper-data logistics-platform_zookeeper-log; do
        docker volume rm "$vol" 2>/dev/null || true
    done
    log_info "  Kafka data cleared. Restarting infrastructure..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" up -d zookeeper kafka
    sleep 10
    log_success "  Kafka self-healing initiated."
    return 0
  fi
  return 1
}

run_doctor() {
  log_info "--- Platform Doctor ---"
  
  # 1. Docker Status
  if docker info >/dev/null 2>&1; then
    log_success "Docker is running."
  else
    log_error "Docker is NOT running."
  fi

  # 2. Port Conflicts
  log_info "Checking for port conflicts..."
  for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti tcp:"$PORT" 2>/dev/null)
    if [ -n "$PID" ]; then
      PROCESS=$(ps -p "$PID" -o comm=)
      log_warn "  Port $PORT is in use by $PROCESS (PID: $PID)."
    fi
  done

  # 3. Kafka Check
  if docker ps --filter "name=logistics-kafka" --filter "status=running" | grep -q "kafka"; then
    KAFKA_LOGS=$(docker logs logistics-kafka 2>&1 | tail -n 50)
    if echo "$KAFKA_LOGS" | grep -q "InconsistentClusterIdException"; then
      log_error "Kafka Cluster ID mismatch detected."
      log_info "  Fix: Run './run-platform.sh start' (it will auto-heal) or './run-platform.sh fresh'."
    else
      log_success "Kafka appears healthy."
    fi
  else
    log_warn "Kafka is not running."
  fi

  # 4. Postgres Check
  if docker ps --filter "name=logistics-postgres" --filter "status=running" | grep -q "postgres"; then
    log_success "Postgres is running."
  else
    log_warn "Postgres is not running."
  fi

  log_info "-----------------------"
}

cleanup_containers_and_ports() {
  log_info "Cleaning up stale containers..."
  for CONTAINER in "${CONTAINERS[@]}"; do
    if [ "$(docker ps -aq -f name=^/${CONTAINER}$)" ]; then
      docker stop "$CONTAINER" >/dev/null 2>&1 || true
      docker rm "$CONTAINER" >/dev/null 2>&1 && log_info "  Removed $CONTAINER." || true
    fi
  done

  log_info "Verifying platform ports..."
  for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti tcp:"$PORT" 2>/dev/null)
    if [ -n "$PID" ]; then
      PROCESS=$(ps -p "$PID" -o comm=)
      # Only warn, don't kill host processes. 
      # Colima/Docker might be using them for tunnel/forwarding.
      log_warn "  Port $PORT is currently in use by $PROCESS (PID: $PID)."
      log_info "  If startup fails, you may need to free this port manually."
    fi
  done
}

drop_all_volumes() {
  log_warn "Dropping all data volumes (fresh mode)..."
  for VOL in "${VOLUMES[@]}"; do
    docker volume rm "$VOL" 2>/dev/null || \
    docker volume rm "logistics-platform_${VOL#docker_}" 2>/dev/null || true
  done
  log_info "All volumes cleared."
}

case "$COMMAND" in

  # ─────────────────────────────────────────────────────────────────────
  # START — Quick restart. Keeps data (volumes). Does NOT rebuild image.
  # Use this for: daily startups after closing Docker / rebooting Mac
  # ─────────────────────────────────────────────────────────────────────
  start)
    check_environment
    log_info "Starting Logistics Platform (quick start — data preserved)..."
    cleanup_containers_and_ports
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" up -d
    
    log_info "Waiting for Kafka to stabilize..."
    MAX_RETRIES=12
    COUNT=0
    while [ $COUNT -lt $MAX_RETRIES ]; do
      if docker inspect logistics-kafka --format '{{.State.Health.Status}}' 2>/dev/null | grep -q "healthy"; then
        log_success "Kafka is healthy."
        break
      fi
      
      # Check if it already failed with mismatch
      if self_heal_kafka; then
        # If healed, it will restart Kafka, so we reset our wait
        log_info "Triggered self-healing. Waiting again..."
        COUNT=0
      fi
      
      sleep 5
      COUNT=$((COUNT + 1))
      log_info "  Still waiting for Kafka... ($((COUNT * 5))s)"
    done
    
    log_success "Platform started. Data from previous session is preserved."
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # FRESH — Full reset. Wipes all volumes + rebuilds app image.
  # Use this for: DB schema changes, entity changes, corrupted state
  # ─────────────────────────────────────────────────────────────────────
  fresh)
    check_environment
    log_warn "FRESH START — All data will be wiped and image rebuilt!"
    cleanup_containers_and_ports
    drop_all_volumes
    log_info "Running Maven build (this ensures new code is compiled)..."
    (cd "$PROJECT_ROOT" && "$MVN" clean install -DskipTests -q) || { log_error "Maven build failed! Aborting."; exit 1; }
    log_info "Rebuilding Docker image..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" up -d --build
    log_success "Platform started fresh. All tables recreated from scratch."
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # STOP — Stop all containers (keeps volumes/data)
  # ─────────────────────────────────────────────────────────────────────
  stop)
    log_info "Stopping Platform..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" down
    log_success "Stopped. Data volumes preserved."
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # DOCTOR — Diagnostics
  # ─────────────────────────────────────────────────────────────────────
  doctor)
    run_doctor
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # RESTART — Quick stop + quick start (keeps data)
  # ─────────────────────────────────────────────────────────────────────
  restart)
    $0 stop
    $0 start
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # LOGS — Tail all container logs
  # ─────────────────────────────────────────────────────────────────────
  logs)
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" logs -f
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # BUILD — Maven build + Docker image rebuild only (no start)
  # ─────────────────────────────────────────────────────────────────────
  build)
    log_info "Building Logistic App (Maven)..."
    (cd "$PROJECT_ROOT" && "$MVN" clean install -DskipTests -q) || { log_error "Maven build failed!"; exit 1; }
    log_info "Rebuilding Docker Image..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" build logistic-app
    log_success "Build complete. Run './run-platform.sh start' to start."
    ;;

  help|*)
    echo ""
    echo "Usage: ./run-platform.sh [command]"
    echo ""
    echo "  start    Quick start — keeps data, no image rebuild   (daily use)"
    echo "  fresh    Full reset  — wipes data, rebuilds image     (after schema changes)"
    echo "  stop     Stop all containers (data preserved)"
    echo "  restart  Stop + quick start"
    echo "  doctor   Run diagnostics and environment checks"
    echo "  logs     Tail all logs"
    echo "  build    Maven build + Docker image rebuild only"
    echo ""
    ;;
esac
