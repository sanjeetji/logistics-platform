#!/bin/bash
echo "🏥 Health Check for Logistics Platform..."
echo "========================================"

services=(
    "Service Discovery:8761"
    "Config Server:8888"
    "Platform Core:8080"
    "B2B Engine:8081"
    "B2C Engine:8082"
)

for service in "${services[@]}"; do
    name=$(echo $service | cut -d':' -f1)
    port=$(echo $service | cut -d':' -f2)

    if curl -s -f "http://localhost:$port/actuator/health" > /dev/null; then
        echo "✅ $name is UP"
    else
        echo "❌ $name is DOWN"
    fi
done

echo ""
echo "📊 Database Status:"
if docker exec logistics-mysql mysqladmin ping -h localhost -u root -prootpassword > /dev/null 2>&1; then
    echo "✅ MySQL is UP"
else
    echo "❌ MySQL is DOWN"
fi

echo ""
echo "📈 RabbitMQ Status:"
if curl -s -u admin:admin123 http://localhost:15672/api/overview > /dev/null 2>&1; then
    echo "✅ RabbitMQ is UP"
else
    echo "❌ RabbitMQ is DOWN"
fi