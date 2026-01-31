import os

# Allow connections to Docker services
DEFAULT_SERVER = '0.0.0.0'
DEFAULT_SERVER_PORT = 80

# Security settings
SECURITY_PASSWORD_SALT = 'logistics-pgadmin-salt-2024'
SESSION_COOKIE_SAMESITE = 'Lax'
SESSION_COOKIE_SECURE = False
SESSION_COOKIE_HTTPONLY = True

# Database connection pool
DATABASE_CONNECTION_POOL_SIZE = 10

# Allow server connections without master password
MASTER_PASSWORD_REQUIRED = False

# Logging configuration
CONSOLE_LOG_LEVEL = 10
FILE_LOG_LEVEL = 10
LOG_FILE = '/var/lib/pgadmin/pgadmin.log'

# Allow saving server passwords
ALLOW_SAVE_PASSWORD = True

# Server timeout
SERVER_TIMEOUT = 300

# Enable desktop mode
DESKTOP_USER = 'admin@logistics.com'

# Trust Docker internal network
TRUSTED_HOSTS = ['localhost', '127.0.0.1', 'postgres-db', 'host.docker.internal']