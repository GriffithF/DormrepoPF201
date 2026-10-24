package ui;

import java.util.List;
import java.util.Scanner;

import dao.UserDao;
import model.User;

public class AdminMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public AdminMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void show() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("           ADMIN MENU");
            System.out.println("========================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUserAccount();
                case "4" -> {
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

        System.out.print("\nEnter User ID to approve (or 0 to cancel): ");
        String input = scanner.nextLine().trim();

        int userId;
        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean exists = pending.stream().anyMatch(u -> u.id() == userId);

        if (!exists) {
            System.out.println("User ID " + userId + " is not in the pending list.");
            return;
        }

        boolean success = userDao.approveUser(userId);

        if (success) {
            System.out.println("User ID " + userId + " has been approved successfully.");
        } else {
            System.out.println("Failed to approve user. Please try again.");
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
                    u.id(),
                    u.username(),
                    u.role(),
                    u.status());
        }

        System.out.println("+-----+-----------------+--------------+------------+");
        System.out.println("Total: " + users.size() + " user(s)");
    }
}