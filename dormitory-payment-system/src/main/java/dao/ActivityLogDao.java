package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.ActivityLog;

public class ActivityLogDao {

    public void log(int userId, String username, String action, String details) {
        String sql = """
            INSERT INTO activity_logs (user_id, username, action, details, timestamp)
            VALUES (?, ?, ?, ?, datetime('now', 'localtime'))
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, action);
            pstmt.setString(4, details);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to write activity log: " + e.getMessage());
        }
    }

    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT id, user_id, username, action, details, timestamp FROM activity_logs ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getString("timestamp")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }

        return logs;
    }
}