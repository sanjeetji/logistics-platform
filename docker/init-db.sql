-- Initialize Databases for Logistics Platform Services

CREATE DATABASE tenant_service_test;
CREATE DATABASE auth_service_test;
CREATE DATABASE user_service_test;
CREATE DATABASE fleet_service_test;
CREATE DATABASE order_service_test;
CREATE DATABASE dispatch_service_test;
CREATE DATABASE role_permission_service_test;
CREATE DATABASE customer_service_test;
CREATE DATABASE route_optimization_service_test;
CREATE DATABASE payment_service_test;
CREATE DATABASE notification_service_test;
CREATE DATABASE returns_service_test;
CREATE DATABASE customer_app_service_test;
CREATE DATABASE driver_app_service_test;
CREATE DATABASE user_management_service_test;
CREATE DATABASE tracking_service_test;

-- Also create the main development databases (non-test)
CREATE DATABASE tenant_service;
CREATE DATABASE auth_service;
CREATE DATABASE user_service;
CREATE DATABASE fleet_service;
CREATE DATABASE order_service;
CREATE DATABASE dispatch_service;
CREATE DATABASE role_permission_service;
CREATE DATABASE customer_service;
CREATE DATABASE route_optimization_service;
CREATE DATABASE payment_service;
CREATE DATABASE notification_service;
CREATE DATABASE returns_service;
CREATE DATABASE customer_app_service;
CREATE DATABASE driver_app_service;
CREATE DATABASE user_management_service;
CREATE DATABASE tracking_service;
