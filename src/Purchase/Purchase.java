package Purchase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;


public class Purchase {
    private static final int GROCERY_ID = 3;

    private static int addReceiptItem(Connection conn, String command) throws SQLException {
        Statement stmt = null;
        int riid = Integer.MIN_VALUE;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            System.out.println();
            System.out.println("[+] Receipt item added");

            String command2 = "SELECT max(riid) from receipt_item;";
            ResultSet rs = st.executeQuery(command2);
            while(rs.next()) {
                riid = Integer.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("ERROR: could not insert receipt item into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return riid;
    }

    private static String createTransactCommand(Connection conn, int userId, int itemID) throws SQLException {
        Scanner uIn = new Scanner(System.in);
        String price = "";
        while (true) {
            // ask for price
            System.out.print("\nPlease enter the price of the purchase (format: dollars.cents): ");
            try {
                Double p = Double.valueOf(uIn.nextLine());
                price += p;
                break;
            } catch (Exception e) {
                System.out.println("Please ensure your input is formatted correctly!");
            }
        }

        // ask for vendor id
        Vendor v = new Vendor();
        int vid = v.getVendor(conn);

        String date = "";
        while (true) {
            // ask for date
            System.out.print("\nPlease enter the date of the purchase (format: yyyy-mm-dd): ");
            try {
                String dIn = uIn.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                dateFormat.parse(dIn.toString());
                date += dIn;
                System.out.println(date);
                break;
            } catch (ParseException pe) {
                System.out.println("Please ensure your input is formatted correctly!");
            }
        }
        return "INSERT INTO transact (user_id, vendor_id, item_id, transact_date, price)" +
                " VALUES (" + userId + ", " + vid + ", " + itemID + ", '" + date + "', " + price + ")";
    }

    private static int addTransact(Connection conn, int userID, int itemID) throws SQLException {
        String command = createTransactCommand(conn, userID, itemID);
        Statement stmt = null;
        int tid = Integer.MIN_VALUE;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            System.out.println();
            System.out.println("[+] Transaction added");

            String command2 = "SELECT max(tid) from transact;";
            ResultSet rs = st.executeQuery(command2);
            while(rs.next()) {
                tid = Integer.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("ERROR: could not insert transaction into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return tid;
    }

    public static void start(Connection conn, int userID) throws SQLException {
        // ask item name
        Scanner userIn = new Scanner(System.in);
        System.out.println();
        System.out.print("Please enter the name of the purchased item: ");
        String itemName = userIn.nextLine();

        // ask category
        Category category = new Category();
        int cat = category.getCategory(conn);

        String command = "";

        // if grocery ask which grocery type or create new
        // and ask qty
        if (cat == GROCERY_ID) {
            GroceryType gType = new GroceryType();
            int gtID = gType.getGroceryType(conn);

            System.out.println();
            System.out.print("Please enter the number of items purchased for this receipt: ");
            Integer qty = Integer.valueOf(userIn.nextLine());

            command += "INSERT INTO receipt_item (name, category, purchase_qty, gt_id)" +
                       " VALUES ('" + itemName + "', " + cat + ", " + qty + ", " + gtID + ")";
        } else {
            command += "INSERT INTO receipt_item (name, category, purchase_qty, gt_id)" +
                       " VALUES ('" + itemName + "', " + cat + ", null, null)";
        }

        // add receipt item (triggers update to grocery type qty)
        int itemID = addReceiptItem(conn, command);
        if (itemID == Integer.MIN_VALUE) {
            System.out.println("Unable to create receipt item at this time. Please try again...");
            return;
        }

        // add to transact table
        addTransact(conn, userID, itemID);


    }
}
