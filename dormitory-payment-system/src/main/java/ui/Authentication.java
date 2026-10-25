package ui;

import java.util.Optional;
import java.util.Scanner;

import dao.UserDao;
import model.User;

public class Authentication {

    private final Scanner scanner;
    private final UserDao userDao;
    private final AdminMenu adminMenu;
    private final MainMenu mainMenu;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.adminMenu = new AdminMenu(scanner, userDao);
        this.mainMenu = new MainMenu(scanner);
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register();
                case "2" -> loginAndRoute();
                case "3" -> {
                    System.out.println("\nThank you for using the system. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    private void register() {
        System.out.println("\n========================================");
        System.out.println("        REGISTER NEW ACCOUNT");
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

        String role = switch (roleChoice) {
            case "1" -> "ADMIN";
            case "2" -> "STAFF";
            case "3" -> "TENANT";
            default -> {
                System.out.println("Invalid role selection. Registration cancelled.");
                yield null;
            }
        };

        if (role == null) return;

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

    private void loginAndRoute() {
        System.out.println("\n========================================");
        System.out.println("               LOGIN");
        System.out.println("========================================");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        Optional<User> result = userDao.login(username, password);

        if (result.isEmpty()) {
            System.out.println("\nInvalid username or password.");
            return;
        }

        User user = result.get();

        boolean isAdmin = user.role().equalsIgnoreCase("ADMIN");
        boolean isApproved = user.status().equalsIgnoreCase("APPROVED");

        if (!isAdmin && !isApproved) {
            System.out.println("\nLogin failed: Your account status is currently " + user.status() + ".");
            System.out.println("Please wait for an administrator to approve your registration.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("  Welcome, " + user.username() + " (" + user.role() + ")");
        System.out.println("  Account Status: " + user.status());

        if (isAdmin) {
            adminMenu.show();
        } else {
            mainMenu.show(user);
        }
    }
}