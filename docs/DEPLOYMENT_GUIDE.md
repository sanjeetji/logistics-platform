# Infrastructure Deployment Guide

## Quick Start with Docker Compose

### Start Infrastructure Only (Recommended for Development)

```bash
# Start only infrastructure services (PostgreSQL, Kafka, Redis)
docker-compose up -d postgres zookeeper kafka redis

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### Start Full Platform with Docker

```bash
# Build and start all services
docker-compose up --build -d

# Check status
docker-compose ps

# View logs for specific service
docker-compose logs -f api-gateway

# Stop all services
docker-compose down
```

## Manual Deployment (Without Docker)

### Prerequisites

Since Homebrew services aren't installed, you have two options:

**Option 1: Use Docker for Infrastructure** (Recommended)
```bash
# Start infrastructure with Docker
docker-compose up -d postgres zookeeper kafka redis

# Then run application services manually
cd infrastructure/config-server
mvn spring-boot:run
```

**Option 2: Install Infrastructure Services**
```bash
# Install PostgreSQL
brew install postgresql@16

# Install Kafka (includes Zookeeper)
brew install kafka

# Install Redis
brew install redis

# Start services
brew services start postgresql@16
brew services start zookeeper
brew services start kafka
brew services start redis
```

### Start Application Services

#### Step 1: Start Infrastructure Services (Config Server & Eureka)

```bash
# Terminal 1: Config Server
cd infrastructure/config-server
mvn spring-boot:run

# Terminal 2: Service Discovery (wait for config-server)
cd infrastructure/service-discovery
mvn spring-boot:run

# Terminal 3: API Gateway (wait for eureka)
cd infrastructure/api-gateway
mvn spring-boot:run
```

#### Step 2: Start Core Services

```bash
# Terminal 4: Auth Service
cd platform-core/auth-service
mvn spring-boot:run

# Terminal 5: Order Service
cd platform-core/order-service
mvn spring-boot:run

# Terminal 6: Route Optimization Service
cd shared-services/route-optimization-service
mvn spring-boot:run

# ... Continue for other services as needed
```

## Verification

### Check Infrastructure

```bash
# Check Docker containers
docker-compose ps

# Check PostgreSQL
docker exec -it logistics-postgres psql -U postgres -c "SELECT version();"

# Check Kafka
docker exec -it logistics-kafka kafka-topics --list --bootstrap-server localhost:9092

# Check Redis
docker exec -it logistics-redis redis-cli ping
```

### Check Application Services

```bash
# Check Eureka Dashboard
open http://localhost:8761

# Check API Gateway
curl http://localhost:8080/actuator/health

# Check Config Server
curl http://localhost:8888/actuator/health

# Check Order Service
curl http://localhost:8085/actuator/health
```

## Current Status

✅ PostgreSQL@16 - Running locally  
✅ Docker - Installed and available  
✅ Docker Compose - Installed and available  
⏭️ Kafka - Can start with Docker  
⏭️ Redis - Can start with Docker  
⏭️ Application Services - Ready to deploy

## Recommended Approach

**For Development:**
1. Use Docker Compose for infrastructure (PostgreSQL, Kafka, Redis)
2. Run application services manually with `mvn spring-boot:run`
3. This allows easy debugging and hot-reload

**For Testing:**
1. Use Docker Compose for everything
2. Build all services with `mvn clean install -DskipTests`
3. Start with `docker-compose up --build -d`

**For Production:**
1. Use Kubernetes or Docker Swarm
2. Separate infrastructure from application services
3. Use proper secrets management
4. Enable monitoring and logging
