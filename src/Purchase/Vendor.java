package Purchase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Vendor {
    public Map<Integer, String> getVendors(Connection conn, String command)
            throws SQLException {
        Statement stmt = null;
        Map<Integer, String> vendors = new HashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                vendors.put(Integer.valueOf(rs.getString(1)), rs.getString(2));
            }
            return vendors;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public int createVendor(Connection conn, String vendorName) throws SQLException {
        String command = "INSERT INTO vendor (name) VALUES ('" + vendorName + "')";
        Statement stmt = null;
        int vID = Integer.MIN_VALUE;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            String command2 = "SELECT max(vid) from vendor;";
            ResultSet rs = st.executeQuery(command2);
            while(rs.next()) {
                vID = Integer.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("ERROR: could not insert vendor into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return vID;
    }

    public int getUserChoice(Map<Integer, String> vendors, Connection conn) throws SQLException {
        while (true) {
            // display all vendors
            System.out.println();
            for (Integer id : vendors.keySet()) {
                System.out.println("ID: " + id + " | Name: " + vendors.get(id));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the ID that matches your purchase vendor\n" +
                    "(Or enter -1 to create a new vendor): ");

            Integer vID;
            try {
                vID = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter an ID from the list, or enter -1 to create a new vendor");
                System.out.println();
                continue;
            }
            if (vendors.keySet().contains(vID)) {
                return vID;
            }  else if (vID == -1) {
                // create new grocery type
                System.out.println();
                System.out.print("Please enter the name of the new vendor: ");
                String vName = userIn.nextLine();

                // call function to create a new user (returns int)
                vID = createVendor(conn, vName);
                if (vID > Integer.MIN_VALUE) {
                    return vID;
                } else {
                    System.out.println("Unable to create a new vendor at this time. Please try again...");
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


    public int getVendor(Connection conn) throws SQLException {
        // find all vendors in db
        Map<Integer, String> vendors = new HashMap<>();
        try {
            String loadString = "SELECT * FROM vendor;";
            vendors = getVendors(conn, loadString);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not retrieve vendors");
        }

        // ask user to choose a grocery type or create new
        int vID = getUserChoice(vendors, conn);
        return vID;
    }
}
