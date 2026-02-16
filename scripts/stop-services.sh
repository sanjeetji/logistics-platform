#!/bin/bash

# Logistics Platform - Stop Services Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

LOG_DIR="logs/deployment"

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Stopping Logistics Platform Services${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="$LOG_DIR/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}Stopping $service_name (PID: $pid)...${NC}"
            kill $pid
            sleep 2
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${YELLOW}Force stopping $service_name...${NC}"
                kill -9 $pid
            fi
            
            echo -e "${GREEN}✓ Stopped $service_name${NC}"
        else
            echo -e "${YELLOW}$service_name is not running${NC}"
        fi
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}No PID file found for $service_name${NC}"
    fi
}

# Stop services in reverse order
echo -e "${YELLOW}Stopping platform services...${NC}"
stop_service "tracking-service"
stop_service "route-optimization-service"
stop_service "order-service"
stop_service "auth-service"

echo ""
echo -e "${YELLOW}Stopping infrastructure services...${NC}"
stop_service "api-gateway"
stop_service "service-discovery"
stop_service "config-server"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}All Services Stopped${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Optional: Kill any remaining Spring Boot processes
echo -e "${YELLOW}Checking for remaining Spring Boot processes...${NC}"
pkill -f "spring-boot:run" 2>/dev/null && echo -e "${GREEN}✓ Cleaned up remaining processes${NC}" || echo -e "${GREEN}✓ No remaining processes${NC}"

echo ""
echo -e "${GREEN}✓ Platform shutdown complete${NC}"
