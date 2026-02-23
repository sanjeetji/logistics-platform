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
  "docker_kafka-data"
  "docker_redis-data"
  "docker_elasticsearch-data"
  "docker_minio-data"
)

cleanup_containers_and_ports() {
  log_info "Cleaning up stale containers..."
  for CONTAINER in "${CONTAINERS[@]}"; do
    docker rm -f "$CONTAINER" 2>/dev/null && log_info "  Removed $CONTAINER." || true
  done

  log_info "Freeing all platform ports..."
  for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti tcp:"$PORT" 2>/dev/null)
    if [ -n "$PID" ]; then
      log_info "  Freeing port $PORT (PID: $PID)..."
      kill -9 "$PID" 2>/dev/null || true
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
    log_info "Starting Logistics Platform (quick start — data preserved)..."
    cleanup_containers_and_ports
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" up -d
    log_success "Platform started. Data from previous session is preserved."
    ;;

  # ─────────────────────────────────────────────────────────────────────
  # FRESH — Full reset. Wipes all volumes + rebuilds app image.
  # Use this for: DB schema changes, entity changes, corrupted state
  # ─────────────────────────────────────────────────────────────────────
  fresh)
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
    echo "  logs     Tail all logs"
    echo "  build    Maven build + Docker image rebuild only"
    echo ""
    ;;
esac
