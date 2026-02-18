#!/bin/bash

# ==============================================================================
# Run Specific Service(s) Script
# ==============================================================================
# Usage: ./run-service.sh [service1] [service2] ...
# Example: ./run-service.sh order-service
# This script starts the required infrastructure (DB, Redis, Config, Eureka)
# and then starts the specified service(s).

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

if [ $# -eq 0 ]; then
    echo "Usage: $0 [service_name...]"
    echo "Example: $0 order-service"
    echo "Available services: Check docker-compose.yml"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
PROJECT_ROOT="$SCRIPT_DIR/../.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
ENV_FILE="$PROJECT_ROOT/.env"

# Core Infrastructure Services that usually must run
INFRA_SERVICES="postgres-db redis rabbitmq service-discovery config-server"

log_info "Starting Infrastructure Services: $INFRA_SERVICES"
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" up -d $INFRA_SERVICES

echo "Waiting for core infra to initialize (20s)..."
sleep 20

# Start requested services
REQUESTED_SERVICES="$@"
log_info "Starting Requested Services: $REQUESTED_SERVICES"

if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" up -d $REQUESTED_SERVICES; then
    log_success "Services started successfully!"
    echo ""
    log_info "Status:"
    docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" ps
else
    log_error "Failed to start services."
    exit 1
fi
