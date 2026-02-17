#!/bin/bash
echo "🔧 Fixing pgAdmin Connection Issues..."

echo "1. Stopping pgAdmin..."
docker-compose stop pgadmin

echo "2. Checking PostgreSQL connection..."
if docker-compose exec postgres-db pg_isready -U logistics_user; then
    echo "✅ PostgreSQL is accessible"
else
    echo "❌ Cannot connect to PostgreSQL"
    echo "   Starting PostgreSQL..."
    docker-compose up -d postgres-db
    sleep 5
fi

echo "3. Checking network connectivity..."
docker-compose exec pgadmin ping -c 2 postgres-db

echo "4. Creating new pgpass file..."
cat > docker/pgadmin/pgpass << EOF
postgres-db:5432:*:logistics_user:logistics_pass
localhost:5432:*:postgres:logistics_pass
172.17.0.1:5432:*:logistics_user:logistics_pass
host.docker.internal:5432:*:logistics_user:logistics_pass
EOF

chmod 600 docker/pgadmin/pgpass

echo "5. Starting pgAdmin..."
docker-compose up -d pgadmin

echo ""
echo "📊 Connection Test Commands:"
echo "============================"
echo "From pgAdmin container:"
echo "  docker-compose exec pgadmin ping postgres-db"
echo ""
echo "From PostgreSQL container:"
echo "  docker-compose exec postgres-db psql -U logistics_user -d logistics_postgres"
echo ""
echo "Direct connection test:"
echo "  PGPASSWORD=logistics_pass psql -h localhost -p 5432 -U logistics_user -d logistics_postgres"
echo ""
echo "🖥️  pgAdmin URL: http://localhost:5050"
echo "📧 Email: admin@logistics.com"
echo "🔑 Password: admin123"
echo ""
echo "Try these hostnames in pgAdmin:"
echo "1. postgres-db (Recommended)"
echo "2. host.docker.internal"
echo "3. 172.17.0.1"
echo "4. localhost"