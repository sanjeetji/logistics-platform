#!/bin/bash
echo "🚀 Starting Logistics Platform Development Environment..."

# Load environment variables
export $(cat .env | grep -v '^#' | xargs)

# Build and start services
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

echo ""
echo "📊 Services Status:"
echo "=================="
echo "MySQL Database:        http://localhost:3306"
echo "PostgreSQL:            http://localhost:5432"
echo "Redis:                 http://localhost:6379"
echo "RabbitMQ Management:   http://localhost:15672 (admin/admin123)"
echo "Service Discovery:     http://localhost:8761"
echo "Config Server:         http://localhost:8888"
echo "Platform Core:         http://localhost:8080"
echo "B2B Engine:            http://localhost:8081"
echo "B2C Engine:            http://localhost:8082"
echo "pgAdmin:               http://localhost:5050 (admin@logistics.com/admin123)"
echo "MailHog (SMTP):        http://localhost:8025"
echo ""
echo "🖥️  Database Management:"
echo "========================"
echo "MySQL CLI:     docker-compose exec mysql-db mysql -u logistics_user -plogistics_pass"
echo "PostgreSQL CLI: docker-compose exec postgres-db psql -U logistics_user -d logistics_postgres"
echo "pgAdmin Web:    http://localhost:5050"
echo ""
echo "📋 Debug Ports:"
echo "Platform Core:         5005"
echo "B2B Engine:            5006"
echo "B2C Engine:            5007"
echo ""
echo "💡 Commands:"
echo "View logs: docker-compose logs -f [service]"
echo "Stop: ./scripts/stop.sh"
echo "Rebuild: ./scripts/rebuild.sh"