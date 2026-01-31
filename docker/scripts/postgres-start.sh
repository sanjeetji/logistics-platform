#!/bin/bash
echo "🐘 Starting PostgreSQL and pgAdmin..."

# Start only PostgreSQL and pgAdmin services
docker-compose up -d postgres-db pgadmin

echo ""
echo "✅ PostgreSQL & pgAdmin started successfully!"
echo ""
echo "📊 Access Details:"
echo "================="
echo "PostgreSQL Host:     localhost:5432"
echo "PostgreSQL Username: logistics_user"
echo "PostgreSQL Password: logistics_pass"
echo "PostgreSQL Database: logistics_postgres"
echo ""
echo "🖥️  pgAdmin Access:"
echo "================="
echo "URL:      http://localhost:5050"
echo "Email:    admin@logistics.com"
echo "Password: admin123"
echo ""
echo "💡 Quick Commands:"
echo "Connect via psql: docker-compose exec postgres-db psql -U logistics_user -d logistics_postgres"
echo "View logs:        docker-compose logs -f postgres-db"
echo "Backup:           ./scripts/postgres-backup.sh"