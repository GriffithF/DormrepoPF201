package main.java;

import main.java.dao.DatabaseConnection;
import main.java.dao.UserDao;
import main.java.model.User;
import main.java.ui.AuthMenu;
import main.java.ui.MainMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();
        AuthMenu authMenu = new AuthMenu(scanner, userDao);
        MainMenu mainMenu = new MainMenu(scanner);

        System.out.println("================================================");
        System.out.println("   DORMITORY / BOARDING HOUSE PAYMENT SYSTEM");
        System.out.println("================================================");
        System.out.println("Welcome! Manage rooms, tenants, and payments.");

        boolean running = true;

        while (running) {
            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> authMenu.register();

                case "2" -> {
                    User loggedInUser = authMenu.login();
                    if (loggedInUser != null) {
                        boolean returnToMain = mainMenu.show(loggedInUser);
                        if (!returnToMain) {
                            running = false;
                        }
                    }
                }

                case "3" -> {
                    System.out.println("\nThank you for using the system. Goodbye!");
                    running = false;
                }

                default -> System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }

        scanner.close();
    }
}