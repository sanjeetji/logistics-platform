#!/bin/bash
echo "💾 Creating PostgreSQL backup..."

BACKUP_DIR="./backups/postgres"
BACKUP_FILE="logistics_backup_$(date +%Y%m%d_%H%M%S).sql"

mkdir -p $BACKUP_DIR

# Backup all databases
docker-compose exec -T postgres-db pg_dumpall -U logistics_user > "$BACKUP_DIR/$BACKUP_FILE"

# Compress the backup
gzip "$BACKUP_DIR/$BACKUP_FILE"

echo "✅ Backup created: $BACKUP_DIR/$BACKUP_FILE.gz"
echo "📦 Size: $(du -h "$BACKUP_DIR/$BACKUP_FILE.gz" | cut -f1)"