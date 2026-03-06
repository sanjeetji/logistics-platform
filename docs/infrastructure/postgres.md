# 🐘 PostgreSQL & PostGIS

## Overview
PostgreSQL is the primary relational database for the Logistics Platform. We use the **PostGIS** extension to support spatial data and geographic queries.

## Requirements
- **Image**: `postgis/postgis:15-3.3-alpine`
- **Volume**: `postgres-data` (Persistent)
- **Memory**: ~256MB - 512MB (Idle)

## Features & Usage
- **Spatial Queries**: Used by `geo-service` and `tracking-module` to calculate distances, check geofences, and store GPS coordinates.
- **Multi-tenancy**: Data is partitioned using a `tenant_id` column on most entities.
- **Liquibase**: (Optional) Schema migrations are managed via Liquibase or Hibernate ddl-auto.

## Access Details
### Inside Docker Network
- **Host**: `postgres`
- **Port**: `5432`

### From Host Machine (Mac/Windows)
- **Host**: `localhost`
- **Port**: `5432`

### Credentials
- **DB Name**: `logistics_postgres`
- **User**: `logistics_user`
- **Password**: `logistics_pass`

## Example Connection String
`jdbc:postgresql://localhost:5432/logistics_postgres`
