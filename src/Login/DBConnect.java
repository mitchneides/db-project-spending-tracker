package Login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class DBConnect {
    private String dbUserName;
    private String dbPassword;
    private final String serverName = "localhost";
    private final int portNumber = 3306;
    private final String dbName = "spending_tracker";



    private void dbLogin() {
        Scanner userIn = new Scanner(System.in);
        System.out.print("Enter MySQL username: ");
        String uName = userIn.nextLine();
        System.out.println();
        System.out.print("Enter MySQL password: ");
        String pWord = userIn.nextLine();
        System.out.println();

        this.dbUserName = uName;
        this.dbPassword = pWord;
    }

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        dbLogin();
        connectionProps.put("user", this.dbUserName);
        connectionProps.put("password", this.dbPassword);
        conn = DriverManager.getConnection("jdbc:mysql://"
                        + this.serverName + ":" + this.portNumber + "/" +
                        this.dbName + "?allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useSSL=false",
                connectionProps);
        return conn;
    }

    public Connection getConnInstance() throws SQLException {
        // connect to DB
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = this.getConnection();
            System.out.println("[+] Connected to database");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }
        return conn;
    }

}
