#!/bin/bash

# ==============================================================================
# Complete Logistics Platform Startup Script
# ==============================================================================
# This script starts all 23 microservices in the correct order

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

echo "🚀 Starting Complete Logistics Platform..."
echo "================================================"

# Step 1: Stop any running containers
log_info "Stopping any existing containers..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform down 2>/dev/null

# Step 2: Start Infrastructure - Databases
# Step 2: Start Infrastructure - Databases
log_info "📦 Step 1/6: Starting databases..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d postgres-db
echo "Waiting for databases to be ready (30s)..."
sleep 30

# Verify databases
if docker exec logistics-postgres pg_isready -U logistics_user > /dev/null 2>&1; then
    log_success "PostgreSQL is ready"
else
    log_error "PostgreSQL failed to start"
    exit 1
fi

# Step 3: Start Cache & Messaging
log_info "📦 Step 2/6: Starting Redis and RabbitMQ..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d redis rabbitmq
sleep 15

# Step 4: Start Service Discovery & Config
log_info "🔍 Step 3/6: Starting Service Discovery and Config Server..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d service-discovery config-server
echo "Waiting for Eureka to be ready (40s)..."
sleep 40

# Verify Eureka
if curl -f http://localhost:8761/actuator/health > /dev/null 2>&1; then
    log_success "Eureka is ready"
else
    log_warn "Eureka may not be fully ready, continuing anyway..."
fi

# Step 5: Start Monitoring Stack
log_info "📊 Step 4/6: Starting monitoring stack (Loki, Promtail, Tempo, Grafana)..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d loki promtail tempo grafana pgadmin
sleep 10

# Step 6: Start Core Services
log_info "⚙️ Step 5/6: Starting core microservices..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d \
  auth-service \
  user-service \
  fleet-service \
  dispatch-service \
  order-service \
  parcel-service

echo "Waiting for core services (30s)..."
sleep 30

# Step 7: Start Advanced Features (Phase 5)
log_info "🚀 Step 6/7: Starting Advanced Features (ML, Analytics, Notification)..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d \
  analytics-service \
  notification-service \
  ml-service \
  geo-service \
  pricing-service \
  payment-service \
  billing-service \
  tracking-service

echo "Waiting for advanced services (20s)..."
sleep 20

# Step 8: Start API Gateway
log_info "🌐 Step 7/7: Starting API Gateway..."
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d gateway-service
sleep 15

# Final Status
echo ""
log_success "✅ Platform startup complete!"
echo "================================================"
echo ""

# Show running containers
log_info "📊 Service Status:"
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform ps

echo ""
log_info "🔗 Access Points:"
echo "  - Eureka Dashboard:    http://localhost:8761"
echo "  - API Gateway:         http://localhost:8080"
echo "  - Grafana:             http://localhost:3000 (admin/admin)"
echo "  - RabbitMQ Management: http://localhost:15672 (admin/admin123)"
echo "  - pgAdmin:             http://localhost:5050 (admin@logistics.com/admin123)"
echo ""

# Check for unhealthy services
log_info "Checking service health..."
UNHEALTHY=$(docker ps --filter "health=unhealthy" --format "{{.Names}}")
RESTARTING=$(docker ps --filter "status=restarting" --format "{{.Names}}")

if [ -n "$UNHEALTHY" ]; then
    log_warn "Unhealthy services detected:"
    echo "$UNHEALTHY"
fi

if [ -n "$RESTARTING" ]; then
    log_error "Services restarting (check logs):"
    echo "$RESTARTING"
fi

echo ""
log_info "To view logs: ./run-platform.sh logs"
log_info "To stop platform: ./run-platform.sh stop"
echo ""
