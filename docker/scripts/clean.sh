#!/bin/bash
echo "🧹 Cleaning Docker environment..."

# Stop and remove containers
docker-compose down -v

# Remove all logistics images
docker rmi $(docker images "logistics-*" -q) 2>/dev/null || true

# Remove dangling images
docker image prune -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f

echo "✅ Cleanup complete!"