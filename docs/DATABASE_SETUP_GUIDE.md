# Database Setup & Deployment Guide

This document provides a comprehensive guide on setting up the database for the **Logistics Platform** across different environments (Test, Development, Staging, Production).

## 1. Concepts: Local vs. Cloud

*   **Cloud Database (Online - Recommended for Ease)**
    *   **What it is:** The database runs on a remote server managed by a provider (e.g., Neon, Supabase, AWS RDS).
    *   **Pros:** No installation required on your machine. Access from anywhere. Scalable.
    *   **Cons:** Requires internet connection. Free tiers have limits.
    *   **Tools needed:** None! (Or optionally a GUI like DBeaver/PgAdmin to view data).

*   **Local Database (Offline)**
    *   **What it is:** The database runs directly on your computer.
    *   **Pros:** Fast, works offline, full control, free.
    *   **Cons:** Uses your system resources (RAM/CPU). Requires installation and maintenance.
    *   **Tools needed:** PostgreSQL Installer, PgAdmin (GUI), or Docker.

---

## 2. Setup Options

### Option A: Cloud Setup (No Installation Required)

**Recommended for:** Testing, Staging, Production, or if you have low system resources.

1.  **Choose a Provider:**
    *   **Neon.tech** (Serverless, excellent free tier).
    *   **Supabase** (Easy to use, Firebase alternative).
    *   **ElephantSQL** (Simple managed instances).

2.  **Create a Database:**
    *   Sign up and create a new project (e.g., `logistics-platform`).
    *   Note down the **Connection String**, **Host**, **User**, and **Password**.

3.  **Connection Details:**
    *   **URL:** `jdbc:postgresql://<host>:5432/<database>?sslmode=require`
    *   **Username:** (Provided by cloud)
    *   **Password:** (Provided by cloud)

---

### Option B: Local Setup (Using Installed Tools)

**Recommended for:** Heavy Development, Offline work.

1.  **Install PostgreSQL:**
    *   **Mac (Homebrew):** `brew install postgresql@15 && brew services start postgresql@15`
    *   **Windows/Mac/Linux (Installer):** Download from [postgresql.org](https://www.postgresql.org/download/).

2.  **Install a GUI Tool (Optional):**
    *   **PgAdmin 4:** Official GUI.
    *   **DBeaver:** Powerful universal DB tool.

### Option C: Docker Setup (Easiest Local Option)

If you have Docker installed, you don't need to install PostgreSQL manually.

1.  **Run the Database Container:**
    ```bash
    docker-compose up -d postgres
    ```
    *   This starts a PostgreSQL server on port `5432`.
    *   **Automatic Setup:** It automatically creates all required databases using `docker/init-db.sql`.

2.  **Verify:**
    *   The database is now running at `localhost:5432`.
    *   Username: `postgres`
    *   Password: `postgres` (as defined in `docker-compose.yml`)

3.  **Stop/Reset:**
    *   Stop: `docker-compose stop postgres`
    *   Reset (Destroy data): `docker-compose down -v`

---

## 3. Environment-Specific Configurations

---

## 3. Environment-Specific Configurations

### 1. Test Environment (`src/test/resources/application.yaml`)

We use a specific setup for running `mvn test`.

*   **Strategy:** We do **not** hardcode credentials. We use Environment Variables.
*   **Automatic Setup:** We have a Java utility (`DatabaseSetup.java`) that creates the required 16+ service databases for you automatically.

**How to Run:**
```bash
# 1. Export Credentials (for Cloud or Local)
export TEST_DB_URL=jdbc:postgresql://<your-host>:5432/postgres
export TEST_DB_USER=<your-user>
export TEST_DB_PASSWORD=<your-pass>

# 2. Run the Setup Utility (Creates empty DBs like 'user_service_test')
mvn -pl shared-lib/common-utils test-compile exec:java \
    -Dexec.classpathScope=test \
    -Dexec.mainClass="com.logistics.platform.utils.DatabaseSetup"

# 3. Run Tests
mvn clean install
```

### 2. Local Development (`src/main/resources/application.yaml`)

When running the application locally (`mvn spring-boot:run` or from IDE).

*   **Config:** Usually points to `localhost`.
*   **Env Variables:**
    ```yaml
    spring:
      datasource:
        url: ${DB_URL:jdbc:postgresql://localhost:5432/user_service}
        username: ${DB_USER:postgres}
        password: ${DB_PASSWORD:password}
    ```
*   **Setup:** You must create the databases manually or use the same `DatabaseSetup` utility (tweaked for non-test names) or let Hibernate `ddl-auto: update` handle tables (if DB exists).

### 3. Staging & Production

**NEVER** use local databases. Always use a managed Cloud Database (AWS RDS, Google Cloud SQL, Azure Database for PostgreSQL).

*   **Security:**
    *   Use private subnets.
    *   Enable SSL (`sslmode=verify-full`).
    *   Use separate credentials for each service (Microservices best practice).
*   **Deployment:**
    *   Set environment variables in your deployment platform (Kubernetes Secrets, AWS Parameter Store).
    *   **Example (K8s/Docker):**
        ```yaml
        env:
          - name: SPRING_DATASOURCE_URL
            value: "jdbc:postgresql://prod-db-instance.aws.com:5432/order_service"
          - name: SPRING_DATASOURCE_USERNAME
            value: "order_svc_user"
          - name: SPRING_DATASOURCE_PASSWORD
            value: "complex_secure_password"
        ```

---

## 4. Summary Checklist

| Environment | Database Type | Connection String Example | Who Manages it? |
| :--- | :--- | :--- | :--- |
| **Test** | Cloud / Local | `jdbc:postgresql://neon.tech.../postgres` | `DatabaseSetup.java` Utility |
| **Development** | Local (Docker/Native) | `jdbc:postgresql://localhost:5432/db` | Developer (You) |
| **Staging** | Cloud (Shared) | `jdbc:postgresql://staging-db.../db` | DevOps / CI Pipeline |
| **Production** | Cloud (Dedicated) | `jdbc:postgresql://prod-db.../db` | Cloud Provider (AWS/GCP) |

## 5. Troubleshooting

*   **"Role 'postgres' does not exist"**: You are probably on a Mac using Homebrew. Your default user is your system username (e.g., `sanjeet_kumar`).
    *   *Fix:* Use `psql postgres` to log in, or set `TEST_DB_USER=sanjeet_kumar`.
*   **"Connection refused"**: The database is not running.
    *   *Fix:* Check Docker (`docker ps`) or Services (`brew services list`).
*   **"No suitable driver found"**: Missing dependency in `pom.xml`.
    *   *Fix:* Ensure `postgresql` dependency is added (we fixed this in `common-utils`).
