#!/bin/bash
echo "💾 Creating PostgreSQL backup..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

BACKUP_DIR="$PROJECT_ROOT/backups/postgres"
BACKUP_FILE="logistics_backup_$(date +%Y%m%d_%H%M%S).sql"

mkdir -p $BACKUP_DIR

# Backup all databases
docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform exec -T postgres-db pg_dumpall -U logistics_user > "$BACKUP_DIR/$BACKUP_FILE"

# Compress the backup
gzip "$BACKUP_DIR/$BACKUP_FILE"

echo "✅ Backup created: $BACKUP_DIR/$BACKUP_FILE.gz"
echo "📦 Size: $(du -h "$BACKUP_DIR/$BACKUP_FILE.gz" | cut -f1)"