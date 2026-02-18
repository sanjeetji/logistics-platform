CREATE DATABASE auth_db; CREATE DATABASE user_db; CREATE DATABASE user_management_db; CREATE DATABASE order_db; CREATE DATABASE dispatch_db; CREATE DATABASE fleet_db; CREATE DATABASE tenant_db; CREATE DATABASE role_permission_db; CREATE DATABASE orchestration_db; GRANT ALL PRIVILEGES ON DATABASE auth_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE user_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE user_management_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE order_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE dispatch_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE fleet_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE tenant_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE role_permission_db TO logistics_user; GRANT ALL PRIVILEGES ON DATABASE orchestration_db TO logistics_user;

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


-- Phase 10: Shared & Advanced Services
CREATE DATABASE analytics_db; GRANT ALL PRIVILEGES ON DATABASE analytics_db TO logistics_user;
CREATE DATABASE notifications_db; GRANT ALL PRIVILEGES ON DATABASE notifications_db TO logistics_user;
CREATE DATABASE ml_db; GRANT ALL PRIVILEGES ON DATABASE ml_db TO logistics_user;
CREATE DATABASE geo_db; GRANT ALL PRIVILEGES ON DATABASE geo_db TO logistics_user;
CREATE DATABASE pricing_db; GRANT ALL PRIVILEGES ON DATABASE pricing_db TO logistics_user;
CREATE DATABASE payment_db; GRANT ALL PRIVILEGES ON DATABASE payment_db TO logistics_user;
CREATE DATABASE logistics_routing; GRANT ALL PRIVILEGES ON DATABASE logistics_routing TO logistics_user;
CREATE DATABASE billing_db; GRANT ALL PRIVILEGES ON DATABASE billing_db TO logistics_user;
CREATE DATABASE returns_db; GRANT ALL PRIVILEGES ON DATABASE returns_db TO logistics_user;
CREATE DATABASE payouts_db; GRANT ALL PRIVILEGES ON DATABASE payouts_db TO logistics_user;

-- Phase 11: Migrated from MySQL
CREATE DATABASE logistics_b2c_db; GRANT ALL PRIVILEGES ON DATABASE logistics_b2c_db TO logistics_user;
CREATE DATABASE logistics_b2b_db; GRANT ALL PRIVILEGES ON DATABASE logistics_b2b_db TO logistics_user;
CREATE DATABASE webhook_db; GRANT ALL PRIVILEGES ON DATABASE webhook_db TO logistics_user;
