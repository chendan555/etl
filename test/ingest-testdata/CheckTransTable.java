import java.sql.*;
public class CheckTransTable {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT OWNER, TABLE_NAME FROM ALL_TABLES WHERE TABLE_NAME LIKE 'T_TRANSFORMATION%' ORDER BY OWNER");
            while (rs.next()) System.out.println(rs.getString(1) + "." + rs.getString(2));
            // CASIC_ASSESS 里 210 是否真的还在
            rs = st.executeQuery("SELECT ID, NAME, STATUS FROM T_TRANSFORMATION WHERE ID >= 200 ORDER BY ID");
            while (rs.next()) System.out.println("cassic id=" + rs.getLong(1) + " " + rs.getString(2) + " status=" + rs.getInt(3));
        }
    }
}
