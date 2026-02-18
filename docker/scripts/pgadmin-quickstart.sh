#!/bin/bash
echo "🚀 Quick Start for pgAdmin with PostgreSQL"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Stop any existing instances
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform down pgadmin postgres-db 2>/dev/null

# Start fresh
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d postgres-db

echo "⏳ Waiting for PostgreSQL to start..."
sleep 5

# Start pgAdmin
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d pgadmin

echo "⏳ Waiting for pgAdmin to start..."
sleep 3

echo ""
echo "✅ SETUP COMPLETE!"
echo ""
echo "📊 ACCESS DETAILS:"
echo "=================="
echo "🌐 pgAdmin URL:    http://localhost:5050"
echo "📧 Login Email:    admin@logistics.com"
echo "🔐 Login Password: admin123"
echo ""
echo "🐘 PostgreSQL Info:"
echo "=================="
echo "Host:     postgres-db (use this in pgAdmin)"
echo "Port:     5432"
echo "Username: logistics_user"
echo "Password: logistics_pass"
echo "Database: logistics_postgres"
echo ""
echo "🔧 Connection Steps in pgAdmin:"
echo "==============================="
echo "1. Go to http://localhost:5050"
echo "2. Login with admin@logistics.com / admin123"
echo "3. Right-click 'Servers' → 'Register' → 'Server'"
echo "4. General Tab: Name = 'Logistics PostgreSQL'"
echo "5. Connection Tab:"
echo "   - Host: postgres-db"
echo "   - Port: 5432"
echo "   - Username: logistics_user"
echo "   - Password: logistics_pass"
echo "6. Click 'Save'"
echo ""
echo "🔄 If connection fails, run: ./docker/scripts/fix-pgadmin.sh"