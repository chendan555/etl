import java.sql.*;
public class CheckDetails {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID, SCHEME_ID, YB_ID, YB_NAME, YB_LIST, STATUS FROM T_SCHEME_DETAIL ORDER BY SCHEME_ID, ID")) {
            while (rs.next())
                System.out.println("detail id=" + rs.getLong(1) + " scheme=" + rs.getLong(2) + " ybId=" + rs.getLong(3)
                        + " ybName=" + rs.getString(4) + " ybList=" + rs.getString(5) + " status=" + rs.getInt(6));
        }
    }
}
