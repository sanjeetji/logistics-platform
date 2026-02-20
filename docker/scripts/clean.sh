#!/bin/bash
echo "🧹 Cleaning Docker environment..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Stop and remove containers
# Stop and remove containers via compose
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" down -v 2>/dev/null || true

# Force remove specific containers (in case of project name mismatch)
docker rm -f logistics-postgres logistics-kafka logistics-zookeeper logistics-redis logistics-minio logistics-app 2>/dev/null || true

# Remove all logistics images
docker rmi $(docker images "logistics-*" -q) 2>/dev/null || true

# Remove dangling images
docker image prune -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f

echo "✅ Cleanup complete!"