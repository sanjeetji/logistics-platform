#!/bin/bash
echo "🏥 Health Check for Logistics Platform..."
echo "========================================"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"

# Define core services to check
services=(
    "Service Discovery:8761"
    "Config Server:8888"
    "Gateway Service:8080"
    "Auth Service:8081"
    "User Service:8082"
)

for service in "${services[@]}"; do
    name=$(echo $service | cut -d':' -f1)
    port=$(echo $service | cut -d':' -f2)

    echo -n "Checking $name ($port)... "
    if curl -s -f "http://localhost:$port/actuator/health" > /dev/null; then
        echo "✅ UP"
    else
        echo "❌ DOWN"
    fi
done

echo ""
echo "📊 Database Status:"
if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform exec postgres-db pg_isready -U logistics_user > /dev/null 2>&1; then
    echo "✅ PostgreSQL is UP"
else
    echo "❌ PostgreSQL is DOWN"
fi

echo ""
echo "📈 Infrastructure Status:"
if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform exec kafka kafka-topics --list --bootstrap-server localhost:9092 > /dev/null 2>&1; then
    echo "✅ Kafka is UP"
else
    echo "❌ Kafka is DOWN"
fi

echo ""
echo "Redis Status:"
if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform exec redis redis-cli ping > /dev/null 2>&1; then
    echo "✅ Redis is UP"
else
    echo "❌ Redis is DOWN"
fi