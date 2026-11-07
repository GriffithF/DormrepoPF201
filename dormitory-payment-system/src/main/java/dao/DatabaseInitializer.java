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
                                     CHECK(status IN ('PENDING', 'APPROVED', 'INACTIVE'))
                );
                """;

            String createActivityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id   INTEGER,
                    username  TEXT,
                    action    TEXT NOT NULL,
                    details   TEXT,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

            stmt.execute(createUsersTable);
            stmt.execute(createActivityLogsTable);

            System.out.println("[DB] Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}