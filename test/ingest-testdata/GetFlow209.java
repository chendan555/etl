import java.sql.*;
public class GetFlow209 {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT FLOW_JSON FROM T_TRANSFORMATION WHERE ID=209")) {
            if (rs.next()) System.out.println(rs.getString(1));
        }
    }
}
