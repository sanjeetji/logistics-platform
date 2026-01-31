#!/bin/bash
echo "🔨 Rebuilding Logistics Platform..."

# Stop services
docker-compose down

# Remove old images
docker rmi $(docker images "logistics-*" -q) 2>/dev/null || true

# Build and start
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

echo "✅ Services rebuilt and started."