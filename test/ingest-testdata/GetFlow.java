import java.sql.*;
public class GetFlow {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID, NAME, CATEGORIES_ID, STATUS FROM T_TRANSFORMATION ORDER BY ID")) {
            while (rs.next()) System.out.println("trans id=" + rs.getLong(1) + " name=" + rs.getString(2)
                    + " cat=" + rs.getInt(3) + " status=" + rs.getInt(4));
        }
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT FLOW_JSON FROM T_TRANSFORMATION WHERE ID=6")) {
            if (rs.next()) {
                String json = rs.getString(1);
                System.out.println("== flowJson length=" + json.length());
                System.out.println(json.length() > 6000 ? json.substring(0, 6000) : json);
            }
        }
    }
}
