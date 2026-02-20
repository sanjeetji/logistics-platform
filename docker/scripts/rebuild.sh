#!/bin/bash
echo "🔨 Rebuilding Logistics Platform..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
PROJECT_ROOT="$SCRIPT_DIR/../.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
COMPOSE_DEV_FILE="$SCRIPT_DIR/../docker-compose.dev.yml"

# Stop services
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform down

# Remove old images
docker rmi $(docker images "logistics-*" -q) 2>/dev/null || true

# Build and start
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform up -d --build

echo "✅ Services rebuilt and started."