import Consumption.Consumption;
import Login.AppLogin;
import Login.DBConnect;
import Purchase.GroceryType;
import Purchase.Purchase;
import Report.Report;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static int getMenuChoice() {
        int choice = Integer.MAX_VALUE;
        while(true) {
            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.println("How can we help you today? Choose an option from below");
            System.out.println();
            System.out.println("1: Record data");
            System.out.println("2: Generate report");
            System.out.println("3: Exit");
            System.out.print("Your choice: ");
            try {
                choice = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter one of the specified menu options");
                continue;
            }
            if (choice == 1) {
                return 1;
            } else if (choice == 2) {
                return 2;
            } else if (choice == 3) {
                return 3;
            } else {
                System.out.println();
                System.out.println("Please enter one of the specified menu options");
                continue;
            }
        }
    }

    public static int getInputChoice() throws SQLException {
        int choice = Integer.MAX_VALUE;
        while(true) {
            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.println("What type of data would you like to input? Choose an option from below");
            System.out.println();
            System.out.println("1: Record expense");
            System.out.println("2: Record consumption");
            System.out.print("\nYour choice: ");
            try {
                choice = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter one of the specified menu options");
                continue;
            }
            if (choice == 1) {
                return 1;
            } else if (choice == 2) {
                return 2;
            } else {
                System.out.println();
                System.out.println("Please enter one of the specified menu options");
                continue;
            }
        }
    }

    // program entry point
    public static void main(String[] args) throws SQLException {
        // Login and connect to DB
        DBConnect app = new DBConnect();
        Connection conn = app.getConnInstance();

        // welcome
        System.out.println();
        System.out.println("********* Welcome to the Spending Tracker *********");
        System.out.println();

        // App user login/create user
        AppLogin appLogin = new AppLogin();
        int userID = appLogin.getUserID(conn);

        // begin execution flow
        while (true) {
            // display menu
            int menuChoice = getMenuChoice();
            if (menuChoice == 1) {
                // ask for purchase or consumption input
                int inputChoice = getInputChoice();
                if (inputChoice == 1) {
                    // input purchase
                    Purchase purchase = new Purchase();
                    purchase.start(conn, userID);

                } else {
                    // input consumption
                    Consumption consumption = new Consumption();
                    consumption.start(conn, userID);

                }
            } else if (menuChoice == 2) {
                // generate report
                Report report = new Report();
                report.start(conn, userID);

            } else if (menuChoice == 3) {
                // exit system
                System.out.println();
                System.out.println("********* Thanks for stopping by :) *********");
                break;
            }
        }
    }
}
