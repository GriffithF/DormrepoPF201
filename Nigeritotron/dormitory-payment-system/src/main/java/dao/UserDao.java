package main.java.dao;

import main.java.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    public boolean register(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                System.out.println("Error: Username '" + username + "' is already taken.");
            } else {
                System.err.println("Registration failed: " + e.getMessage());
            }
            return false;
        }
    }

    public Optional<User> login(String username, String password) {
        String sql = "SELECT id, username, password, role, status FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return Optional.empty();
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, role, status FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }

        return Optional.empty();
    }
}