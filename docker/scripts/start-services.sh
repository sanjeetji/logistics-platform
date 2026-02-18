#!/bin/bash

# Logistics Platform - Simplified Deployment Script
# This script starts core services manually using existing PostgreSQL

set -e

# Ensure we are in the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."
cd "$PROJECT_ROOT"


# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Log directory
LOG_DIR="logs/deployment"
mkdir -p "$LOG_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Logistics Platform Deployment${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local name=$1
    local port=$2
    local max_attempts=60
    local attempt=0
    
    echo -e "${YELLOW}Waiting for $name to be ready on port $port...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        if check_port $port; then
            if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
                echo -e "${GREEN}✓ $name is ready!${NC}"
                return 0
            fi
        fi
        attempt=$((attempt + 1))
        sleep 2
    done
    
    echo -e "${RED}✗ $name failed to start${NC}"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    
    echo -e "${YELLOW}Starting $service_name...${NC}"
    
    # Check if already running
    if check_port $port; then
        echo -e "${GREEN}✓ $service_name already running on port $port${NC}"
        return 0
    fi
    
    # Start the service in background
    cd "$service_path"
    nohup mvn spring-boot:run > "$LOG_DIR/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "$LOG_DIR/${service_name}.pid"
    cd - > /dev/null
    
    echo -e "${GREEN}Started $service_name (PID: $pid)${NC}"
    
    # Wait for service to be ready
    wait_for_service "$service_name" $port
}

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

# Check PostgreSQL
if ! pgrep -x "postgres" > /dev/null; then
    echo -e "${RED}✗ PostgreSQL is not running${NC}"
    echo -e "${YELLOW}Please start PostgreSQL with: brew services start postgresql@16${NC}"
    exit 1
fi
echo -e "${GREEN}✓ PostgreSQL is running${NC}"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven is installed${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java is installed${NC}"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Starting Infrastructure Services${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Start Config Server
start_service "config-server" "infrastructure/config-server" 8888

# Start Service Discovery
start_service "service-discovery" "infrastructure/service-discovery" 8761

# Start API Gateway
start_service "api-gateway" "infrastructure/api-gateway" 8080

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Starting Core Platform Services${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Start Auth Service
start_service "auth-service" "platform-core/auth-service" 8081

# Start Order Service
start_service "order-service" "platform-core/order-service" 8085

# Start Route Optimization Service
start_service "route-optimization-service" "shared-services/route-optimization-service" 8110

# Start Tracking Service
start_service "tracking-service" "shared-services/tracking-service" 8095

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

echo -e "${GREEN}Services Status:${NC}"
echo -e "  Config Server:       http://localhost:8888"
echo -e "  Service Discovery:   http://localhost:8761"
echo -e "  API Gateway:         http://localhost:8080"
echo -e "  Auth Service:        http://localhost:8081"
echo -e "  Order Service:       http://localhost:8085"
echo -e "  Route Optimization:  http://localhost:8110"
echo -e "  Tracking Service:    http://localhost:8095"
echo ""

echo -e "${YELLOW}Logs are available in: $LOG_DIR${NC}"
echo -e "${YELLOW}To stop services, run: ./scripts/stop-services.sh${NC}"
echo ""

echo -e "${GREEN}✓ Platform is ready for testing!${NC}"
