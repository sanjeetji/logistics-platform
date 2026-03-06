-- ============================================================
-- Logistics Platform — Monolith Database Setup
-- Single database for the entire platform (monolith mode)
-- ============================================================

-- The main application database (all tables live here)
-- Hibernate (ddl-auto: create-drop) will create all tables automatically on startup

GRANT ALL PRIVILEGES ON DATABASE logistics_postgres TO logistics_user;
