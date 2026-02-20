# 🐘 pgAdmin Connectivity Guide

This guide explains how to connect to the Logistics Platform's PostgreSQL database using pgAdmin, whether you are running inside or outside the Docker environment.

## 1. Credentials & Configuration

- **Database Name**: `logistics_postgres`
- **Username**: `logistics_user`
- **Password**: `logistics_pass`
- **Default Port**: `5432`

---

## 2. Connecting from pgAdmin (Inside Docker)
If you are using the pgAdmin container provided in the `docker-compose.yml` (at `http://localhost:5050`):

1. **Login to pgAdmin**:
   - **Email**: `admin@logistics.com`
   - **Password**: `admin123`
2. **Add New Server**:
   - **General Tab**: Give it a name (e.g., `Logistics Platform`).
   - **Connection Tab**:
     - **Host name/address**: `postgres` (This is the service name used in Docker).
     - **Port**: `5432`
     - **Maintenance database**: `logistics_postgres`
     - **Username**: `logistics_user`
     - **Password**: `logistics_pass`
3. **Save**: Click Save. You are now connected via the internal Docker network.

---

## 3. Connecting from Host (Outside Docker)
If you are using pgAdmin directly installed on your Mac:

1. **Add New Server**:
   - **General Tab**: Give it a name (e.g., `Local Docker Postgres`).
   - **Connection Tab**:
     - **Host name/address**: `localhost`
     - **Port**: `5432`
     - **Maintenance database**: `logistics_postgres`
     - **Username**: `logistics_user`
     - **Password**: `logistics_pass`
2. **Save**: Click Save. This connects via the port mapped to your localhost.

---

## 4. Common Issues
- **Connection Timed Out**: Ensure the container is running (`docker ps`).
- **Authentication Failed**: Double-check the username (`logistics_user`) and password (`logistics_pass`).
- **Host Not Found**: If inside Docker, ensure you are using the hostname `postgres`, not `localhost`.
