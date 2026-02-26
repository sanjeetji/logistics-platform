#!/bin/bash
set -e

# =============================================================================
# Create Test Databases in the running PostgreSQL container.
# Usage: ./docker/scripts/create_test_dbs.sh
# Requires: PostgreSQL container (logistics-postgres) must be running.
# =============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/../docker-compose.yml"

# Database names (service test DBs)
DBS=(
    "tenant_service_test"
    "auth_service_test"
    "user_service_test"
    "fleet_service_test"
    "order_service_test"
    "dispatch_service_test"
    "role_permission_service_test"
    "customer_service_test"
    "route_optimization_service_test"
    "payment_service_test"
    "notification_service_test"
    "returns_service_test"
    "customer_app_service_test"
    "driver_app_service_test"
    "user_management_service_test"
    "tracking_service_test"
)

# Verify postgres container is reachable
if ! docker compose -f "$COMPOSE_FILE" exec postgres pg_isready -U logistics_user > /dev/null 2>&1; then
    echo "[ERROR] PostgreSQL container is not running or not ready."
    echo "        Start the platform first: ./docker/scripts/run-platform.sh start"
    exit 1
fi

echo "Creating test databases in logistics-postgres container..."

for db in "${DBS[@]}"; do
    RESULT=$(docker compose -f "$COMPOSE_FILE" exec postgres psql -U logistics_user -tAc \
        "SELECT 1 FROM pg_database WHERE datname='$db'" 2>/dev/null)
    if [ "$RESULT" == "1" ]; then
        echo "  [SKIP] $db (already exists)"
    else
        docker compose -f "$COMPOSE_FILE" exec postgres psql -U logistics_user -c "CREATE DATABASE $db;" > /dev/null
        echo "  [OK]   $db created"
    fi
done

echo ""
echo "All test databases ready."
