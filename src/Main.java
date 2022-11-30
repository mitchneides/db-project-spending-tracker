import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    // program entry point
    public static void main(String[] args) throws SQLException {
        // Login and connect to DB
        DBConnect app = new DBConnect();
        Connection conn = app.getConnInstance();

        // welcome/user login
        System.out.println();
        System.out.println("********* Welcome to the Spending Tracker *********");
        System.out.println();
        AppLogin appLogin = new AppLogin();
        appLogin.getUserID(conn);

        // begin execution flow
//        while(true) {
//
//        }
    }
}
