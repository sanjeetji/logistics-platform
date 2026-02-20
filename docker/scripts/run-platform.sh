#!/bin/bash

# ==============================================================================
# Logistics Platform Automation Script (Logistic App Edition)
# ==============================================================================

# --- Colors ---
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

COMMAND=$1

case "$COMMAND" in
  start)
    log_info "Starting Logistics Platform (Logistic App)..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" up -d --build
    log_success "Platform started (Infrastructure + Logistic App)."
    ;;

  stop)
    log_info "Stopping Platform..."
    docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" down
    log_success "Stopped."
    ;;

  restart)
    $0 stop
    $0 start
    ;;
    
  logs)
     docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" logs -f
     ;;
     
  build)
     log_info "Building Logistic App..."
     (cd "$PROJECT_ROOT" && mvn clean install -pl logistic-app -am -DskipTests)
     log_info "Rebuilding Docker Image..."
     docker compose -f "$PROJECT_ROOT/docker/docker-compose.yml" build logistic-app
     ;;

  help|*)
    echo "Usage: ./run-platform.sh [start|stop|restart|logs|build]"
    ;;
esac
