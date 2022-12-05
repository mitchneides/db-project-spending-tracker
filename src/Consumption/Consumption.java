package Consumption;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Consumption {
    public static Map<Integer, List<String>> getAllStockedGroceryTypes(Connection conn, String command)
            throws SQLException {
        Statement stmt = null;
        Map<Integer, List<String>> gTypes = new HashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                String qty = rs.getString(3);
                String name = rs.getString(2);
                List<String> vals = new ArrayList<>();
                vals.add(name);
                vals.add(qty);
                gTypes.put(Integer.valueOf(rs.getString(1)), vals);
            }
            return gTypes;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public static List<Integer> getUserChoice(Map<Integer, List<String>> allGTypes, Connection conn) throws SQLException {
        // list format: [id, qty]
        List<Integer> res = new ArrayList<>();
        while (true) {
            System.out.println("\n\tCurrent stock of items: ");
            // display grocery types and their stock
            System.out.println();
            for (Integer id : allGTypes.keySet()) {
                System.out.println("ID: " + id + " | Grocery Type: " + allGTypes.get(id).get(0) +
                                    " | Remaining qty: " + allGTypes.get(id).get(1));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the ID that matches the item you are consuming\n" +
                             "(Or enter -1 to cancel and return to main menu): ");

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
                res.add(gtID);
                res.add(Integer.valueOf(allGTypes.get(gtID).get(1)));
                return res;
            }  else if (gtID == -1) {
                res.add(-1);
                return res;
            } else {
                System.out.println();
                System.out.println("Input was not recognized as a valid ID. " +
                        "Please try again...");
                System.out.println();
            }
        }
    }

    public static void writeConsumption(Connection conn, int gtid, int qty_consumed) throws SQLException {
        String command = "INSERT INTO consumption (grocery_type_id, qty_consumed) " +
                         "VALUES (" + gtid + ", " + qty_consumed + ");";
        Statement stmt = null;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            System.out.println();
            System.out.println("[+] Consumption added");
        } catch (Exception e) {
            System.out.println("ERROR: could not insert consumption into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public void deleteLastConsumption(Connection conn) throws SQLException {
        String command = "select max(cons_id) from consumption;";
        Statement stmt = null;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            int pk = -1;
            while (rs.next()) {
                pk = Integer.valueOf(rs.getString(1));
            }
            String command2 = "DELETE FROM consumption WHERE (cons_id = " + pk +");";
            int deleted = st.executeUpdate(command2);
            System.out.println();
            System.out.println("[-] Consumption deleted");
        } catch (Exception e) {
            System.out.println("ERROR: could not delete consumption from database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public static void start(Connection conn, int userID) throws SQLException {
        while (true) {
            // get user's grocery stock (grocery type list where qty > 0)
            String command = "SELECT * FROM grocery_types WHERE remaining_qty > 0;";
            Map<Integer, List<String>> stock = getAllStockedGroceryTypes(conn, command);

            // get user's consumption item
            // list format: [id, qty]
            List<Integer> choice = getUserChoice(stock, conn);
            if (choice.get(0) == -1) {
                return;
            }

            int itemPK = choice.get(0);
            int old_qty = choice.get(1);
            int new_qty = -1;
            // get/validate consumption quantity
            int qty_consumed = -1;
            while (true) {
                Scanner userIn = new Scanner(System.in);
                System.out.print("\nPlease enter the quantity consumed (or -1 to cancel): ");
                try {
                    qty_consumed = Integer.parseInt(userIn.nextLine());
                } catch (Exception e) {
                    System.out.println("\nAn integer must be entered. Please try again...");
                    continue;
                }
                if (qty_consumed > choice.get(1)) {
                    System.out.println("\nQuantity cannot exceed the amount you have stocked. Please try again...");
                    continue;
                }
                if (qty_consumed < 1 && qty_consumed != -1) {
                    System.out.println("\nQuantity consumed must be at least 1. Please try again...");
                    continue;
                }
                if (qty_consumed == -1) {
                    qty_consumed = 0;
                    break;
                } else {
                    new_qty = old_qty - qty_consumed;
                    // alert user that they are low if qty is below 3
                    if (new_qty < 3) {
                        System.out.println("\n*********************************************************");
                        System.out.println("ALERT: You're stock is running low! You may want to add this item" +
                                " to your shopping list!");
                        System.out.println("*********************************************************");
                    }
                    break;
                }
            }
            // write to consumption table
            if (qty_consumed != 0) {
                writeConsumption(conn, itemPK, qty_consumed);
            }
        }
    }
}
