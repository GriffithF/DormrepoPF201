package main.java.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:dormitory.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT    NOT NULL UNIQUE,
                password TEXT    NOT NULL,
                role     TEXT    NOT NULL CHECK(role IN ('ADMIN', 'STAFF', 'TENANT')),
                status   TEXT    NOT NULL DEFAULT 'PENDING'
                                 CHECK(status IN ('PENDING', 'ACTIVE', 'INACTIVE'))
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            System.out.println("[DB] Database initialized successfully (dormitory.db)");

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}