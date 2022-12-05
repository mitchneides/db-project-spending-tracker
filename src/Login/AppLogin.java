package Login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AppLogin {
    private Map<Integer, String> getUsers(Connection conn, String command)
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

    private int createUser(Connection conn, String username, Double budget) throws SQLException {
        String command = "INSERT INTO user (name, monthly_allowance) VALUES ('" + username + "', " + budget + ")";
        Statement stmt = null;
        int userID = Integer.MIN_VALUE;
        try {
            Statement st = conn.createStatement();
            int status = st.executeUpdate(command);
            String command2 = "SELECT max(uid) from user;";
            ResultSet rs = st.executeQuery(command2);
            while(rs.next()) {
                userID = Integer.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("ERROR: could not insert user into database");
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return userID;
    }

    private int userIdentification(Map<Integer, String> allUsers, Connection conn) throws SQLException {
        while (true) {
            // display users
            for (Integer id : allUsers.keySet()) {
                System.out.println("ID: " + id + " | Username: " + allUsers.get(id));
            }

            Scanner userIn = new Scanner(System.in);
            System.out.println();
            System.out.print("Please enter the user ID that matches your username\n" +
                    "(Or enter -1 to create a new user): ");

            Integer uID;
            try {
                uID = Integer.valueOf(userIn.nextLine());
            } catch (Exception e) {
                System.out.println();
                System.out.println("Please enter an ID from the list, or enter -1 to create a new user");
                System.out.println();
                continue;
            }
            if (allUsers.keySet().contains(uID)) {
                return uID;
            } else if (uID == -1) {
                // create new user
                System.out.println();
                System.out.println("Welcome!! We just need a little information from you.");
                System.out.print("Please enter your name: ");
                String userName = userIn.nextLine();

                double budget;
                while (true) {
                    System.out.print("Please enter your monthly spending allowance (format dollars.cents): ");
                    try {
                        budget = Double.valueOf(userIn.nextLine());
                        break;
                    } catch (Exception e) {
                        System.out.println();
                        System.out.println("Please enter a monetary value formatted as dollars.cents");
                        System.out.println();
                        continue;
                    }
                }
                // call function to create a new user (returns int)
                uID = createUser(conn, userName, budget);
                if (uID > Integer.MIN_VALUE) {
                    return uID;
                } else {
                    System.out.println("Unable to create a new user at this time. Please try again...");
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
        int userID = userIdentification(allUsers, conn);
        return userID;
    }
}
