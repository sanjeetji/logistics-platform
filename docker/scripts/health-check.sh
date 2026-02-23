#!/bin/bash

# Logistics Platform - Unified Health & Status Check Tool
# Combined functionality of health-check.sh and check-status.sh

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
export PATH=$PATH:/usr/local/bin

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}🏥 Logistics Platform Health Status${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Helper to log status with symbols and colors
log_status() {
    local name=$1
    local status=$2
    local details=$3
    if [ "$status" == "UP" ]; then
        echo -e "${GREEN}✓${NC} $name - ${GREEN}UP${NC} $details"
    else
        echo -e "${RED}✗${NC} $name - ${RED}DOWN${NC} $details"
    fi
}

echo -e "${YELLOW}Application Services:${NC}"

# Check Logistic App via Actuator
if curl -s -f "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
    log_status "Logistic App (8080)   " "UP"
else
    # Fallback to port check if actuator is not ready yet
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
        log_status "Logistic App (8080)   " "UP" "(Port open, but Actuator not ready)"
    else
        log_status "Logistic App (8080)   " "DOWN"
    fi
fi

# Check MinIO Console
if curl -s -f "http://localhost:9001" > /dev/null 2>&1; then
    log_status "MinIO Console (9001)  " "UP"
else
    log_status "MinIO Console (9001)  " "DOWN"
fi

# Check pgAdmin
if curl -s -f "http://localhost:5050" > /dev/null 2>&1; then
    log_status "pgAdmin (5050)        " "UP"
else
    log_status "pgAdmin (5050)        " "DOWN"
fi

echo ""
echo -e "${YELLOW}Infrastructure Status:${NC}"

# Check PostgreSQL
if docker compose -f "$COMPOSE_FILE" exec postgres pg_isready -U logistics_user > /dev/null 2>&1; then
    log_status "PostgreSQL (5432)     " "UP"
else
    log_status "PostgreSQL (5432)     " "DOWN"
fi

# Check Kafka
if docker compose -f "$COMPOSE_FILE" exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    log_status "Kafka (9092)          " "UP"
else
    log_status "Kafka (9092)          " "DOWN"
fi

# Check Redis
if docker compose -f "$COMPOSE_FILE" exec redis redis-cli ping 2>/dev/null | grep -q PONG; then
    log_status "Redis (6379)          " "UP"
else
    log_status "Redis (6379)          " "DOWN"
fi

# Check Elasticsearch
if curl -s -f "http://localhost:9200" > /dev/null 2>&1; then
    log_status "Elasticsearch (9200)  " "UP"
else
    log_status "Elasticsearch (9200)  " "DOWN"
fi

echo ""
echo -e "${GREEN}========================================${NC}"