import java.sql.*;

/** 确认数据表实际所在 schema：对比 CASIC_ASSESS 与 ETL 两个 schema 下的 t_topic% 表及行数 */
public class CheckSchema {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             Statement st2 = c.createStatement()) {
            for (String schema : new String[]{"CASIC_ASSESS", "ETL"}) {
                System.out.println("== schema " + schema);
                ResultSet rs = st.executeQuery(
                        "SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER='" + schema + "' AND TABLE_NAME LIKE 'T_TOPIC%' ORDER BY TABLE_NAME");
                int n = 0;
                while (rs.next()) {
                    String t = rs.getString(1);
                    long cnt = -1;
                    try (ResultSet r2 = st2.executeQuery("SELECT COUNT(*) FROM " + schema + ".\"" + t + "\"")) {
                        r2.next();
                        cnt = r2.getLong(1);
                    } catch (Exception ignore) { }
                    System.out.println("  " + t + " rows=" + cnt);
                    n++;
                }
                if (n == 0) System.out.println("  (无 t_topic% 表)");
            }
            System.out.println("== 当前登录用户默认 schema: " + c.getSchema());
        }
    }
}
