import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AppLogin {
    public Map<Integer, String> getUsers(Connection conn, String command)
            throws SQLException {
        Statement stmt = null;
        Map<Integer, String> users = new HashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                users.put(Integer.valueOf(rs.getString(1)), rs.getString(2));
            }
            return users;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public int userIdentification(Map<Integer, String> allUsers) {
        while (true) {
            // display users
            for (Integer id : allUsers.keySet()) {
                System.out.println("ID: " + id + " | Username: " + allUsers.get(id));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the user ID that matches your username\n" +
                    "(Or enter -1 to create a new user): ");

            Integer uID = Integer.valueOf(userIn.nextLine());
            if (allUsers.keySet().contains(uID)) {
                return uID;
            } else if (uID == -1) {
                // create new user
                continue;
            } else {
                System.out.println("'" + userIn + "' was not recognized. Please try again...");
            }
        }
    }

    public int getUserID(Connection conn) throws SQLException {
        // find all users in db
        Map<Integer, String> allUsers = new HashMap<>();
        try {
            String loadString = "SELECT * FROM user;";
            allUsers = getUsers(conn, loadString);
        } catch (SQLException e) {
            System.out.println("ERROR: Could not retrieve users");
        }

        // ask user to identify themselves
        int userID = userIdentification(allUsers);

        return userID;
    }
}
