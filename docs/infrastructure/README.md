# 🏗️ Infrastructure Knowledge Base

Welcome to the Logistics Platform Infrastructure documentation. This directory contains detailed guides for each service used in our stack.

## 📋 Service Index

1. [**PostgreSQL & PostGIS**](./postgres.md) - Relational & Spatial Database.
2. [**Kafka & Zookeeper**](./kafka.md) - Event Streaming & Orchestration.
3. [**Redis**](./redis.md) - Caching & Rate Limiting.
4. [**Elasticsearch**](./elasticsearch.md) - Full-text Search.
5. [**MinIO**](./minio.md) - S3 Object Storage for Documents.
6. [**pgAdmin**](../pgadmin-connectivity.md) - Database Administration UI.
7. [**SMTP (MailHog)**](./smtp.md) - Email Mocking & Production Setup.

## 🛠️ General Management

### Startup
The entire stack is managed via Docker Compose:
```bash
# Start all services
./docker/scripts/run-platform.sh start
```

### Health Monitoring
We use Spring Boot Actuator for health aggregation:
- **Local URL**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

> [!NOTE]
> If the root status is `DOWN`, check the individual components. Usually, it's the `mail` component because SMTP credentials aren't set in local development.
