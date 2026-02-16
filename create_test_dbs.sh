#!/bin/bash
set -e

# Database names
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
    "user_service_test"
    "customer_app_service_test"
    "driver_app_service_test"
    "user_management_service_test"
    "tracking_service_test"
)

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for psql and createdb
if ! command_exists psql; then
    # Try to find psql in common locations
    COMMON_PATHS=(
        "/opt/homebrew/bin"
        "/usr/local/bin"
        "/Library/PostgreSQL/16/bin"
        "/Library/PostgreSQL/15/bin"
        "/Library/PostgreSQL/14/bin"
        "/Applications/Postgres.app/Contents/Versions/latest/bin"
    )
    
    FOUND=false
    for path in "${COMMON_PATHS[@]}"; do
        if [ -x "$path/psql" ]; then
            export PATH="$path:$PATH"
            echo "Found PostgreSQL tools at $path"
            FOUND=true
            break
        fi
    done
    
    if [ "$FOUND" = false ]; then
        echo "Error: psql command not found. Please install PostgreSQL or ensure it is in your PATH."
        exit 1
    fi
fi

# User to run psql command (defaults to postgres if not set)
PG_USER=${PG_USER:-postgres}

echo "Creating test databases..."

for db in "${DBS[@]}"; do
    if psql -U "$PG_USER" -lqt | cut -d \| -f 1 | grep -qw "$db"; then
        echo "Database $db already exists."
    else
        echo "Creating database $db..."
        createdb -U "$PG_USER" "$db"
    fi
done

echo "All test databases created successfully."
