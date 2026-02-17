#!/bin/bash

# Logistics Platform Load Testing Runner
# This script runs different load test scenarios based on the test type

set -e

# Colors for output
RED='\033[0:31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
BASE_URL="${BASE_URL:-http://localhost:8080}"
FLEET_URL="${FLEET_URL:-http://localhost:8083}"
WS_URL="${WS_URL:-ws://localhost:8085}"
TEST_TYPE="${1:-quick}"

echo -e "${GREEN}==============================================\033[0m"
echo -e "${GREEN}Logistics Platform Load Testing\033[0m"
echo -e "${GREEN}==============================================\033[0m"
echo ""

# Navigate to load-tests directory
cd "$(dirname "$0")/../../load-tests"

case "$TEST_TYPE" in
  "quick"|"smoke")
    echo -e "${YELLOW}Running Quick Smoke Test (low load)...${NC}"
    mvn gatling:test \
      -Dgatling.simulationClass=simulations.OrderProcessingSimulation \
      -DbaseUrl=$BASE_URL \
      -DtargetOrders=100 \
      -DrampUp=10
    ;;
    
  "orders")
    echo -e "${YELLOW}Running Order Processing Load Test (10k orders/hour)...${NC}"
    mvn gatling:test \
      -Dgatling.simulationClass=simulations.OrderProcessingSimulation \
      -DbaseUrl=$BASE_URL \
      -DtargetOrders=10000 \
      -DrampUp=300
    ;;
    
  "websocket"|"ws")
    echo -e "${YELLOW}Running WebSocket Connections Test (1000+ connections)...${NC}"
    mvn gatling:test \
      -Dgatling.simulationClass=simulations.WebSocketSimulation \
      -DwsUrl=$WS_URL \
      -DtargetConnections=1000 \
      -DrampUp=60
    ;;
    
  "drivers"|"location")
    echo -e "${YELLOW}Running Driver Location Streaming Test (1000+ drivers)...${NC}"
    mvn gatling:test \
      -Dgatling.simulationClass=simulations.DriverLocationStreamingSimulation \
      -DbaseUrl=$FLEET_URL \
      -DtargetDrivers=1000 \
      -DrampUp=120 \
      -Dduration=600
    ;;
    
  "comprehensive"|"full")
    echo -e "${YELLOW}Running Comprehensive Load Test (all scenarios)...${NC}"
    mvn gatling:test \
      -Dgatling.simulationClass=simulations.ComprehensiveLoadSimulation \
      -DbaseUrl=$BASE_URL \
      -DfleetUrl=$FLEET_URL \
      -DwsUrl=$WS_URL \
      -DordersPerHour=5000 \
      -DactiveDrivers=500 \
      -DwsConnections=500 \
      -Dduration=1800
    ;;
    
  "all")
    echo -e "${YELLOW}Running All Test Simulations...${NC}"
    mvn gatling:test
    ;;
    
  *)
    echo -e "${RED}Invalid test type: $TEST_TYPE${NC}"
    echo ""
    echo "Usage: $0 [test-type]"
    echo ""
    echo "Available test types:"
    echo "  quick         - Quick smoke test (100 orders, 10s ramp-up)"
    echo "  orders        - Order processing test (10k orders/hour)"
    echo "  websocket     - WebSocket connections test (1000+ connections)"
    echo "  drivers       - Driver location streaming test (1000+ drivers)"
    echo "  comprehensive - Comprehensive multi-scenario test"
    echo "  all           - Run all test simulations"
    echo ""
    echo "Examples:"
    echo "  $0 quick"
    echo "  $0 orders"
    echo "  BASE_URL=http://production.example.com $0 comprehensive"
    exit 1
    ;;
esac

echo ""
echo -e "${GREEN}Test completed!${NC}"
echo ""
echo -e "View results at: ${GREEN}load-tests/target/gatling/results/${NC}"
echo ""
