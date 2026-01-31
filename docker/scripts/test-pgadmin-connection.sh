#!/bin/bash
echo "🔌 Testing PostgreSQL Connections..."

echo ""
echo "1. Testing from Host Machine:"
echo "=============================="
if command -v psql &> /dev/null; then
    if PGPASSWORD=logistics_pass psql -h localhost -p 5432 -U logistics_user -d logistics_postgres -c "SELECT version();" &> /dev/null; then
        echo "✅ Host → PostgreSQL: SUCCESS"
    else
        echo "❌ Host → PostgreSQL: FAILED"
    fi
else
    echo "⚠️  psql not installed on host"
fi

echo ""
echo "2. Testing from Docker Network:"
echo "==============================="
if docker-compose exec postgres-db pg_isready -U logistics_user &> /dev/null; then
    echo "✅ PostgreSQL internal: SUCCESS"
else
    echo "❌ PostgreSQL internal: FAILED"
fi

echo ""
echo "3. Testing pgAdmin → PostgreSQL:"
echo "================================"
if docker-compose exec pgadmin curl -s http://postgres-db:5432 &> /dev/null; then
    echo "✅ pgAdmin → PostgreSQL: NETWORK ACCESSIBLE"
else
    echo "❌ pgAdmin → PostgreSQL: NETWORK BLOCKED"
fi

echo ""
echo "4. Testing Different Hostnames:"
echo "================================"
hostnames=("postgres-db" "localhost" "host.docker.internal" "172.17.0.1")

for host in "${hostnames[@]}"; do
    echo -n "   Testing $host:5432 ... "
    if docker-compose exec pgadmin nc -z -w2 $host 5432 &> /dev/null; then
        echo "✅ REACHABLE"
    else
        echo "❌ UNREACHABLE"
    fi
done

echo ""
echo "💡 Solution:"
echo "==========="
echo "In pgAdmin, use 'postgres-db' as hostname"
echo "If that fails, try these steps:"
echo "1. Run: ./scripts/fix-pgadmin.sh"
echo "2. Check if containers are in same network: docker network ls"
echo "3. Check PostgreSQL logs: docker-compose logs postgres-db"
echo "4. Check pgAdmin logs: docker-compose logs pgadmin"