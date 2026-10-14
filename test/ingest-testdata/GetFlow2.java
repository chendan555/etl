import java.sql.*;
public class GetFlow2 {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT ID, NAME, LENGTH(FLOW_JSON), FLOW_JSON FROM T_TRANSFORMATION " +
                 "WHERE FLOW_JSON LIKE '%TableInput%' ORDER BY LENGTH(FLOW_JSON)")) {
            while (rs.next()) {
                System.out.println("id=" + rs.getLong(1) + " name=" + rs.getString(2) + " len=" + rs.getInt(3));
                String json = rs.getString(4);
                System.out.println(json);
                break; // 只要最短的一条
            }
        }
    }
}
