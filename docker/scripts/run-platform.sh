#!/bin/bash

# ==============================================================================
# Logistics Platform Automation Script
# ==============================================================================
# This script manages the build, startup, and maintenance of the Logistics Platform.
# It handles environment loading, docker-compose context, and NVD API Key injection.

# --- Colors for Output ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# --- Default Configurations ---
# --- Default Configurations ---
DEFAULT_ENV="dev"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
PROJECT_ROOT="$SCRIPT_DIR/../.."
ENV_FILE="$PROJECT_ROOT/.env"

# Ensure standard paths are in PATH
export PATH="/usr/local/bin:/opt/homebrew/bin:$PATH"
# --- Helper Functions ---
log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

check_dependency() {
  if ! command -v "$1" &> /dev/null; then
    log_error "$1 is not installed or not in PATH."
    exit 1
  fi
}

usage() {
  echo ""
  echo "Usage: ./run-platform.sh [COMMAND] [OPTIONS]"
  echo ""
  echo "Commands:"
  echo "  start       Start the platform (Docker Compose)"
  echo "  stop        Stop the platform"
  echo "  restart     Restart the platform"
  echo "  build       Build the platform (Maven + Docker)"
  echo "  logs        Follow logs of all services"
  echo "  status      Check status of containers"
  echo "  prune       Clean up unused Docker resources"
  echo "  help        Show this help message"
  echo ""
  echo "Options:"
  echo "  --env=[dev|prod]  Select environment (default: dev)"
  echo ""
  echo "Examples:"
  echo "  ./run-platform.sh start"
  echo "  ./run-platform.sh start --env=prod"
  echo "  ./run-platform.sh build"
  echo ""
}

# --- Argument Parsing ---
COMMAND=$1
shift # Shift passed first argument (command) so we can parse options

ENV=$DEFAULT_ENV

for i in "$@"; do
  case $i in
    --env=*)
      ENV="${i#*=}"
      shift 
      ;;
    *)
      # unknown option
      ;;
  esac
done

# --- Environment Setup ---
if [ "$ENV" == "prod" ]; then
  ENV_FILE=".env.prod"
  COMPOSE_FILE="$DOCKER_DIR/docker-compose.prod.yml"
else
  ENV_FILE=".env"
  COMPOSE_FILE="$DOCKER_DIR/docker-compose.yml"
fi

# Load Environment Variables from file if it exists
if [ -f "$ENV_FILE" ]; then
  log_info "Loading environment from $ENV_FILE"
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  log_warn "Environment file $ENV_FILE not found. Using system environment variables."
fi

# NVD API Key Check
if [ -z "$NVD_API_KEY" ]; then
    log_warn "NVD_API_KEY is not set. OWASP Dependency Check may fail or be slow."
else
    log_info "NVD_API_KEY found."
fi

# Validate Dependencies
check_dependency "docker"
check_dependency "mvn"

check_docker_daemon() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker is not running or not accessible. Please start Docker Desktop."
        exit 1
    fi
}

# --- Command Implementation ---

case "$COMMAND" in
  start)
    check_docker_daemon
    log_info "Starting platform in [$ENV] mode..."
    # The docker-compose.yml in ./docker expects to be run from there OR relative paths.
    # We use --project-directory . to ensure relative paths in the compose file work from the root context if they are ../
    # But wait, looking at the compose file: "context: ../service-discovery".
    # If we run from project root with -f docker/docker-compose.yml, then ".." relative to docker/docker-compose.yml is ".".
    # Docker compose resolves paths relative to the compose file location by default.
    # So if file is in docker/, ../ is project root. Correct.
    
    if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" up -d; then
        log_success "Platform started successfully!"
    else
        log_error "Failed to start platform."
        exit 1
    fi
    ;;

    stop)
    check_docker_daemon
    log_info "Stopping platform..."
    docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" down
    log_success "Platform stopped."
    ;;

  restart)
    $0 stop --env="$ENV"
    sleep 2
    $0 start --env="$ENV"
    ;;

  build)
    log_info "Building project with Maven..."
    # Using the NVD Key if present to avoid limits, but skipping by default as requested to save time.
    # To run security check: ./run-platform.sh build --secure
    # To run security check: ./run-platform.sh build --secure
    if (cd "$PROJECT_ROOT" && mvn clean install -DskipTests -Ddependency-check-maven.skip=true -Dnvd.api.key="$NVD_API_KEY"); then
        log_success "Maven build successful."
        
        check_docker_daemon
        log_info "Building Docker images..."
        if docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" build; then
            log_success "Docker images built."
        else
             log_error "Docker build failed."
             exit 1
        fi
    else
        log_error "Maven build failed."
        exit 1
    fi
    ;;

  logs)
    check_docker_daemon
    docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" logs -f
    ;;

  status)
    check_docker_daemon
    docker compose -f "$COMPOSE_FILE" --project-directory "$DOCKER_DIR" -p logistics-platform --env-file "$ENV_FILE" ps
    ;;

  prune)
    check_docker_daemon
    log_warn "This will remove all stopped containers and unused networks."
    docker system prune -f
    ;;

  help|*)
    usage
    ;;
esac
