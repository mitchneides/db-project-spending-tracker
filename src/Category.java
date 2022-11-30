import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Category {
  public void displayCategoryID(Connection conn) throws SQLException {
    Statement stmt = null;
    String command = "SELECT * FROM item_categories";


    try {
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(command);
      System.out.println("Here are all categories we have:");
      while(rs.next()) {
        System.out.println(rs.getString(1)+". " + rs.getString(2));
      }
    }
      finally {
      if (stmt != null) { stmt.close(); }
    }
  }

  public int getCategory(Connection conn) throws SQLException {

    int catagorySel = 0;
    displayCategoryID(conn);
    while (true){
      Scanner userIn = new Scanner(System.in);
      System.out.print("Please select one of the categories: ");
      try {
        catagorySel = Integer.valueOf(userIn.nextLine());
      } catch (Exception e) {
        System.out.println();
        System.out.println("Please enter one of the specified menu options");
        continue;
      }
      if(catagorySel == 0 || catagorySel > 7){
        System.out.println("Please enter one of the specified menu options");
      }
      else{
        return catagorySel;
      }
    }
  }
}
