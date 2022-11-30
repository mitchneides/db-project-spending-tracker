import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class DBConnectOLD {
    private String dbUserName;
    private String dbPassword;
    private final String serverName = "localhost";
    private final int portNumber = 3306;
    private final String dbName = "spending_tracker";
    private String name;
    private int uid;
    private double monthly_allowance;

    public void dbLogin() {

        /* COMMENTED OUT FOR DEV
        Scanner userIn = new Scanner(System.in);
        System.out.print("Enter MySQL username: ");
        String uName = userIn.nextLine();
        System.out.println();
        System.out.print("Enter MySQL password: ");
        String pWord = userIn.nextLine();
        System.out.println();

        this.userName = uName;
        this.password = pWord;
         */

        this.dbUserName = "root";
        this.dbPassword = "MgN130458!";
    }

    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        dbLogin();
        connectionProps.put("user", this.dbUserName);
        connectionProps.put("password", this.dbPassword);
        conn = DriverManager.getConnection("jdbc:mysql://"
                        + this.serverName + ":" + this.portNumber + "/" +
                        this.dbName + "?characterEncoding=UTF-8&useSSL=false",
                connectionProps);
        return conn;
    }

    public List<String> getUsers(Connection conn, String command)
            throws SQLException {
        Statement stmt = null;
        List<String> users = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(command);
            while(rs.next()) {
                users.add(rs.getString(2));
            }
            return users;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    public void appLogin(Connection conn) {
        // retrieve users
        List<String> users = new ArrayList<>();
        try {
            String loadString = "SELECT * FROM user;";
            users = this.getUsers(conn, loadString);
            System.out.println("Retrieved users");
        } catch (SQLException e) {
            System.out.println("Error retrieving data from user table");
            e.printStackTrace();
            return;
        }

        Scanner userIn = new Scanner(System.in);
        System.out.print("Enter your Spending Tracker username: ");
        String uName = userIn.nextLine();

        if (users.contains(uName)) {
            ///////////////////////////// come back
        }



        System.out.println();
        System.out.print("Enter MySQL password: ");
        String pWord = userIn.nextLine();
        System.out.println();

//        this.userName = uName;
//        this.password = pWord;
    }

    public void run() throws SQLException {
        // connect to DB
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = this.getConnection();
            System.out.println("Connected to database");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
            return;
        }

        // load users
        List<String> users = new ArrayList<>();
        try {
            String loadString = "SELECT * FROM user;";
            users = this.getUsers(conn, loadString);
            System.out.println("Retrieved users");
        } catch (SQLException e) {
            System.out.println("Error retrieving data from user table");
            e.printStackTrace();
            return;
        }
    }

    // program entry point
    public static void main(String[] args) throws SQLException {
        DBConnectOLD app = new DBConnectOLD();
        app.run();
    }

}
