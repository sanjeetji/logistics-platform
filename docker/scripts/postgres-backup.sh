#!/bin/bash
echo "💾 Creating PostgreSQL backup..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

BACKUP_DIR="$PROJECT_ROOT/backups/postgres"
BACKUP_FILE="logistics_backup_$(date +%Y%m%d_%H%M%S).sql"

mkdir -p "$BACKUP_DIR"

# Backup all databases using correct container name (postgres, not postgres-db)
docker compose -f "$COMPOSE_FILE" exec -T postgres pg_dumpall -U logistics_user > "$BACKUP_DIR/$BACKUP_FILE"

# Compress the backup
gzip "$BACKUP_DIR/$BACKUP_FILE"

echo "✅ Backup created: $BACKUP_DIR/$BACKUP_FILE.gz"
echo "📦 Size: $(du -h "$BACKUP_DIR/$BACKUP_FILE.gz" | cut -f1)"