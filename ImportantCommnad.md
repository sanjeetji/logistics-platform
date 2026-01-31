📋 Comprehensive Command Reference Guide
1. PROJECT SETUP & REPOSITORY MANAGEMENT
   bash
# Clone all repositories for the platform
git clone https://github.com/yourorg/logistics-backend.git
git clone https://github.com/yourorg/logistics-mobile.git
git clone https://github.com/yourorg/logistics-web.git
git clone https://github.com/yourorg/logistics-shared.git

# Initialize a multi-module project (if starting from scratch)
git init logistics-platform
cd logistics-platform
git submodule add https://github.com/yourorg/logistics-backend.git backend
git submodule add https://github.com/yourorg/logistics-mobile.git mobile
git submodule add https://github.com/yourorg/logistics-web.git web
git submodule add https://github.com/yourorg/logistics-shared.git shared

# Update all submodules
git submodule update --init --recursive

# Pull latest from all repositories
find . -name ".git" -type d | sed 's/.git//' | xargs -I {} git -C {} pull
2. MAVEN BUILD COMMANDS
   bash
# Clean install all services (parent POM)
cd logistics-backend
mvn clean install -DskipTests

# Build specific module
cd logistics-backend/platform-core/auth-service
mvn clean compile

# Build with specific Java version
mvn clean install -Djava.version=21

# Skip tests for faster builds
mvn clean install -DskipTests

# Run only unit tests
mvn test

# Run integration tests
mvn verify -Pintegration-test

# Generate dependency tree (useful for debugging conflicts)
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates

# Build with different profiles
mvn clean install -Pdev,local
mvn clean install -Pprod,cloud

# Build with Docker image creation
mvn clean install dockerfile:build

# Create JAR without dependencies
mvn clean package -DskipTests

# Create executable JAR with dependencies
mvn clean package spring-boot:repackage
3. SPRING BOOT SPECIFIC COMMANDS
   bash
# Run a Spring Boot application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run with custom JVM arguments
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m"

# Enable debug mode for remote debugging
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Build Docker image using Spring Boot
mvn spring-boot:build-image

# Build with native image (GraalVM)
mvn spring-boot:build-image -Dspring-boot.build-image.builder=paketobuildpacks/builder:tiny

# Generate build information
mvn spring-boot:build-info

# Generate Actuator endpoint mappings
mvn spring-boot:build-info spring-boot:start spring-boot:stop
4. INFRASTRUCTURE SERVICES MANAGEMENT
   bash
# Start all infrastructure services using Docker Compose
cd logistics-backend
docker-compose -f docker-compose-infra.yml up -d

# Start development environment
docker-compose -f docker-compose-dev.yml up -d

# View logs of infrastructure services
docker-compose -f docker-compose-infra.yml logs -f
docker-compose logs -f config-server service-discovery gateway

# Stop infrastructure services
docker-compose -f docker-compose-infra.yml down

# Stop and remove volumes (clean state)
docker-compose -f docker-compose-infra.yml down -v

# Check service status
docker-compose -f docker-compose-infra.yml ps

# Scale specific services
docker-compose -f docker-compose-infra.yml up --scale config-server=2

# Infrastructure service-specific commands
# Config Server
cd infrastructure/config-server
mvn spring-boot:run -Dspring-boot.run.profiles=native

# Eureka Server
cd infrastructure/service-discovery
mvn spring-boot:run

# API Gateway
cd infrastructure/gateway-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
5. RUNNING INDIVIDUAL MICROSERVICES
   bash
# Pattern for running any microservice
cd <service-directory>
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

# Example: Auth Service
cd platform-core/auth-service
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

# Example: Order Service
cd b2b-engine/order-service
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

# Example: User Service
cd platform-core/user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dserver.port=8095

# With custom properties
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --spring.profiles.active=dev"

# With environment variables
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Run with debug output
mvn spring-boot:run -Ddebug

# Run in background
nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > auth-service.log 2>&1 &
6. RUNNING ALL MICROSERVICES TOGETHER
   bash
# Method 1: Using Docker Compose (Recommended)
cd logistics-backend
docker-compose -f docker-compose-all-services.yml up -d

# Method 2: Using Maven from parent directory
cd logistics-backend
mvn clean install -DskipTests
mvn spring-boot:run -pl infrastructure/config-server -am &
mvn spring-boot:run -pl infrastructure/service-discovery -am &
mvn spring-boot:run -pl infrastructure/gateway-service -am &
mvn spring-boot:run -pl platform-core/auth-service -am &
# ... continue for all services

# Method 3: Using shell script
# Create run-all.sh:
#!/bin/bash
echo "Starting infrastructure services..."
cd infrastructure/config-server && mvn spring-boot:run &
cd infrastructure/service-discovery && mvn spring-boot:run &
cd infrastructure/gateway-service && mvn spring-boot:run &

sleep 30  # Wait for infrastructure to start

echo "Starting core services..."
cd platform-core/auth-service && mvn spring-boot:run &
cd platform-core/user-service && mvn spring-boot:run &
cd platform-core/tenant-service && mvn spring-boot:run &

# Make executable and run
chmod +x run-all.sh
./run-all.sh

# Method 4: Using Process Manager (PM2 for Node.js alternative for Java)
# Install jstart or use screen/tmux
7. DATABASE MIGRATION COMMANDS
   bash
# Using Liquibase
mvn liquibase:update
mvn liquibase:rollback -Dliquibase.rollbackCount=1
mvn liquibase:status
mvn liquibase:diff
mvn liquibase:generateChangeLog

# Using Flyway
mvn flyway:migrate
mvn flyway:clean
mvn flyway:info
mvn flyway:validate
mvn flyway:repair

# Generate migration scripts
mvn liquibase:diffChangeLog -Dliquibase.diffChangeLogFile=src/main/resources/db/changelog/changes/001-initial-schema.xml

# Rollback to specific tag
mvn liquibase:rollback -Dliquibase.rollbackTag=version1.0
8. DOCKER COMMANDS FOR MICROSERVICES
   bash
# Build Docker image for a service
cd platform-core/auth-service
docker build -t logistics/auth-service:1.0.0 .

# Build with Maven and Docker
mvn clean package dockerfile:build

# Push to container registry
docker tag logistics/auth-service:1.0.0 your-registry.com/logistics/auth-service:1.0.0
docker push your-registry.com/logistics/auth-service:1.0.0

# Run a service in Docker
docker run -d -p 8081:8081 \
-e SPRING_PROFILES_ACTIVE=dev \
-e CONFIG_SERVER_URL=http://config-server:8888 \
--name auth-service \
logistics/auth-service:1.0.0

# View container logs
docker logs -f auth-service

# Execute command in container
docker exec -it auth-service /bin/sh
docker exec auth-service java -version

# Check container resources
docker stats auth-service

# Stop and remove container
docker stop auth-service
docker rm auth-service

# Remove all unused containers, networks, images
docker system prune -a

# Build multi-platform images
docker buildx build --platform linux/amd64,linux/arm64 -t logistics/auth-service:1.0.0 .
9. KUBERNETES DEPLOYMENT COMMANDS
   bash
# Apply Kubernetes manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml

# Apply all files in directory
kubectl apply -f k8s/

# Check pod status
kubectl get pods -n logistics-platform
kubectl get pods -n logistics-platform -o wide
kubectl describe pod auth-service-7c5d8f9b6-abcde -n logistics-platform

# View logs
kubectl logs -f deployment/auth-service -n logistics-platform
kubectl logs -f auth-service-7c5d8f9b6-abcde -n logistics-platform --tail=100

# Port forwarding for local access
kubectl port-forward svc/auth-service 8081:8081 -n logistics-platform
kubectl port-forward svc/api-gateway 8080:8080 -n logistics-platform

# Scale deployments
kubectl scale deployment auth-service --replicas=3 -n logistics-platform
kubectl autoscale deployment auth-service --min=2 --max=10 --cpu-percent=80 -n logistics-platform

# Rolling updates
kubectl set image deployment/auth-service auth-service=logistics/auth-service:1.0.1 -n logistics-platform
kubectl rollout status deployment/auth-service -n logistics-platform
kubectl rollout undo deployment/auth-service -n logistics-platform

# Execute command in pod
kubectl exec -it auth-service-7c5d8f9b6-abcde -n logistics-platform -- /bin/sh
kubectl exec auth-service-7c5d8f9b6-abcde -n logistics-platform -- java -version

# Check service endpoints
kubectl get svc -n logistics-platform
kubectl describe svc auth-service -n logistics-platform

# Check ingress
kubectl get ingress -n logistics-platform
kubectl describe ingress logistics-ingress -n logistics-platform
10. HELM COMMANDS FOR DEPLOYMENT
    bash
# Install Helm chart
helm install logistics-platform ./charts/logistics-platform -n logistics-platform --create-namespace

# Upgrade release
helm upgrade logistics-platform ./charts/logistics-platform -n logistics-platform

# Rollback release
helm rollback logistics-platform 1 -n logistics-platform

# List releases
helm list -n logistics-platform

# Get release status
helm status logistics-platform -n logistics-platform

# Uninstall release
helm uninstall logistics-platform -n logistics-platform

# Package chart
helm package ./charts/logistics-platform

# Lint chart
helm lint ./charts/logistics-platform

# Dry run (simulate installation)
helm install logistics-platform ./charts/logistics-platform -n logistics-platform --dry-run --debug

# Values override
helm install logistics-platform ./charts/logistics-platform -n logistics-platform -f values-dev.yaml
helm upgrade logistics-platform ./charts/logistics-platform -n logistics-platform --set auth-service.replicaCount=3
11. MONITORING AND OBSERVABILITY COMMANDS
    bash
# Check Spring Boot Actuator endpoints
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/info
curl http://localhost:8081/actuator/metrics
curl http://localhost:8081/actuator/env
curl http://localhost:8081/actuator/beans
curl http://localhost:8081/actuator/mappings

# Check readiness and liveness
curl http://localhost:8081/actuator/health/readiness
curl http://localhost:8081/actuator/health/liveness

# Prometheus metrics
curl http://localhost:8081/actuator/prometheus

# Heap dump (for memory analysis)
curl http://localhost:8081/actuator/heapdump -o heapdump.hprof

# Thread dump
curl http://localhost:8081/actuator/threaddump

# Check Eureka dashboard
curl http://localhost:8761/eureka/apps

# Check service registration
curl http://localhost:8761/eureka/apps/AUTH-SERVICE
curl http://localhost:8761/eureka/apps/ORDER-SERVICE

# Config server check
curl http://localhost:8888/auth-service/dev
curl http://localhost:8888/order-service/prod
12. TESTING COMMANDS
    bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=OrderServiceIntegrationTest

# Run specific test method
mvn test -Dtest=AuthServiceTest#testUserAuthentication

# Run tests with coverage (Jacoco)
mvn clean test jacoco:report

# Run integration tests
mvn verify -Pintegration-test
mvn failsafe:integration-test

# Run contract tests (Pact)
mvn pact:verify
mvn pact:publish

# Run performance tests (JMeter)
jmeter -n -t src/test/jmeter/order-load-test.jmx -l results.jtl

# Run with specific test profile
mvn test -Dspring.profiles.active=test

# Generate test report
mvn surefire-report:report
mvn site -DgenerateReports=false

# Skip tests
mvn clean install -DskipTests
mvn clean install -DskipTests -DskipITs
13. DEBUGGING AND TROUBLESHOOTING COMMANDS
    bash
# Enable remote debugging
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Connect with debugger (from IDE on port 5005)

# Enable verbose logging
mvn spring-boot:run -Ddebug -Dlogging.level.org.springframework=DEBUG

# Check database connection
psql -h localhost -p 5432 -U postgres -d auth_db
\dt  # List tables
SELECT * FROM users;

# Check Redis
redis-cli
keys *
get auth:token:abc123

# Check Kafka topics
kafka-topics.sh --bootstrap-server localhost:9092 --list
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order.created --from-beginning

# Check network connectivity between services
curl -v http://auth-service:8081/actuator/health
telnet auth-service 8081

# Check DNS resolution
nslookup auth-service
dig auth-service

# Memory analysis with jcmd
jcmd <pid> GC.heap_info
jcmd <pid> Thread.print
jcmd <pid> VM.flags

# Heap analysis
jmap -heap <pid>
jmap -histo:live <pid> | head -20

# Thread analysis
jstack <pid> > threaddump.txt
14. CI/CD PIPELINE COMMANDS
    bash
# GitLab CI example commands
# .gitlab-ci.yml stages would execute these:

# Build stage
mvn clean compile -DskipTests

# Test stage
mvn test
mvn verify -Pintegration-test

# Security scan
mvn org.owasp:dependency-check-maven:check

# SonarQube analysis
mvn sonar:sonar -Dsonar.projectKey=logistics-platform

# Build Docker image
docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .

# Push to registry
docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

# Deploy to Kubernetes
kubectl set image deployment/auth-service auth-service=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA -n logistics-platform

# Smoke tests after deployment
curl -f http://auth-service.logistics-platform.svc.cluster.local:8081/actuator/health
15. UTILITY AND MAINTENANCE COMMANDS
    bash
# Find all Java processes
jps -l
ps aux | grep java

# Kill a Spring Boot application
kill $(lsof -t -i:8081)
kill -9 <pid>

# Check disk space for logs
du -sh /var/log/* | sort -rh

# Clear Maven cache
rm -rf ~/.m2/repository

# Update all dependencies
mvn versions:use-latest-versions
mvn versions:update-parent
mvn versions:update-properties

# Generate dependency license report
mvn license:add-third-party
mvn license:aggregate-add-third-party

# Generate project site
mvn site

# Generate OpenAPI/Swagger documentation
mvn springdoc-openapi:generate

# Check for vulnerable dependencies
mvn org.owasp:dependency-check-maven:check
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar

# Format code
mvn spotless:apply

# Check code style
mvn checkstyle:check
16. BATCH OPERATIONS FOR MULTIPLE SERVICES
    bash
# Build all services in parallel
cd logistics-backend
find . -name "pom.xml" -type f | grep -E "(platform-core|b2b-engine|b2c-engine)" | xargs -I {} dirname {} | xargs -I {} -P 4 bash -c "cd {} && mvn clean compile -DskipTests"

# Start multiple services in background
start_service() {
cd $1
mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/$1.log 2>&1 &
echo "Started $1 with PID $!"
}

start_service platform-core/auth-service
start_service platform-core/user-service
start_service b2b-engine/order-service
# ... continue for other services

# Stop all running Spring Boot applications
pkill -f "spring-boot:run"

# Check status of all services
for port in 8081 8082 8083 8084 8085; do
echo "Checking port $port..."
curl -s http://localhost:$port/actuator/health | jq '.status' || echo "Service on port $port not reachable"
done

# Update dependencies across all modules
find . -name "pom.xml" -type f | xargs -I {} mvn -f {} versions:update-properties
17. SNAPSHOT AND BACKUP COMMANDS
    bash
# Create database backup for a service
pg_dump -h localhost -p 5432 -U postgres auth_db > auth_db_backup_$(date +%Y%m%d).sql
pg_dump -h localhost -p 5432 -U postgres order_db > order_db_backup_$(date +%Y%m%d).sql

# Restore database
psql -h localhost -p 5432 -U postgres auth_db < auth_db_backup_20240115.sql

# Take application snapshot (Docker)
docker commit auth-service auth-service-snapshot:$(date +%Y%m%d)
docker save auth-service-snapshot:20240115 > auth-service-snapshot.tar

# Kubernetes resource backup
kubectl get all -n logistics-platform -o yaml > k8s-backup-$(date +%Y%m%d).yaml
kubectl get configmap -n logistics-platform -o yaml > configmaps-backup.yaml
kubectl get secret -n logistics-platform -o yaml > secrets-backup.yaml

# Config server configuration backup
cd logistics-backend/infrastructure/config-server
git add .
git commit -m "Config backup $(date)"
git push origin main

# Backup logs
tar -czf logs-backup-$(date +%Y%m%d).tar.gz /var/log/application/*.log
18. PERFORMANCE TESTING AND BENCHMARKING
    bash
# Load test with Apache Bench
ab -n 1000 -c 100 http://localhost:8080/api/v1/orders

# Load test with wrk
wrk -t4 -c100 -d30s http://localhost:8080/api/v1/orders

# JMeter load test
jmeter -n -t load-test.jmx -l results.jtl -e -o ./load-test-report

# Monitor JVM during load test
jstat -gc <pid> 1000 10  # GC statistics every second for 10 times

# Heap dump during performance test
jmap -dump:live,format=b,file=heapdump.hprof <pid>

# Monitor Kafka throughput
kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --group order-consumer --zookeeper localhost:2181

# Monitor database performance
pg_stat_activity view in PostgreSQL
19. SECURITY SCANNING AND HARDENING
    bash
# Dependency vulnerability check
mvn org.owasp:dependency-check-maven:check

# OWASP ZAP security scan (Docker)
docker run -v $(pwd):/zap/wrk -t owasp/zap2docker-stable zap-baseline.py \
-t http://localhost:8080 -g gen.conf -r testreport.html

# Check for exposed endpoints
nmap -sV -p 8080-8100 localhost

# SSL/TLS check
openssl s_client -connect localhost:8443 -servername localhost
nmap --script ssl-enum-ciphers -p 8443 localhost

# Check JWT token security
jwt_tool <JWT_TOKEN> -C -d /path/to/wordlist.txt

# Audit Kubernetes security
kubectl describe pod auth-service -n logistics-platform | grep -i security
kubectl get networkpolicy -n logistics-platform
20. DEVELOPMENT WORKFLOW COMMANDS
    bash
# Create feature branch and start development
git checkout -b feature/add-payment-service
git add .
git commit -m "feat: add payment service with Stripe integration"
git push origin feature/add-payment-service

# Rebase with main
git checkout main
git pull origin main
git checkout feature/add-payment-service
git rebase main

# Merge feature branch
git checkout main
git merge --no-ff feature/add-payment-service
git push origin main

# Create hotfix
git checkout -b hotfix/fix-order-bug main
# Fix the issue
git add .
git commit -m "fix: resolve order calculation bug"
git checkout main
git merge --no-ff hotfix/fix-order-bug
git tag -a v1.0.1 -m "Hotfix for order calculation"
git push origin main --tags

# Clean up branches
git branch -d feature/add-payment-service
git push origin --delete feature/add-payment-service
📊 Quick Reference Table: Essential Commands by Category
Category	Top 3 Essential Commands	Purpose
Building	mvn clean install -DskipTests
mvn spring-boot:run
mvn clean package	Build, run, package
Docker	docker-compose up -d
docker build -t service:tag .
docker logs -f container	Orchestrate, build, debug
Kubernetes	kubectl apply -f k8s/
kubectl logs -f pod
kubectl get pods	Deploy, monitor, check
Database	mvn liquibase:update
pg_dump db > backup.sql
psql -U user -d db	Migrate, backup, access
Testing	mvn test
mvn verify -Pintegration
mvn jacoco:report	Unit, integration, coverage
Debugging	curl /actuator/health
jcmd <pid> GC.heap_info
kubectl port-forward	Health check, memory, access
🚀 Quick Start Script for Your Logistics Platform
Create a start-platform.sh script:

bash
#!/bin/bash
# logistics-platform/start-platform.sh

echo "🚀 Starting Logistics Platform..."

# 1. Start Infrastructure
echo "Starting infrastructure services..."
docker-compose -f docker-compose-infra.yml up -d

# Wait for infrastructure
sleep 30

# 2. Build and start core services
echo "Building and starting core services..."
services=("auth-service" "user-service" "tenant-service" "order-service")
for service in "${services[@]}"; do
echo "Starting $service..."
cd logistics-backend
find . -name "$service" -type d | head -1 | xargs -I {} bash -c "cd {} && mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/${service}.log 2>&1 &"
sleep 5
done

echo "✅ Logistics platform starting..."
echo "📊 Monitor at:"
echo "   Config Server: http://localhost:8888"
echo "   Eureka: http://localhost:8761"
echo "   API Gateway: http://localhost:8080"
echo "   Auth Service: http://localhost:8081/actuator/health"
Make it executable: chmod +x start-platform.sh

💡 Pro Tips for Efficient Development
Use aliases in your ~/.bashrc or ~/.zshrc:

bash
alias ms='mvn spring-boot:run'
alias mci='mvn clean install -DskipTests'
alias k='kubectl'
alias kgp='kubectl get pods'
alias kaf='kubectl apply -f'
alias dcu='docker-compose up -d'
alias dcd='docker-compose down'
Use shell scripts for repetitive tasks

Use process manager like tmux or screen to run multiple services

Set up port forwarding for easy local development

Use Docker Compose override files for different environments

This comprehensive command guide covers the entire development lifecycle of your 
logistics platform microservices. Start with the basic building and running commands, 
then gradually incorporate the deployment and monitoring commands as your project matures.




### WINDOWS - Simple Commands
Display Variables
cmd
rem All environment variables
set

rem Show variable
echo %PATH%
echo %USERNAME%
echo %COMPUTERNAME%

rem Filter variables
set | findstr "PATH"
set | findstr /i "java"
Create/Modify Variables
cmd
rem Temporary (session only)
set MY_VAR=value
set PATH=%PATH%;C:\new\path

rem Permanent (user)
setx MY_VAR "value"

rem Permanent (system - admin)
setx MY_VAR "value" /M

rem Append to PATH permanently
setx PATH "%PATH%;C:\new\path"
Delete Variables
cmd
rem Clear temporary
set MY_VAR=

rem Delete permanent
setx MY_VAR ""
Check Variables
cmd
rem Check if exists
if defined JAVA_HOME (echo Set) else (echo Not set)

rem Check value
echo %PATH% | findstr /C:"Python"
PowerShell Commands
powershell
# All variables
Get-ChildItem Env:
$env:PATH

# Set temporary
$env:MY_VAR = "value"

# Set permanent (user)
[Environment]::SetEnvironmentVariable("MY_VAR", "value", "User")

# Set permanent (system)
[Environment]::SetEnvironmentVariable("MY_VAR", "value", "Machine")

# Get specific scope
[Environment]::GetEnvironmentVariable("PATH", "User")
macOS - Simple Commands
Display Variables
bash
# All environment variables
env
printenv

# All variables (including local)
set

# Show variable
echo $HOME
echo $USER
echo $PATH

# Filter variables
env | grep -i "java"
printenv | grep PATH
env | grep "HOME\|USER\|PATH"
Create/Modify Variables
bash
# Temporary (session only)
export MY_VAR=value
export PATH=$PATH:/new/path

# Permanent (add to ~/.zshrc)
echo 'export MY_VAR="value"' >> ~/.zshrc
Example: echo 'export NVD_API_KEY=your_actual_api_key_here' >> ~/.zshrc
echo 'export PATH="$PATH:/new/path"' >> ~/.zshrc

# Load changes
source ~/.zshrc

# System-wide (all users)
sudo launchctl setenv MY_VAR value
Delete Variables
bash
# Remove temporary
unset MY_VAR

# Remove from ~/.zshrc
sed -i '' '/export MY_VAR=/d' ~/.zshrc

# System-wide remove
sudo launchctl unsetenv MY_VAR
Check Variables
bash
# Check if exists
[ -z "$JAVA_HOME" ] && echo "Not set" || echo "Set to: $JAVA_HOME"

# Check value
echo $PATH | grep -q "python" && echo "Found" || echo "Not found"
Launchd (GUI Apps)
bash
# Set for all processes
sudo launchctl setenv PATH "$PATH"

# Get current
launchctl getenv PATH

# List all
launchctl export
LINUX - Simple Commands
Display Variables
bash
# All environment variables
env
printenv

# All variables (including local)
set

# Show variable
echo $HOME
echo $USER
echo $SHELL

# Filter variables
printenv | grep -i "path"
env | grep "HOME\|USER"
set | grep PS1
Create/Modify Variables
bash
# Temporary (session only)
export MY_VAR=value
export PATH=$PATH:/usr/local/bin

# Permanent (add to ~/.bashrc)
echo 'export MY_VAR="value"' >> ~/.bashrc
echo 'export PATH="$PATH:/new/path"' >> ~/.bashrc

# Load changes
source ~/.bashrc
. ~/.bashrc

# System-wide (all users)
sudo sh -c 'echo "MY_VAR=value" >> /etc/environment'
sudo tee /etc/profile.d/myapp.sh <<< 'export MY_VAR="value"'
Delete Variables
bash
# Remove temporary
unset MY_VAR

# Remove from ~/.bashrc
sed -i '/export MY_VAR=/d' ~/.bashrc

# System-wide remove
sudo sed -i '/MY_VAR=/d' /etc/environment
Check Variables
bash
# Check if exists
[ -z "$JAVA_HOME" ] && echo "Not set" || echo $JAVA_HOME

# Check with default
echo ${EDITOR:-"vim"}

# Check if in PATH
echo $PATH | tr ':' '\n' | grep -q "^/usr/bin$" && echo "Found"
Systemd (Services)
bash
# Set for all services
sudo systemctl set-environment MY_VAR=value

# Get current
systemctl show-environment

# Unset
sudo systemctl unset-environment MY_VAR
User vs System
bash
# User only
echo 'export VAR="user"' >> ~/.bashrc

# All users
sudo echo 'export VAR="all"' >> /etc/profile

# /etc/environment (no export)
sudo echo 'VAR=value' >> /etc/environment

# /etc/profile.d/ (with export)
sudo echo 'export VAR="value"' > /etc/profile.d/custom.sh

### How to use run-platform.sh
1. Start Locally (Development) This uses .env (defaults to dev profile) and docker/docker-compose.yml.
./run-platform.sh start
2. Stop Locally
./run-platform.sh stop
3. Restart Locally
./run-platform.sh restart
4. Build (No Tests) - Uses NVD key from .env
./run-platform.sh build
5. Build with Security Check (Slow)
./run-platform.sh build --secure
6. Deploy to Docker Compose (dev profile)
./run-platform.sh deploy
7. Deploy to Docker Compose (prod profile)
./run-platform.sh deploy --prod
8. Deploy to Kubernetes (dev profile)
./run-platform.sh deploy --k8s
9. Deploy to Kubernetes (prod profile)
./run-platform.sh deploy --k8s --prod
10. Run Specific Service (Local)
./run-platform.sh run auth-service
./run-platform.sh run user-service
11. Run Specific Service (Docker Compose)
./run-platform.sh run auth-service --docker
12. Run Specific Service (Kubernetes)
./run-platform.sh run auth-service --k8s
13. Help
./run-platform.sh help




Setup the NVD API key process in local and prod

1. Get API Key
Go to https://nvd.nist.gov/developers/request-an-api-key
Fill in the form
Copy the API key
2. Set in .env (Local)
Edit .env file:
bash
NVD_API_KEY=your_actual_api_key_here
3. Set in Docker (Production)
Edit docker-compose.yml:
yaml
environment:
  - NVD_API_KEY=${NVD_API_KEY:-your_actual_api_key_here}
4. Set in Kubernetes (Production)
Edit k8s/config/nvd-config.yaml:
yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: nvd-config
data:
  NVD_API_KEY: "your_actual_api_key_here"

  The NVD API key usage depends on whether you are Building or Running the application.

1. Building for Production (./run-platform.sh build --env=prod)
When you run the build command, the script loads 
.env.prod
. In 
.env.prod
, the key is defined as ${NVD_API_KEY}.

bash
# .env.prod
NVD_API_KEY=${NVD_API_KEY}
This syntax tells the script to look in your system environment variables for the value.

Locally: It works automatically because we added export NVD_API_KEY=... to your ~/.zshrc file earlier. The script picks it up from your terminal session.
CI/CD (Real Production): In a real deployment pipeline (like GitHub Actions, GitLab CI, or Jenkins), you would save the API Key as a Secure Secret in the project settings. The pipeline injects it as an environment variable, and the script picks it up the same way.
2. Running in Production (./run-platform.sh start --env=prod)
The NVD API Key is NOT required to run the application containers. It is only used by the Maven dependency-check plugin during the build process to scan for vulnerabilities. Once the Docker images are built, the key is no longer needed.

Summary
You don't need to do anything! Since you added the key to your ~/.zshrc, it will automatically be "filled in" when using 
.env.prod
 locally.

If you ever run this on a separate production server:

Option A (Recommended): Set the variable in the server's environment: export NVD_API_KEY=key_here.
Option B: You could hardcode it in 
.env.prod
 like you did in 
.env
, but it is better security practice to keep secrets out of production config files