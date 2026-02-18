#!/bin/bash
echo "🛑 Stopping Logistics Platform..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform down

echo "✅ All services stopped."