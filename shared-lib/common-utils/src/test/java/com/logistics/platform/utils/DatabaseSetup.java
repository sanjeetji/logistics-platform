package com.logistics.platform.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DatabaseSetup {

    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DEFAULT_User = "postgres";
    private static final String DEFAULT_PASSWORD = "password";

    // List of databases to create
    private static final List<String> DATABASES = Arrays.asList(
            "tenant_service_test",
            "auth_service_test",
            "user_service_test",
            "fleet_service_test",
            "order_service_test",
            "dispatch_service_test",
            "role_permission_service_test",
            "customer_service_test",
            "route_optimization_service_test",
            "payment_service_test",
            "notification_service_test",
            "returns_service_test",
            "customer_app_service_test",
            "driver_app_service_test",
            "user_management_service_test",
            "tracking_service_test");

    public static void main(String[] args) {
        // Priority: System Property (-D) -> Environment Variable -> Default
        String dbUrl = getConfiguration("TEST_DB_URL", DEFAULT_DB_URL);
        String dbUser = getConfiguration("TEST_DB_USER", System.getProperty("user.name", "postgres"));
        String dbPassword = getConfiguration("TEST_DB_PASSWORD", DEFAULT_PASSWORD);

        System.out.println("Connecting with User: " + dbUser);
        System.out.println("Connecting to URL: " + dbUrl);
        if (!dbUrl.endsWith("/postgres")) {
            // If the URL points to a specific DB, try to strip it and append postgres
            // This is a naive check, for cloud DBs the URL might be different
            System.out.println(
                    "Warning: TEST_DB_URL should ideally point to 'postgres' database for creation privileges. Current: "
                            + dbUrl);
        }

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Statement statement = connection.createStatement()) {

            for (String dbName : DATABASES) {
                if (!databaseExists(statement, dbName)) {
                    createDatabase(statement, dbName);
                } else {
                    System.out.println("Database " + dbName + " already exists.");
                }
            }
            System.out.println("All test databases verified/created successfully.");

        } catch (SQLException e) {
            System.err.println("Error connecting to database or executing SQL: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean databaseExists(Statement statement, String dbName) throws SQLException {
        String query = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
        return statement.executeQuery(query).next();
    }

    private static void createDatabase(Statement statement, String dbName) throws SQLException {
        System.out.println("Creating database " + dbName + "...");
        // Parameters cannot be used in CREATE DATABASE statements
        statement.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
        System.out.println("Database " + dbName + " created.");
    }

    private static String getConfiguration(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        if (value == null || value.isEmpty()) {
            value = defaultValue;
        }
        return value;
    }
}
