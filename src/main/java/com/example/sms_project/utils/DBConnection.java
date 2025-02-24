package com.example.sms_project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Change 'studentdb' to your DB name
    private static final String USER = "postgres";  // Change to your PostgreSQL username
    private static final String PASSWORD = "admin"; // Change to your PostgreSQL password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
}
