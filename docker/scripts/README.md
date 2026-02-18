### **8. `scripts/README.md`**
```markdown
# Scripts Directory

Utility scripts for building, deploying, and managing the Logistics Platform.

## 🎯 Purpose
Contains all utility scripts for development, testing, deployment, and maintenance of the Logistics Platform.

## 📁 Directory Structure
scripts/
├── build/ # Build scripts
│ ├── build-all.sh
│ ├── build-service.sh
│ └── package.sh
├── deploy/ # Deployment scripts
│ ├── deploy-dev.sh
│ ├── deploy-prod.sh
│ └── rollback.sh
├── database/ # Database scripts
│ ├── migrate.sh
│ ├── backup.sh
│ └── restore.sh
├── monitoring/ # Monitoring scripts
│ ├── health-check.sh
│ ├── log-analyzer.sh
│ └── metrics-collector.sh
├── security/ # Security scripts
│ ├── generate-keys.sh
│ ├── ssl-renew.sh
│ └── security-scan.sh
├── cleanup/ # Cleanup scripts
│ ├── clean-docker.sh
│ ├── clean-maven.sh
│ └── clean-logs.sh
└── utils/ # Utility scripts
├── env-setup.sh
├── service-check.sh
└── version-check.sh

text

## 🔧 Build Scripts

### Build All Services
```bash
#!/bin/bash
# scripts/build/build-all.sh

echo "Building Logistics Platform..."

# Build parent project
echo "Building parent project..."
mvn clean install -DskipTests

# Build shared libraries
echo "Building shared libraries..."
mvn clean install -pl shared-lib/common-dto -am
mvn clean install -pl shared-lib/common-utils -am
mvn clean install -pl shared-lib/common-exceptions -am

# Build platform core
echo "Building platform core..."
mvn clean install -pl platform-core/auth-service -am
mvn clean install -pl platform-core/tenant-service -am
# ... continue for all services

echo "Build completed successfully!"