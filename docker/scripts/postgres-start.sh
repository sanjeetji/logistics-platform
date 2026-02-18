#!/bin/bash
echo "🐘 Starting PostgreSQL and pgAdmin..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Start only PostgreSQL and pgAdmin services
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d postgres-db pgadmin

echo ""
echo "✅ PostgreSQL & pgAdmin started successfully!"
echo ""
echo "📊 Access Details:"
echo "================="
echo "PostgreSQL Host:     localhost:5432"
echo "PostgreSQL Username: logistics_user"
echo "PostgreSQL Password: logistics_pass"
echo "PostgreSQL Database: logistics_postgres"
echo ""
echo "🖥️  pgAdmin Access:"
echo "================="
echo "URL:      http://localhost:5050"
echo "Email:    admin@logistics.com"
echo "Password: admin123"
echo ""
echo "💡 Quick Commands:"
echo "Connect via psql: docker compose exec postgres-db psql -U logistics_user -d logistics_postgres"
echo "View logs:        docker compose logs -f postgres-db"
echo "Backup:           ./docker/scripts/postgres-backup.sh"