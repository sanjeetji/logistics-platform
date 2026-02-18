#!/bin/bash
echo "🧹 Cleaning Docker environment..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Stop and remove containers
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform down -v

# Remove all logistics images
docker rmi $(docker images "logistics-*" -q) 2>/dev/null || true

# Remove dangling images
docker image prune -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f

echo "✅ Cleanup complete!"