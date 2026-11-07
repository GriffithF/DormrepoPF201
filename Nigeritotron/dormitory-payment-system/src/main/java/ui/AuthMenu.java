package main.java.ui;

import main.java.dao.UserDao;
import main.java.model.User;

import java.util.Optional;
import java.util.Scanner;

public class AuthMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public AuthMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void register() {
        System.out.println("\n========================================");
        System.out.println("         REGISTER NEW ACCOUNT");
        System.out.println("========================================");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        if (userDao.usernameExists(username)) {
            System.out.println("Username already exists. Please choose another.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.println("\nSelect Role:");
        System.out.println("1. ADMIN");
        System.out.println("2. STAFF");
        System.out.println("3. TENANT");
        System.out.print("Enter choice (1-3): ");
        String roleChoice = scanner.nextLine().trim();

        String role;
        switch (roleChoice) {
            case "1" -> role = "ADMIN";
            case "2" -> role = "STAFF";
            case "3" -> role = "TENANT";
            default -> {
                System.out.println("Invalid role selection. Registration cancelled.");
                return;
            }
        }

        boolean success = userDao.register(username, password, role);

        if (success) {
            System.out.println("\nRegistration successful!");
            System.out.println("  Username : " + username);
            System.out.println("  Role     : " + role);
            System.out.println("  Status   : PENDING");
        } else {
            System.out.println("\nRegistration failed. Please try again.");
        }
    }

    public User login() {
        System.out.println("\n========================================");
        System.out.println("              LOGIN");
        System.out.println("========================================");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return null;
        }

        Optional<User> result = userDao.login(username, password);

        if (result.isPresent()) {
            User user = result.get();
            System.out.println("\nLogin successful!");
            System.out.println("  Welcome, " + user.username() + " (" + user.role() + ")");
            System.out.println("  Account Status: " + user.status());
            return user;
        } else {
            System.out.println("\nInvalid username or password.");
            return null;
        }
    }
}