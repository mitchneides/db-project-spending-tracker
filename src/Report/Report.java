package Report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.*;

public class Report {

    public static void reportRemainingMonthlyBudget(Connection conn, int userID) throws SQLException {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));

        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int daysInMonth = yearMonthObject.lengthOfMonth();

        String command = "CALL remainingBudget(" + userID + ", '" + year + "-" + month + "-01', '" +
                                                year + "-" + month + "-" + daysInMonth + "')";

        Statement stmt = null;
        double remaining = Double.MAX_VALUE;
        double monthly_allowance = Double.MAX_VALUE;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                monthly_allowance = Double.valueOf(rs.getString(2));
                if (rs.getString(1) == null) {
                    remaining = monthly_allowance;
                } else {
                    remaining = Double.valueOf(rs.getString(1));
                }
            }
            // display report
            if (remaining < Double.MAX_VALUE && monthly_allowance < Double.MAX_VALUE) {
                System.out.println("\n************************************************");
                System.out.println("Report for " + year + "-" + month);
                System.out.println("Monthly allowance: " + monthly_allowance);
                System.out.println("Remaining budget: " + remaining);
                System.out.println("************************************************");
            } else {
                System.out.println("\nERROR retrieving remaining monthly budget. Please try again...");
            }
        } catch (Exception e) {
            System.out.println("\nERROR retrieving remaining monthly budget. Please try again...");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public static Map<Integer, String> getAllGroceryTypes(Connection conn, String command)
            throws SQLException {
        Statement stmt = null;
        Map<Integer, String> gTypes = new HashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                gTypes.put(Integer.valueOf(rs.getString(1)), rs.getString(2));
            }
            return gTypes;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public static int getGTypeChoice(Map<Integer, String> allGTypes, Connection conn) throws SQLException {
        while (true) {
            // display grocery types
            System.out.println();
            for (Integer id : allGTypes.keySet()) {
                System.out.println("ID: " + id + " | Grocery Type: " + allGTypes.get(id));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the ID that matches your desired grocery type\n" +
                    "(Or enter -1 to cancel): ");

            Integer gtID;
            try {
                gtID = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter an ID from the list, or enter -1 to cancel");
                System.out.println();
                continue;
            }
            if (allGTypes.keySet().contains(gtID)) {
                return gtID;
            }  else if (gtID == -1) {
                return -1;
            } else {
                System.out.println();
                System.out.println("Input was not recognized as a valid ID. " +
                        "Please try again...");
                System.out.println();
            }
        }
    }

    public static void bestVendorToBuy(Connection conn) throws SQLException {
        Map<Integer, String> allGTypes = new HashMap<>();
        try {
            String loadString = "SELECT * FROM grocery_types;";
            allGTypes = getAllGroceryTypes(conn, loadString);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not retrieve grocery types");
        }

        int gType = getGTypeChoice(allGTypes, conn);
        if (gType == -1) {
            return;
        } else {
            String command = "CALL avg_per_visit(" + gType + ")";

            Statement stmt = null;
            String best = "";
            boolean first = true;
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(command);
                System.out.println("\n*******************************************************************************");
                System.out.println("Here are the average amounts spent on 1 item per shopping trip for each vendor\n");
                while (rs.next()) {
                    String name = rs.getString(1);
                    Double avgPrice = Double.valueOf(rs.getString(2));
                    System.out.printf("\nVendor: " + name + " | Avg. amount spent: %.2f", avgPrice);
                    if (first) {
                        best += name;
                        first = false;
                    }
                }
                System.out.println("\n\n\tWe recommend that you buy from: " + best);
                System.out.println("*******************************************************************************");
            } catch (Exception e) {
                System.out.println("\nERROR retrieving average prices. Please try again...");
            } finally {
                if (stmt != null) { stmt.close(); }
            }
        }
    }

    public static int getUserChoice() {
        System.out.println();
        System.out.println("ID: 1 | Report: Get remaining monthly budget");
        System.out.println("ID: 2 | Report: Find best vendor to buy a given grocery type");
        Scanner userIn = new Scanner(System.in);
        System.out.print("\nPlease enter the ID that matches the report that you would like\n" +
                         "(Or enter -1 to cancel and return to main menu): ");

        int choice = Integer.MAX_VALUE;
        try {
            choice = Integer.valueOf(userIn.nextLine());
        } catch (Exception e) {
            System.out.println();
            System.out.println("\nPlease enter an integer ID from the list, or enter -1 to cancel");
            System.out.print("Your choice: ");
        }
        return choice;
    }

    public static void start(Connection conn, int userID) throws SQLException {
        while (true) {
            // ask user which report
            int choice = getUserChoice();
            if (choice == Integer.MAX_VALUE) {
                continue;
            } else if (choice == -1) {
                return;
            } else if (choice == 1) {
                // 1- show remaining monthly budget
                reportRemainingMonthlyBudget(conn, userID);
            } else if (choice == 2) {
                // 2- best vendor from which to buy _____
                bestVendorToBuy(conn);
            } else {
                System.out.println("\nPlease enter an integer ID from the list, or enter -1 to cancel");
                continue;
            }
        }
    }
}
