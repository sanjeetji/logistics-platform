#!/bin/bash
set -e

# Defined Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Starting Logistics Platform (Optimized Mode)${NC}"
echo -e "${YELLOW}NOTE: This mode runs ~20 containers with strict memory limits. Ensure you have 16GB+ RAM allocated to Docker.${NC}"

# 1. Build Project
echo -e "\n${GREEN}Step 1: Building project artifacts...${NC}"
cd ../..
mvn clean package -DskipTests
cd docker

# 2. Start Infrastructure
echo -e "\n${GREEN}Step 2: Starting Infrastructure (DB, Redis, Kafka, Minio)...${NC}"
docker compose -f docker-compose-optimized.yml up -d postgres redis zookeeper kafka minio mongo
echo -e "Waiting 15s for DB/Kafka to warm up..."
sleep 15

# 3. Start Discovery & Config
echo -e "\n${GREEN}Step 3: Starting Config Server & Service Discovery...${NC}"
docker compose -f docker-compose-optimized.yml up -d config-server service-discovery
echo -e "Waiting 30s for Eureka to initialize..."
sleep 30

# 4. Start Core Platform
echo -e "\n${GREEN}Step 4: Starting Core Platform Services (Gateway, Auth, User)...${NC}"
docker compose -f docker-compose-optimized.yml up -d api-gateway auth-service tenant-service
echo -e "Waiting 20s for Gateway..."
sleep 20

# 5. Start Business Engines
echo -e "\n${GREEN}Step 5: Starting Business Engines (Order, Dispatch, Fleet, B2B)...${NC}"
docker compose -f docker-compose-optimized.yml up -d order-service dispatch-service fleet-service b2b-order-service inventory-service warehouse-service parcel-service
echo -e "Waiting 20s..."
sleep 20

# 6. Start Shared Services
echo -e "\n${GREEN}Step 6: Starting Critical Shared Services (Notification, Tracking)...${NC}"
docker compose -f docker-compose-optimized.yml up -d notification-service tracking-service

echo -e "\n${GREEN}✅ Optimization Startup Complete!${NC}"
echo -e "Stats:"
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.CPUPerc}}"
