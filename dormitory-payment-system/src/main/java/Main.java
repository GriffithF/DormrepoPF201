import dao.DatabaseInitializer;
import dao.UserDao;
import ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseInitializer.initialize();

        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();

        Authentication auth = new Authentication(scanner, userDao);
        auth.start();

        scanner.close();
    }
}