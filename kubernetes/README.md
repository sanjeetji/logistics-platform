# Kubernetes Configuration

Kubernetes manifests and configurations for deploying the Logistics Platform.

## 🎯 Purpose
Contains all Kubernetes manifests, Helm charts, and configuration files for deploying the Logistics Platform on Kubernetes clusters in development, staging, and production environments.

## 📁 Directory Structure
kubernetes/
├── manifests/ # Kubernetes manifests
│ ├── namespaces/
│ │ ├── dev-namespace.yaml
│ │ ├── staging-namespace.yaml
│ │ └── prod-namespace.yaml
│ ├── deployments/
│ │ ├── auth-service.yaml
│ │ ├── order-service.yaml
│ │ └── ...
│ ├── services/
│ │ ├── auth-service.yaml
│ │ ├── order-service.yaml
│ │ └── ...
│ ├── ingress/
│ │ ├── dev-ingress.yaml
│ │ ├── staging-ingress.yaml
│ │ └── prod-ingress.yaml
│ └── configmaps/
│ ├── common-config.yaml
│ └── env-specific/
├── helm/ # Helm charts
│ ├── logistics-platform/
│ │ ├── Chart.yaml
│ │ ├── values.yaml
│ │ ├── templates/
│ │ └── charts/
│ └── dependencies/
├── monitoring/ # Monitoring stack
│ ├── prometheus/
│ ├── grafana/
│ └── alertmanager/
├── secrets/ # Secret management
│ ├── dev/
│ ├── staging/
│ └── prod/
├── scripts/ # Kubernetes scripts
│ ├── deploy-all.sh
│ ├── rollout-restart.sh
│ └── cleanup.sh
└── terraform/ # Infrastructure as Code
├── main.tf
├── variables.tf
└── outputs.tf

text

## ☸️ Kubernetes Manifests

### Namespace Configuration
```yaml
# kubernetes/manifests/namespaces/dev-namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: logistics-dev
  labels:
    name: logistics-dev
    environment: development
    managed-by: kubernetes
Deployment Example
yaml
# kubernetes/manifests/deployments/auth-service.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
  namespace: logistics-dev
  labels:
    app: auth-service
    tier: backend
    component: authentication
spec:
  replicas: 2
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
        version: v1.0.0
    spec:
      containers:
      - name: auth-service
        image: logistics/auth-service:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8081
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "dev"
        - name: DB_URL
          valueFrom:
            configMapKeyRef:
              name: auth-config
              key: db.url
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: auth-secrets
              key: jwt-secret
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 5
Service Configuration
yaml
# kubernetes/manifests/services/auth-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: auth-service
  namespace: logistics-dev
spec:
  selector:
    app: auth-service
  ports:
  - port: 80
    targetPort: 8081
    protocol: TCP
    name: http
  type: ClusterIP