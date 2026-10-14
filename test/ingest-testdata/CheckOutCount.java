import java.sql.*;
public class CheckOutCount {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM t_perf_test_output")) {
            rs.next();
            System.out.println("t_perf_test_output rows=" + rs.getLong(1));
        }
    }
}
