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
            if (menuChoice == 3) {
                break;
            }
        }

    }
}



// 1. ask for input or generate report
//        assume input
                // ask for consumption or add purchase
//         assume add purchase
// 2. Ask which category
//        if grocery
// 3. Ask which grocery type or create new
// 4. Add steps 2 and 3 to receipt item table WITH TRIGGER ON RECEIPT ITEM TO UPDATE QTY IN grocery type
//        if not grocery
// go directly to receipt item table and create entry
// 5. Ask which vendor or create new
// 6. Ask for date and price
// 7. Add everything to transacts table

// consumption
// ask for the grocery type (display list and ask for PK)
// record to comsumption table