# Logistics Platform - Docker Setup

A comprehensive Docker-based development and deployment environment for the Logistics Platform.

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- Maven 3.8+ (for building services)
- Java 17+ (for local development)

### Development Environment
```bash
# Start all services in development mode
./scripts/start-dev.sh

# Or manually with specific profiles
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d





docker/
├── configs/              # Configuration files for Config Server
├── images/              # Docker image templates
├── mongodb/            # MongoDB configurations
├── mysql/              # MySQL initialization scripts
├── rabbitmq/           # RabbitMQ configurations
├── redis/              # Redis configurations
├── scripts/            # Utility scripts
├── monitoring/         # Prometheus & Grafana configs
├── nginx/              # Nginx load balancer configs
├── docker-compose.yml           # Main compose file
├── docker-compose.dev.yml       # Development overrides
├── docker-compose.prod.yml      # Production overrides
├── .env                        # Environment variables
└── README.md                   # This file
🛠️ Services Overview
Service	Port	Description	Health Check
MySQL Database	3306	Primary relational database	http://localhost:3306
Redis	6379	Caching and session storage	redis-cli ping
RabbitMQ	5672/15672	Message broker	http://localhost:15672
Service Discovery	8761	Eureka server	http://localhost:8761/actuator/health
Config Server	8888	Centralized configuration	http://localhost:8888/actuator/health
Platform Core	8080	Core logistics service	http://localhost:8080/actuator/health
B2B Engine	8081	Business-to-business operations	http://localhost:8081/actuator/health
B2C Engine	8082	Business-to-consumer operations	http://localhost:8082/actuator/health
MailHog (Dev)	8025	Email testing tool	http://localhost:8025
📊 API Endpoints
Platform Core Service
Swagger UI: http://localhost:8080/swagger-ui.html

Health: http://localhost:8080/actuator/health

Metrics: http://localhost:8080/actuator/metrics

API Base: http://localhost:8080/api/v1

B2B Engine Service
API Base: http://localhost:8081/api/v1/b2b

B2C Engine Service
API Base: http://localhost:8082/api/v1/b2c

Service Discovery
Dashboard: http://localhost:8761

RabbitMQ Management
Console: http://localhost:15672

Username: admin

Password: admin123

🔧 Utility Scripts
Script	Purpose
scripts/start-dev.sh	Start development environment
scripts/stop.sh	Stop all services
scripts/rebuild.sh	Rebuild and restart services
scripts/health-check.sh	Check service health
scripts/clean.sh	Clean Docker environment
🐳 Docker Commands Cheat Sheet
bash
# Start services
docker-compose up -d

# Start with development profile
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# View logs
docker-compose logs -f platform-core
docker-compose logs -f b2b-engine

# View all logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Build specific service
docker-compose build platform-core

# View running containers
docker-compose ps

# Execute command in container
docker-compose exec platform-core sh
docker-compose exec mysql-db mysql -u root -p

# View resource usage
docker stats

# Clean unused resources
docker system prune -a
🗄️ Database Access
MySQL
bash
# Connect to MySQL
docker-compose exec mysql-db mysql -u logistics_user -plogistics_pass logistics_core_db

# Or using root
docker-compose exec mysql-db mysql -u root -prootpassword
Redis
bash
# Connect to Redis CLI
docker-compose exec redis redis-cli -a redispass
🔍 Debugging
Java Debug Ports
Platform Core: 5005

B2B Engine: 5006

B2C Engine: 5007

Connect with IDE
Set up remote debug configuration in your IDE

Host: localhost

Port: 5005 (or corresponding port)

Connect to debug the running service

📈 Monitoring
Health Checks
All services include Spring Boot Actuator health endpoints:

http://localhost:8080/actuator/health

http://localhost:8081/actuator/health

http://localhost:8082/actuator/health

Metrics
Prometheus metrics are available at:

`http://localhost:8080/actuator/p