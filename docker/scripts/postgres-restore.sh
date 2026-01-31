#!/bin/bash
echo "🔄 Restoring PostgreSQL database..."

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Please provide backup file path"
    echo "Usage: ./scripts/postgres-restore.sh <backup_file.sql.gz>"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "⚠️  WARNING: This will overwrite current database!"
read -p "Are you sure? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Restore cancelled."
    exit 1
fi

echo "Restoring from $BACKUP_FILE..."

# Stop applications using PostgreSQL first
docker-compose stop platform-core b2b-engine b2c-engine

# Restore the database
gunzip -c "$BACKUP_FILE" | docker-compose exec -T postgres-db psql -U logistics_user

# Restart applications
docker-compose start platform-core b2b-engine b2c-engine

echo "✅ Database restored successfully!"