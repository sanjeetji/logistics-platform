CREATE DATABASE auth_db; CREATE DATABASE user_db; CREATE DATABASE order_db; CREATE DATABASE dispatch_db; CREATE DATABASE fleet_db; CREATE DATABASE tenant_db; CREATE DATABASE role_permission_db; GRANT ALL PRIVILEGES ON DATABASE auth_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE user_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE order_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE dispatch_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE fleet_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE tenant_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE role_permission_db TO logistics_user;

-- Phase 9: Final Audit Execution Databases
CREATE DATABASE compliance_db; GRANT ALL PRIVILEGES ON DATABASE compliance_db TO logistics_user;
CREATE DATABASE warehouse_db; GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO logistics_user;
CREATE DATABASE inventory_db; GRANT ALL PRIVILEGES ON DATABASE inventory_db TO logistics_user;
CREATE DATABASE shipment_db; GRANT ALL PRIVILEGES ON DATABASE shipment_db TO logistics_user;
CREATE DATABASE b2b_order_db; GRANT ALL PRIVILEGES ON DATABASE b2b_order_db TO logistics_user;
CREATE DATABASE quick_dispatch_db; GRANT ALL PRIVILEGES ON DATABASE quick_dispatch_db TO logistics_user;
CREATE DATABASE customer_portal_db; GRANT ALL PRIVILEGES ON DATABASE customer_portal_db TO logistics_user;
CREATE DATABASE driver_app_db; GRANT ALL PRIVILEGES ON DATABASE driver_app_db TO logistics_user;
CREATE DATABASE customer_db; GRANT ALL PRIVILEGES ON DATABASE customer_db TO logistics_user;

CREATE DATABASE integration_db; GRANT ALL PRIVILEGES ON DATABASE integration_db TO logistics_user;

CREATE DATABASE chat_db; GRANT ALL PRIVILEGES ON DATABASE chat_db TO logistics_user;

CREATE DATABASE audit_log_db; GRANT ALL PRIVILEGES ON DATABASE audit_log_db TO logistics_user;

CREATE DATABASE master_data_db; GRANT ALL PRIVILEGES ON DATABASE master_data_db TO logistics_user;

