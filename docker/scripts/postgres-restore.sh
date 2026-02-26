SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"
PROJECT_ROOT="$SCRIPT_DIR/../.."

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Please provide backup file path"
    echo "Usage: ./docker/scripts/postgres-restore.sh <backup_file.sql.gz>"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "⚠️  WARNING: This will STOP the platform and OVERWRITE current database!"
read -p "Are you sure? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Restore cancelled."
    exit 1
fi

echo "Restoring from $BACKUP_FILE..."

# Stop all services to ensure no active connections
docker compose -f "$COMPOSE_FILE" down

# Start only PostgreSQL
docker compose -f "$COMPOSE_FILE" up -d postgres
echo "Waiting for database to be ready..."
sleep 10

# Restore the database
gunzip -c "$BACKUP_FILE" | docker compose -f "$COMPOSE_FILE" exec -T postgres psql -U logistics_user

echo "✅ Database restored successfully!"
echo "🚀 You can now start the platform: ./docker/scripts/run-platform.sh start"