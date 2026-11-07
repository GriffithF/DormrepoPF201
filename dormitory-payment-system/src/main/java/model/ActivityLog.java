package model;

public record ActivityLog(
        int id,
        int userId,
        String username,
        String action,
        String details,
        String timestamp

        ) {}