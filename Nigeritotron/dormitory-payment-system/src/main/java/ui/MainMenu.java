package main.java.ui;

import main.java.model.User;

import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private User currentUser;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean show(User user) {
        this.currentUser = user;

        while (true) {
            System.out.println("\n========================================");
            System.out.println("   DORMITORY / BH PAYMENT SYSTEM");
            System.out.println("========================================");
            System.out.println("Logged in as: " + currentUser.username()
                    + " [" + currentUser.role() + "]");
            System.out.println("Status      : " + currentUser.status());
            System.out.println("----------------------------------------");
            System.out.println("1. View My Profile");
            System.out.println("2. Logout");
            System.out.println("3. Exit Application");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewProfile();
                case "2" -> {
                    System.out.println("\nYou have been logged out.");
                    return true;
                }
                case "3" -> {
                    System.out.println("\nThank you for using the system. Goodbye!");
                    return false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewProfile() {
        System.out.println("\n--- My Profile ---");
        System.out.println("ID       : " + currentUser.id());
        System.out.println("Username : " + currentUser.username());
        System.out.println("Role     : " + currentUser.role());
        System.out.println("Status   : " + currentUser.status());
        System.out.println("------------------");
    }
}