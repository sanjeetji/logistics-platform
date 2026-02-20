# Deployment Guide (Logistic Platform)

## Strategy
Deploy the single `logistic_platform-app.jar` artifact. This replaces the complex microservices orchestration.

### Artifacts to Deploy
1.  **Application**: `logistic_platform-app/target/logistic_platform-app-1.0.0-SNAPSHOT.jar`
2.  **Docker Image**: `logistics-logistic_platform:latest`

## Deployment Options

### Option 1: Docker (Recommended)
Build and run the single container.

```bash
docker build -t logistics-logistic_platform -f logistic_platform-app/Dockerfile .
docker run -p 8080:8080 --env-file .env logistics-logistic_platform
```

### Option 2: Kubernetes (Simplified)
You only need **one** Deployment manifest now, instead of 20+.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: logistics-logistic_platform
spec:
  replicas: 2
  selector:
    matchLabels:
      app: logistics-logistic_platform
  template:
    metadata:
      labels:
        app: logistics-logistic_platform
    spec:
      containers:
      - name: logistic_platform
        image: logistics-logistic_platform:latest
        ports:
        - containerPort: 8080
        env:
        - name: PROFILES_ACTIVE
          value: "prod"
```

## Infrastructure Requirements
*   **Database**: PostgreSQL 15+
*   **Redis**: 7+
*   **Kafka**: 3.5+
*   **MinIO / S3**: For object storage

## CI/CD Pipeline
1.  **Build**: `mvn clean package -DskipTests`
2.  **Test**: `mvn test`
3.  **Docker Build**: `docker build ...`
4.  **Push**: Push image to registry.
5.  **Deploy**: Update K8s deployment or restart Docker container.
