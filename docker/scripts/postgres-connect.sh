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

docker-compose exec postgres-db psql -U logistics_user -d "$DATABASE"