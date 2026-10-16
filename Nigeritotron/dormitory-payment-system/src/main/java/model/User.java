package main.java.model;

public record User(
        int id,
        String username,
        String password,
        String role,
        String status
) {
    public User(String username, String password, String role, String status) {
        this(0, username, password, role, status);
    }
}