import java.sql.*;
public class CheckSysdba {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement()) {
            // SYSDBA schema 的三张关键表
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM SYSDBA.T_TRANSFORMATION");
            rs.next();
            System.out.println("SYSDBA.T_TRANSFORMATION rows=" + rs.getInt(1));
            rs = st.executeQuery("SELECT ID, NAME FROM SYSDBA.T_TRANSFORMATION WHERE ID IN (209,210) OR NAME LIKE '%性能%'");
            while (rs.next()) System.out.println("  trans id=" + rs.getLong(1) + " name=" + rs.getString(2));
            rs = st.executeQuery("SELECT COUNT(*) FROM SYSDBA.T_EXECUTION_LOG");
            rs.next();
            System.out.println("SYSDBA.T_EXECUTION_LOG rows=" + rs.getInt(1));
            rs = st.executeQuery("SELECT ID, TRANS_ID, TRANS_NAME, STATUS, START_TIME, ROW_COUNT, ERROR_MSG FROM SYSDBA.T_EXECUTION_LOG ORDER BY ID DESC");
            int n = 0;
            while (rs.next() && n < 15) {
                String err = rs.getString(7);
                System.out.println("  log id=" + rs.getLong(1) + " trans=" + rs.getLong(2) + " " + rs.getString(3)
                        + " status=" + rs.getInt(4) + " rows=" + rs.getLong(6) + " " + rs.getTimestamp(5)
                        + (err != null ? "\n      ERROR: " + (err.length() > 400 ? err.substring(0, 400) + "..." : err) : ""));
                n++;
            }
            rs = st.executeQuery("SELECT ID, SCHEME_NAME, STATUS FROM SYSDBA.T_SCHEME ORDER BY ID DESC");
            int m = 0;
            System.out.println("SYSDBA.T_SCHEME (最近几条):");
            while (rs.next() && m < 6) {
                System.out.println("  scheme id=" + rs.getLong(1) + " " + rs.getString(2) + " status=" + rs.getInt(3));
                m++;
            }
        }
    }
}
