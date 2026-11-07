package ui;

import java.util.List;
import java.util.Scanner;

import dao.ActivityLogDao;
import dao.UserDao;
import model.ActivityLog;
import model.User;

public class AdminMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private User currentAdmin;

    public AdminMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
    }

    public void show(User admin) {
        this.currentAdmin = admin;

        while (true) {
            System.out.println("\n========================================");
            System.out.println("              ADMIN MENU");
            System.out.println("========================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role & Status");
            System.out.println("5. View Activity / Audit Logs");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUserAccount();
                case "4" -> updateUserRoleAndStatus();
                case "5" -> viewActivityLogs();
                case "6" -> {
                    System.out.println("\nLogging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n========== ALL SYSTEM USERS ==========");
        printUserTable(users);
    }

    private void viewPendingUsers() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n======= PENDING REGISTRATIONS =======");
        if (pending.isEmpty()) {
            System.out.println("No pending registrations found.");
        } else {
            printUserTable(pending);
        }
    }

    private void approveUserAccount() {
        List<User> pending = userDao.getPendingUsers();

        if (pending.isEmpty()) {
            System.out.println("\nNo pending users to approve.");
            return;
        }

        System.out.println("\n======= PENDING USERS =======");
        printUserTable(pending);

        int userId = readIntInput("Enter User ID to approve (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean exists = pending.stream().anyMatch(u -> u.id() == userId);
        if (!exists) {
            System.out.println("User ID " + userId + " is not in the pending list.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("User ID " + userId + " has been approved successfully.");
            activityLogDao.log(currentAdmin.id(), currentAdmin.username(),
                    "APPROVE_USER", "Approved user ID " + userId);
        } else {
            System.out.println("Failed to approve user.");
        }
    }

    private void updateUserRoleAndStatus() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n========== ALL SYSTEM USERS ==========");
        printUserTable(users);

        int userId = readIntInput("Enter User ID to update (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (currentAdmin != null && userId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.id() == userId);
        if (!exists) {
            System.out.println("User ID " + userId + " does not exist.");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) return;

        String newStatus = readValidStatus();
        if (newStatus == null) return;

        boolean success = userDao.updateUserRoleAndStatus(userId, newRole, newStatus);

        if (success) {
            System.out.println("User ID " + userId + " updated successfully.");
            System.out.println("New Role   : " + newRole);
            System.out.println("New Status : " + newStatus);

            activityLogDao.log(currentAdmin.id(), currentAdmin.username(),
                    "UPDATE_USER", "Updated user ID " + userId + " to Role=" + newRole + ", Status=" + newStatus);
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private void viewActivityLogs() {
        List<ActivityLog> logs = activityLogDao.getAllLogs();

        System.out.println("\n================ ACTIVITY / AUDIT LOGS ================");

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        System.out.println("+-----+--------+---------------+---------------------+--------------------------------+---------------------+");
        System.out.printf("| %-3s | %-6s | %-13s | %-19s | %-30s | %-19s |%n",
                "ID", "UserID", "Username", "Action", "Details", "Timestamp");
        System.out.println("+-----+--------+---------------+---------------------+--------------------------------+---------------------+");

        for (ActivityLog log : logs) {
            System.out.printf("| %-3d | %-6d | %-13s | %-19s | %-30s | %-19s |%n",
                    log.id(),
                    log.userId(),
                    log.username(),
                    log.action(),
                    log.details() != null ? log.details() : "",
                    log.timestamp());
        }

        System.out.println("+-----+--------+---------------+---------------------+--------------------------------+---------------------+");
        System.out.println("Total logs: " + logs.size());
    }

    // ========== Helper Methods ==========

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.println("\nSelect new Role:");
            System.out.println("1. ADMIN");
            System.out.println("2. STAFF");
            System.out.println("3. TENANT");
            System.out.println("0. Cancel");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { return "ADMIN"; }
                case "2" -> { return "STAFF"; }
                case "3" -> { return "TENANT"; }
                case "0" -> {
                    System.out.println("Update cancelled.");
                    return null;
                }
                default -> System.out.println("Invalid role. Please try again.");
            }
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.println("\nSelect new Status:");
            System.out.println("1. APPROVED");
            System.out.println("2. PENDING");
            System.out.println("0. Cancel");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { return "APPROVED"; }
                case "2" -> { return "PENDING"; }
                case "0" -> {
                    System.out.println("Update cancelled.");
                    return null;
                }
                default -> System.out.println("Invalid status. Please try again.");
            }
        }
    }

    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("+-----+-----------------+--------------+------------+");
        System.out.printf("| %-3s | %-15s | %-12s | %-10s |%n", "ID", "Username", "Role", "Status");
        System.out.println("+-----+-----------------+--------------+------------+");

        for (User u : users) {
            System.out.printf("| %-3d | %-15s | %-12s | %-10s |%n",
                    u.id(), u.username(), u.role(), u.status());
        }

        System.out.println("+-----+-----------------+--------------+------------+");
        System.out.println("Total: " + users.size() + " user(s)");
    }
}