#!/bin/bash

# Logistics Platform - Status Check Script

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Logistics Platform Status${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Function to check service status
check_service() {
    local name=$1
    local port=$2
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        local health=$(curl -s http://localhost:$port/actuator/health 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$health" = "UP" ]; then
            echo -e "${GREEN}✓${NC} $name (port $port) - ${GREEN}UP${NC}"
        else
            echo -e "${YELLOW}⚠${NC} $name (port $port) - ${YELLOW}RUNNING (health: $health)${NC}"
        fi
    else
        echo -e "${RED}✗${NC} $name (port $port) - ${RED}DOWN${NC}"
    fi
}

echo -e "${YELLOW}Infrastructure Services:${NC}"
check_service "Config Server      " 8888
check_service "Service Discovery  " 8761
check_service "API Gateway        " 8080

echo ""
echo -e "${YELLOW}Core Platform Services:${NC}"
check_service "Auth Service       " 8081
check_service "Order Service      " 8085
check_service "Route Optimization " 8110
check_service "Tracking Service   " 8095

echo ""
echo -e "${YELLOW}External Dependencies:${NC}"

# Check PostgreSQL
if pgrep -x "postgres" > /dev/null; then
    echo -e "${GREEN}✓${NC} PostgreSQL - ${GREEN}RUNNING${NC}"
else
    echo -e "${RED}✗${NC} PostgreSQL - ${RED}NOT RUNNING${NC}"
fi

# Check Docker containers
if command -v docker &> /dev/null; then
    echo ""
    echo -e "${YELLOW}Docker Containers:${NC}"
    docker ps --format "table {{.Names}}\t{{.Status}}" 2>/dev/null || echo -e "${YELLOW}No Docker containers running${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo ""

# Show Eureka dashboard link
if lsof -Pi :8761 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}Eureka Dashboard: http://localhost:8761${NC}"
    echo -e "${GREEN}API Gateway:      http://localhost:8080${NC}"
    echo ""
fi
