### **4. `config-server/README.md`**
```markdown
# Config Server

Centralized configuration management for the Logistics Platform microservices.

## 🎯 Purpose
Provides externalized configuration management for all microservices, allowing configuration to be stored in version control and served dynamically to services based on their profile and environment.

## ✨ Features
- **Centralized Configuration**: Single source of truth for all service configurations
- **Environment Specific**: Dev, staging, production profiles
- **Version Control**: Git-backed configuration with history
- **Encryption**: Sensitive data encryption/decryption
- **Dynamic Refresh**: Configuration updates without service restart
- **Security**: Authentication and authorization for config access
- **High Availability**: Multiple instances for redundancy

## 🏗️ Architecture
Config Server → Git Repository (config files)
→ Encryption Server (JCE/Vault)
→ Eureka Server (service discovery)
→ Services (config clients)

text

## 📁 Configuration Repository Structure
config-repo/
├── application.yml # Shared configuration
├── bootstrap.yml # Bootstrap configuration
├── auth-service.yml # Auth service specific
├── order-service.yml # Order service specific
├── dev/ # Development overrides
│ ├── application-dev.yml
│ ├── auth-service-dev.yml
│ └── order-service-dev.yml
├── staging/ # Staging overrides
│ ├── application-staging.yml
│ └── ...
└── prod/ # Production overrides
├── application-prod.yml
├── vault.yml # Vault integration
└── ...

text

## 📡 API Endpoints
GET /{application}/{profile}[/{label}] # Get configuration
POST /encrypt # Encrypt value
POST /decrypt # Decrypt value
GET /env # Environment info
POST /refresh # Refresh configuration
GET /health # Health check
GET /metrics # Metrics
POST /bus/refresh # Broadcast refresh
GET /actuator/* # Actuator endpoints

text

## ⚙️ Server Configuration
```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/logistics-config-repo
          default-label: main
          search-paths: '{application}'
          timeout: 10
          force-pull: true
        encrypt:
          enabled: true
          key: ${ENCRYPTION_KEY:default-encryption-key}
  security:
    user:
      name: config-user
      password: ${CONFIG_SERVER_PASSWORD}
🔒 Security Configuration
yaml
# Enable security
security:
  basic:
    enabled: true
  user:
    name: ${CONFIG_USER:admin}
    password: ${CONFIG_PASSWORD}
  
# JWT configuration (if using JWT)
jwt:
  secret: ${JWT_SECRET}
  expiration: 3600
  
# CORS configuration
cors:
  allowed-origins: "http://localhost:3000,http://localhost:8080"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
  allowed-headers: "*"
  allow-credentials: true
🔐 Encryption Setup
Generate Encryption Key
bash
# Generate a strong encryption key
keytool -genkeypair -alias config-server-key \
  -keyalg RSA -keysize 4096 \
  -sigalg SHA512withRSA \
  -dname "CN=Config Server,OU=Logistics Platform,O=Your Company,L=City,S=State,C=IN" \
  -keypass changeit -keystore config-server.jks \
  -storepass changeit -validity 3650

# Export public key
keytool -export -alias config-server-key \
  -file config-server-public.cer \
  -keystore config-server.jks \
  -storepass changeit
Encrypt/Decrypt Values
bash
# Using curl to encrypt
curl -X POST http://localhost:8888/encrypt \
  -H "Authorization: Basic $(echo -n 'admin:password' | base64)" \
  -d "sensitive-value"

# Using curl to decrypt
curl -X POST http://localhost:8888/decrypt \
  -H "Authorization: Basic $(echo -n 'admin:password' | base64)" \
  -d "encrypted-value"
🚀 Getting Started
Local Development
bash
# 1. Clone configuration repository
git clone https://github.com/your-org/logistics-config-repo

# 2. Start config server
cd config-server
mvn spring-boot:run

# 3. Verify server is running
curl http://localhost:8888/actuator/health
Docker Deployment
dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/config-server-*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
bash
# Build and run
docker build -t logistics/config-server:1.0.0 .
docker run -p 8888:8888 \
  -e CONFIG_REPO_URL=https://github.com/your-org/logistics-config-repo \
  -e ENCRYPTION_KEY=your-encryption-key \
  logistics/config-server:1.0.0
Kubernetes Deployment
yaml
# config-server-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
spec:
  replicas: 2
  selector:
    matchLabels:
      app: config-server
  template:
    metadata:
      labels:
        app: config-server
    spec:
      containers:
      - name: config-server
        image: logistics/config-server:1.0.0
        ports:
        - containerPort: 8888
        env:
        - name: CONFIG_REPO_URL
          valueFrom:
            secretKeyRef:
              name: config-secrets
              key: repo-url
        - name: ENCRYPTION_KEY
          valueFrom:
            secretKeyRef:
              name: config-secrets
              key: encryption-key
🔧 Client Configuration
Bootstrap Configuration
yaml
# bootstrap.yml in client services
spring:
  application:
    name: auth-service
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        multiplier: 1.3
        max-interval: 2000
        max-attempts: 6
      request-read-timeout: 5000
      request-connect-timeout: 2000
      username: ${CONFIG_USERNAME}
      password: ${CONFIG_PASSWORD}
Encrypted Properties
yaml
# In configuration repository
database:
  password: '{cipher}AQAB...encrypted-value...'
  
# Clients automatically decrypt
spring:
  datasource:
    password: ${database.password}
🔄 Dynamic Configuration Refresh
Manual Refresh
bash
# Refresh a specific service
curl -X POST http://service-host:port/actuator/refresh

# Refresh via config server bus
curl -X POST http://config-server:8888/actuator/bus-refresh
Automatic Refresh with Webhooks
yaml
# GitHub webhook configuration
webhook:
  github:
    enabled: true
    secret: ${WEBHOOK_SECRET}
    path: /monitor
📊 Monitoring
Health Indicators
Config Server Health: GET /actuator/health

Git Repository Health: Checks connectivity to Git repo

Encryption Health: Validates encryption/decryption

Disk Space: Monitors available disk space

Metrics
Configuration requests count

Encryption/decryption operations

Git fetch operations

Error rates

Response times

Logging
yaml
logging:
  level:
    org.springframework.cloud.config.server: DEBUG
    org.springframework.cloud.config.server.environment: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/config-server.log
    max-size: 10MB
    max-history: 30
🧪 Testing
Unit Tests
bash
mvn test
Integration Tests
bash
mvn verify -Pintegration
Configuration Validation
bash
# Validate configuration files
mvn validate-config
🔐 Security Best Practices
Use HTTPS: Always use HTTPS in production

Strong Authentication: Use JWT or OAuth2 for service-to-service auth

Network Security: Restrict access to trusted networks

Regular Key Rotation: Rotate encryption keys periodically

Audit Logging: Log all configuration access

Access Control: Role-based access to configuration

Secret Management: Integrate with HashiCorp Vault for secrets

📈 Scaling Considerations
Horizontal Scaling
yaml
# Multiple instances behind load balancer
config-server-1:8888
config-server-2:8888
config-server-3:8888
High Availability
Multiple config server instances

Load balanced access

Shared Git repository

Database-backed configuration (optional)

Performance Optimization
Enable caching

Use Git shallow clones

Implement connection pooling

Monitor and optimize memory usage

🚨 Troubleshooting
Common Issues
Git Connection Issues: Check network connectivity and credentials

Encryption Failures: Verify encryption key and JCE installation

Client Connection Issues: Check network and firewall rules

Memory Issues: Monitor heap usage and adjust JVM options

Log Analysis
bash
# Check server logs
tail -f logs/config-server.log

# Check health status
curl http://localhost:8888/actuator/health | jq .

# Check configuration
curl http://localhost:8888/auth-service/dev | jq .
🔧 Maintenance
Backup Strategy
bash
# Backup configuration repository
git clone --mirror https://github.com/your-org/logistics-config-repo

# Backup encryption keys
cp config-server.jks /backup/location/
Update Procedure
Update configuration in Git repository

Notify services to refresh configuration

Monitor for any issues

Rollback if necessary