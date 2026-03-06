# 🛠️ Scripts Reference — Logistics Platform

Utility scripts for managing the Logistics Platform locally.
All scripts auto-detect the project root — run them from **anywhere**.

---

## 📁 Script Inventory

| Script | Purpose | Usage |
|---|---|---|
| `run-platform.sh` | **Master control script** — start/stop/build/fresh/logs | `./docker/scripts/run-platform.sh <command>` |
| `health-check.sh` | Check health of all services (app + infra) | `./docker/scripts/health-check.sh` |
| `clean.sh` | Nuclear Docker cleanup — removes containers, images, volumes | `./docker/scripts/clean.sh` |
| `postgres-backup.sh` | Backup all DBs to `backups/postgres/*.sql.gz` | `./docker/scripts/postgres-backup.sh` |
| `postgres-restore.sh` | Restore DB from a backup file | `./docker/scripts/postgres-restore.sh <file.sql.gz>` |
| `postgres-connect.sh` | Open psql shell in the running postgres container | `./docker/scripts/postgres-connect.sh [db_name]` |
| `postgres-start.sh` | Start only PostgreSQL + pgAdmin (no full infra) | `./docker/scripts/postgres-start.sh` |
| `fix-pgadmin.sh` | Fix pgAdmin connection issues (recreates pgpass, restarts pgadmin) | `./docker/scripts/fix-pgadmin.sh` |
| `fix_ide_errors.sh` | Maven clean install to fix IDE import errors (Lombok/MapStruct) | `./docker/scripts/fix_ide_errors.sh` |
| `create_test_dbs.sh` | Create all local test databases (runs inside Docker, no host psql needed) | `./docker/scripts/create_test_dbs.sh` |
| `run-integration-test.sh` | End-to-end API integration test (auth → order → dispatch) | `./docker/scripts/run-integration-test.sh` |
| `run-load-tests.sh` | Gatling load tests | `./docker/scripts/run-load-tests.sh [quick\|orders\|ws\|full]` |

---

## ▶️ Most Common Commands

```bash
# Daily startup (keeps data, no rebuild)
./docker/scripts/run-platform.sh start

# Full reset (wipes DB, rebuilds image — use after schema changes)
./docker/scripts/run-platform.sh fresh

# Stop everything
./docker/scripts/run-platform.sh stop

# Tail all logs
./docker/scripts/run-platform.sh logs

# Check all service health
./docker/scripts/health-check.sh

# Take a DB backup
./docker/scripts/postgres-backup.sh

# Restore a backup
./docker/scripts/postgres-restore.sh backups/postgres/logistics_backup_20260226_120000.sql.gz

# Open a psql shell
./docker/scripts/postgres-connect.sh

# Nuclear cleanup (use when Docker is broken)
./docker/scripts/clean.sh
```

---

## 💡 Notes

- All scripts use `docker-compose.yml` from `docker/` directory — no need to `cd` first.
- Backups are saved to `<project_root>/backups/postgres/` which is git-ignored.
- `run-integration-test.sh` requires the platform to be running and `jq` installed (`brew install jq`).
- `create_test_dbs.sh` runs inside the Docker container — no host PostgreSQL tools required.
- `run-load-tests.sh` requires the `load-tests/` Gatling Maven module to be set up.

> 📘 For the full automation guide (local + CI/CD + production), see: [`docs/AUTOMATION_GUIDE.md`](../../docs/AUTOMATION_GUIDE.md)