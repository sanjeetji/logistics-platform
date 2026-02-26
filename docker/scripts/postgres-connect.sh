#!/bin/bash
echo "🔌 Connecting to PostgreSQL..."

DATABASE=${1:-logistics_postgres}

echo "Connecting to database: $DATABASE"
echo "Username: logistics_user"
echo ""
echo "Type '\q' to exit"
echo "Type '\l' to list databases"
echo "Type '\dt' to list tables"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"

docker compose -f "$COMPOSE_FILE" exec postgres psql -U logistics_user -d "$DATABASE"