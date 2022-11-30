package Purchase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GroceryType {
    public Map<Integer, String> getAllGroceryTypes(Connection conn, String command)
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

    public int createGType(Connection conn, String gtName) throws SQLException {
        String command = "INSERT INTO grocery_types (gt_name, remaining_qty) VALUES ('" + gtName + "', " + 0 + ")";
        Statement stmt = null;
        int gtID = Integer.MIN_VALUE;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            String command2 = "SELECT max(gt_id) from grocery_types;";
            ResultSet rs = st.executeQuery(command2);
            while(rs.next()) {
                gtID = Integer.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("ERROR: could not insert grocery type into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return gtID;
    }

    public int getUserChoice(Map<Integer, String> allGTypes, Connection conn) throws SQLException {
        while (true) {
            // display grocery types
            System.out.println();
            for (Integer id : allGTypes.keySet()) {
                System.out.println("ID: " + id + " | Grocery Type: " + allGTypes.get(id));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the ID that matches your desired grocery type\n" +
                    "(Or enter -1 to create a new grocery type): ");

            Integer gtID;
            try {
                gtID = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter an ID from the list, or enter -1 to create a new grocery type");
                System.out.println();
                continue;
            }
            if (allGTypes.keySet().contains(gtID)) {
                return gtID;
            }  else if (gtID == -1) {
                // create new grocery type
                System.out.println();
                System.out.print("Please enter the name of the new grocery type: ");
                String gtName = userIn.nextLine();

                // call function to create a new user (returns int)
                gtID = createGType(conn, gtName);
                if (gtID > Integer.MIN_VALUE) {
                    return gtID;
                } else {
                    System.out.println("Unable to create a new grocery type at this time. Please try again...");
                    continue;
                }
            } else {
                System.out.println();
                System.out.println("Input was not recognized as a valid ID. " +
                        "Please try again...");
                System.out.println();
            }
        }
    }


    public int getGroceryType(Connection conn) throws SQLException {
        // find all users in db
        Map<Integer, String> allGTypes = new HashMap<>();
        try {
            String loadString = "SELECT * FROM grocery_types;";
            allGTypes = getAllGroceryTypes(conn, loadString);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not retrieve grocery types");
        }

        // ask user to choose a grocery type or create new
        int gType = getUserChoice(allGTypes, conn);
        System.out.println(gType);
        return gType;
    }
}
