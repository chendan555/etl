import java.sql.*;
public class CheckExecLog {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT ID, TRANS_ID, TRANS_NAME, STATUS, START_TIME, END_TIME, ROW_COUNT, ERROR_MSG " +
                 "FROM T_EXECUTION_LOG ORDER BY ID DESC")) {
            int n = 0;
            while (rs.next() && n < 30) {
                String err = rs.getString(8);
                System.out.println("id=" + rs.getLong(1) + " trans=" + rs.getLong(2) + " " + rs.getString(3)
                        + " status=" + rs.getInt(4) + " rows=" + rs.getLong(7)
                        + " " + rs.getTimestamp(5) + "~" + rs.getTimestamp(6)
                        + (err != null ? "\n    ERROR: " + (err.length() > 500 ? err.substring(0, 500) + "..." : err) : ""));
                n++;
            }
        }
    }
}
