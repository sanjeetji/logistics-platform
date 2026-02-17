#!/bin/bash
echo "🛑 Stopping PostgreSQL and pgAdmin..."

docker-compose stop postgres-db pgadmin

echo "✅ PostgreSQL & pgAdmin stopped."