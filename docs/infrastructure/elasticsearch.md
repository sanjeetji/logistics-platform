# 🔍 Elasticsearch

## Overview
Elasticsearch provides full-text search capabilities for the platform.

## Requirements
- **Image**: `docker.elastic.co/elasticsearch/elasticsearch:8.12.0`
- **Memory**: 512MB RAM minimum (configured in docker-compose).
- **Disk System**: Requires `vm.max_map_count` to be correctly set on Linux hosts (handled by Docker Desktop automatically on Mac).

## Features
- **Global Search**: Search across Orders, Customers, and Shipments with fuzzy matching.
- **Analytics**: provides backing for the metrics dashboard.

## Access
- **Endpoint**: `http://localhost:9200`
- **Security**: Basic Auth is disabled for local dev (`xpack.security.enabled=false`).

## Indices
- `orders`: Indexed order history.
- `shipments`: Real-time shipment data.
