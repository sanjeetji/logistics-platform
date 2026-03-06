# 🧠 Redis Cache

## Overview
Redis is used as a distributed cache and for high-speed data structures.

## Usage in Platform
- **Caching**: Stores frequently accessed data like `Tenant` configurations and `Preferences`.
- **Session Management**: (Future) Storing authenticated user sessions.
- **Rate Limiting**: Protecting API endpoints from abuse.

## Access
- **Host**: `redis` (Internal) / `localhost` (External)
- **Port**: `6379`
- **Password**: None (Default config)

## Monitoring
You can connect via `redis-cli`:
```bash
docker exec -it logistics-redis redis-cli
```
Command to check health: `PING` -> Response: `PONG`
