#!/bin/bash
echo "🔌 Connecting to PostgreSQL..."

DATABASE=${1:-logistics_postgres}

echo "Connecting to database: $DATABASE"
echo "Username: logistics_user"
echo ""
echo "Type '\\q' to exit"
echo "Type '\\l' to list databases"
echo "Type '\\dt' to list tables"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform exec postgres-db psql -U logistics_user -d "$DATABASE"