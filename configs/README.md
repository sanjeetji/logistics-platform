# Configuration Repository

This directory contains centralized configuration files for all microservices.

## Structure

```
configs/
├── application.yml          # Common config for all services
├── auth-service.yml         # Auth service specific config
├── order-service.yml        # Order service specific config
├── fleet-service.yml        # Fleet service specific config
└── ...
```

## Usage

The Config Service reads from this directory (or a Git repository) and serves configurations to all microservices.

### Profile-based Configuration

- `application.yml` - Default configuration
- `application-dev.yml` - Development environment
- `application-prod.yml` - Production environment
- `{service-name}-{profile}.yml` - Service and profile specific

## Security

Sensitive values should use encryption:
```yaml
datasource:
  password: '{cipher}AQA...'
```

Use `spring cloud config encrypt` to encrypt values.
