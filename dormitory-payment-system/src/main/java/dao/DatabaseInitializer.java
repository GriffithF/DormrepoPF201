package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT    NOT NULL UNIQUE,
                    password TEXT    NOT NULL,
                    role     TEXT    NOT NULL CHECK(role IN ('ADMIN', 'STAFF', 'TENANT')),
                    status   TEXT    NOT NULL DEFAULT 'PENDING'
                                     CHECK(status IN ('PENDING', 'APPROVED', 'ACTIVE', 'INACTIVE'))
                );
                """;

            stmt.execute(createUsersTable);
            System.out.println("[DB] Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}