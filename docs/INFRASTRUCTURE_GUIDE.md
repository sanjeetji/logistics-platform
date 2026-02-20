# 🏗️ Infrastructure Guide

This document provides a detailed overview of the infrastructure services used by the Logistics Platform, including their purpose, configuration, and access details.

## 🗄️ 1. PostgreSQL (with PostGIS)
- **Image**: `postgis/postgis:15-3.3-alpine`
- **Purpose**: Primary relational database for all modules. PostGIS is used for spatial data (geofencing, driver locations, route calculation).
- **Access**:
  - **Host (Inside Docker)**: `postgres`
  - **Host (External)**: `localhost`
  - **Port**: `5432`
  - **Database**: `logistics_postgres`
  - **Username**: `logistics_user`
  - **Password**: `logistics_pass`
- **Features**: PostGIS spatial queries, ACID transactions, JPA integration.

## 🧠 2. Redis
- **Image**: `redis:7-alpine`
- **Purpose**: High-performance caching, session management, and rate limiting.
- **Access**:
  - **Host (Inside Docker)**: `redis`
  - **Host (External)**: `localhost`
  - **Port**: `6379`
- **Features**: Pub/Sub, key-value storage, persistency (AOF enabled).

## 📨 3. Kafka & Zookeeper
- **Images**: `confluentinc/cp-kafka:7.5.0` & `cp-zookeeper:7.5.0`
- **Purpose**: Distributed event streaming platform for the Saga pattern and tracking events.
- **Access**:
  - **Host (Inside Docker)**: `kafka:29092`
  - **Host (External)**: `localhost:9092`
- **Features**: Kafka Streams (used for heatmap calculation), persistent topics, high throughput.

## 🔍 4. Elasticsearch
- **Image**: `docker.elastic.co/elasticsearch/elasticsearch:8.12.0`
- **Purpose**: Full-text search engine for searching across orders, shipments, and customer data.
- **Access**:
  - **Host (Inside Docker)**: `elasticsearch`
  - **Host (External)**: `localhost:9200`
- **Features**: Near real-time search, clustering, REST API.

## 📦 5. MinIO (Object Storage)
- **Image**: `minio/minio:latest`
- **Purpose**: S3-compatible storage for uploading and serving documents (IDs, proof of delivery).
- **Access**:
  - **API Port**: `9000`
  - **Console UI Ports**: [http://localhost:9001](http://localhost:9001)
  - **Root User**: `minioadmin`
  - **Root Password**: `minioadmin`
- **Features**: Data redundancy, bucket management, S3 API compatibility.

## 🛠️ 6. pgAdmin (Database Management)
- **Image**: `dpage/pgadmin4:latest`
- **Purpose**: Web explorer for the PostgreSQL database.
- **Access**: [http://localhost:5050](http://localhost:5050)
  - **Email**: `admin@logistics.com`
  - **Password**: `admin123`

---

## 🚀 Requirement & Startup
The platform orchestrates these services via **Docker Compose**. 
- **Minimum RAM**: 8GB recommended (Elasticsearch and Kafka are resource-heavy).
- **Startup Script**: `docker/scripts/run-platform.sh start`
- **Health Verification**: `docker/scripts/health-check.sh`
