package Report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

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

    public static void start(Connection conn, int userID) throws SQLException {
        // ask user which report

        // REPORTS:
        // 1- show remaining monthly budget
        reportRemainingMonthlyBudget(conn, userID);
    }
}
