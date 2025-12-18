package com.sams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sams";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ← change if your MySQL has a password

    private static Connection connection;

    // Get a single shared connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Add mysql-connector-j.jar to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Close connection (optional use)
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}