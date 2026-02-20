# Comprehensive Docker Command Reference

This document provides a categorized list of essential and advanced Docker commands with examples and their primary uses.

## 1. Container Lifecycle Management

Commands to manage the state of containers.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker run` | Create and start a container | `docker run -d --name my-app -p 8080:80 nginx` |
| `docker start` | Start a stopped container | `docker start my-app` |
| `docker stop` | Gracefully stop running container(s) | `docker stop my-app` |
| `docker restart` | Restart container(s) | `docker restart my-app` |
| `docker pause` | Pause all processes within a container | `docker pause my-app` |
| `docker unpause` | Unpause all processes within a container | `docker unpause my-app` |
| `docker kill` | Kill running container(s) immediately | `docker kill my-app` |
| `docker rm` | Remove stopped container(s) | `docker rm my-app` |
| `docker create` | Create a new container (but do not start it) | `docker create --name my-worker generic-worker` |

### Detailed Examples:

**Run a container in detached mode with port mapping:**
```bash
docker run -d -p 8080:80 --name web-server nginx
# -d: Detached mode (background)
# -p 8080:80: Maps host port 8080 to container port 80
# --name: Assigns a name to the container
```

**Run an interactive shell:**
```bash
docker run -it ubuntu /bin/bash
# -i: Interactive
# -t: Allocated a pseudo-TTY
```

**Remove all stopped containers:**
```bash
docker container prune
```

**Stop ALL running containers:**
```bash
docker stop $(docker ps -q)
# Stops all currently running containers
```

---

## 2. Container Information & Inspection

Commands to get details about running containers.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker ps` | List running containers | `docker ps` |
| `docker ps -a` | List all containers (running & stopped) | `docker ps -a` |
| `docker logs` | Fetch logs of a container | `docker logs -f my-app` |
| `docker inspect` | Return low-level information on Docker objects | `docker inspect my-app` |
| `docker stats` | Live stream of container usage stats | `docker stats` |
| `docker top` | Display running processes of a container | `docker top my-app` |
| `docker events` | Get real-time events from the server | `docker events` |
| `docker port` | List port mappings for the container | `docker port my-app` |

### Detailed Examples:

**Follow log output:**
```bash
docker logs -f --tail 100 my-app
# -f: Follow log output
# --tail 100: Show last 100 lines
```

**Filter containers:**
```bash
docker ps -f "status=exited"
# Lists only exited containers
```

---

## 3. Image Management

Commands to work with Docker images.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker images` | List images | `docker images` |
| `docker pull` | Pull an image or a repository from a registry | `docker pull redis:alpine` |
| `docker push` | Push an image or a repository to a registry | `docker push myrepo/myimage:tag` |
| `docker build` | Build an image from a Dockerfile | `docker build -t my-app:v1 .` |
| `docker rmi` | Remove one or more images | `docker rmi my-app:v1` |
| `docker tag` | Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE | `docker tag my-app:v1 murepo/my-app:latest` |
| `docker history` | Show the history of an image | `docker history nginx` |
| `docker save` | Save one or more images to a tar archive | `docker save -o images.tar postgres:13` |
| `docker load` | Load an image from a tar archive | `docker load -i images.tar` |

### Detailed Examples:

**Build an image:**
```bash
docker build -t my-api:1.0 -f Dockerfile.prod .
# -t: Tag the image
# -f: Specify the Dockerfile path
```

**Remove a specific image:**
You can remove an image by its name (tag) or ID.
```bash
# Remove by name (tag)
docker rmi my-app:v1

# Remove by Image ID (first few characters are enough)
docker rmi 7d9495d03763

# Force remove an image (if it's being used by a stopped container)
docker rmi -f my-app:v1
```

**Remove unused images:**
```bash
docker image prune -a
# Removes all unused images, not just dangling ones
```

---

## 4. Networking

Commands to manage Docker networks.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker network ls` | List networks | `docker network ls` |
| `docker network create`| Create a network | `docker network create my-net` |
| `docker network connect`| Connect a container to a network | `docker network connect my-net my-app` |
| `docker network disconnect`| Disconnect a container from a network | `docker network disconnect my-net my-app` |
| `docker network inspect`| Display detailed information on one or more networks | `docker network inspect bridge` |
| `docker network rm` | Remove one or more networks | `docker network rm my-net` |

### Detailed Examples:

**Running containers on a custom network:**
```bash
# Create network first
docker network create backend-net

# Run database on network
docker run -d --name db --network backend-net postgres

# Run app on same network (can resolve 'db' by name)
docker run -d --name api --network backend-net my-api
```

---

## 5. Volumes & Data Persistence

Commands to manage Docker volumes.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker volume ls` | List volumes | `docker volume ls` |
| `docker volume create` | Create a volume | `docker volume create db-data` |
| `docker volume inspect`| Display detailed information on one or more volumes | `docker volume inspect db-data` |
| `docker volume rm` | Remove one or more volumes | `docker volume rm db-data` |
| `docker volume prune` | Remove all unused local volumes | `docker volume prune` |

### Detailed Examples:

**Mount a volume:**
```bash
docker run -d -v db-data:/var/lib/postgresql/data postgres
# Persists postgres data to 'db-data' volume
```

**Mount a specific host directory (Bind Mount):**
```bash
docker run -d -v $(pwd)/src:/app/src node:14
# Mounts current directory's src to container's /app/src
```

---

## 6. Docker System & Cleanup

Commands for system-wide information and cleanup.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker info` | Display system-wide information | `docker info` |
| `docker version` | Show the Docker version information | `docker version` |
| `docker system df` | Show docker disk usage | `docker system df` |
| `docker system prune` | Remove unused data | `docker system prune` |

### Detailed Examples:

**Total Cleanup: Delete Everything**
> **WARNING**: This will remove all unused containers, networks, images (both dangling and unreferenced), and optionally, volumes.

```bash
# The "Nuclear Option" - Clean up everything unused
docker system prune -a --volumes

# What this does:
# - all stopped containers
# - all networks not used by at least one container
# - all images without at least one container associated to them
# - all build cache
# --volumes: also prune volumes not used by any container
```

**Manual "Delete Everything" (Forceful)**
Sometimes you want to forcefully stop and delete absolutely everything currently running or stopped.

```bash
# 1. Stop all running containers
docker stop $(docker ps -aq)

# 2. Remove all containers
docker rm $(docker ps -aq)

# 3. Remove all images
docker rmi $(docker images -q)

# 4. Remove all volumes
docker volume rm $(docker volume ls -q)

# 5. Remove all networks
docker network rm $(docker network ls -q)
```

---

## 7. Interaction with Containers

Commands to interact with running containers.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker exec` | Run a command in a running container | `docker exec -it my-app bash` |
| `docker cp` | Copy files/folders between a container and the local filesystem | `docker cp my-app:/app/log.txt .` |
| `docker attach` | Attach local standard input, output, and error streams to a running container | `docker attach my-app` |
| `docker diff` | Inspect changes to files or directories on a container's filesystem | `docker diff my-app` |

### Detailed Examples:

**Execute a command inside a running container:**
```bash
docker exec -it my-db psql -U postgres
# Opens psql shell inside the container named 'my-db'
```

**Copy file from container to host:**
```bash
docker cp my-web:/etc/nginx/nginx.conf ./nginx.conf.backup
```

---

## 8. Docker Compose (Common Commands)

Although a separate tool, it is essential for multi-container apps.

| Command | Usage | Example |
| :--- | :--- | :--- |
| `docker-compose up` | Create and start containers | `docker-compose up -d` |
| `docker-compose down` | Stop and remove containers, networks | `docker-compose down` |
| `docker-compose logs` | View output from containers | `docker-compose logs -f` |
| `docker-compose build` | Build or rebuild services | `docker-compose build` |
| `docker-compose ps` | List containers | `docker-compose ps` |
| `docker-compose exec` | Execute a command in a running container | `docker-compose exec web bash` |
| `docker-compose restart`| Restart services | `docker-compose restart` |

### Detailed Examples:

**Start everything in background:**
```bash
docker-compose up -d --build
# -d: Detached
# --build: Rebuild images before starting
```

**Stop and clean up volumes:**
```bash
docker-compose down -v
# -v: Remove named volumes declared in the `volumes` section
```

---

## 9. Project Specific Automation (run-platform.sh)

The project includes a helper script `docker/scripts/run-platform.sh` to simplify common operations.

| Command | Usage | Description |
| :--- | :--- | :--- |
| `./docker/scripts/run-platform.sh build` | Build | Compiles the code (Maven) and rebuilds the Docker image. Run this if you change Java code. |
| `./docker/scripts/run-platform.sh start` | Start | Starts the entire platform (Infrastructure + Logistic Platform) in detached mode. |
| `./docker/scripts/run-platform.sh stop` | Stop | Stops all platform containers. |
| `./docker/scripts/run-platform.sh restart` | Restart | Stops and then starts the platform. |
| `./docker/scripts/run-platform.sh logs` | Logs | Tails the logs of all running containers. |

### Common Workflows:

**First time setup or after code changes:**
```bash
./docker/scripts/run-platform.sh build
./docker/scripts/run-platform.sh start
```

**Check application health:**
```bash
./docker/scripts/run-platform.sh logs
# Press Ctrl+C to exit logs
```
