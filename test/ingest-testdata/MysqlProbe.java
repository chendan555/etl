import java.sql.*;

/** 探测 MySQL data_collect 库:库是否存在、t% 实例表结构样例 */
public class MysqlProbe {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
        try (Connection c = DriverManager.getConnection(url, "root", "root")) {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SHOW DATABASES LIKE 'data_collect'")) {
                System.out.println("data_collect exists: " + rs.next());
            }
        }
        String url2 = url + "&rewriteBatchedStatements=true";
        try (Connection c = DriverManager.getConnection(url2, "root", "root")) {
            c.setCatalog("data_collect");
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT table_name, table_rows, ROUND(data_length/1024/1024) mb " +
                         "FROM information_schema.tables WHERE table_schema='data_collect' AND table_name LIKE 't%' " +
                         "ORDER BY table_rows DESC LIMIT 10")) {
                System.out.println("\n== t% tables (top by rows)");
                boolean any = false;
                while (rs.next()) { any = true; System.out.println(rs.getString(1) + " rows~" + rs.getLong(2) + " " + rs.getLong(3) + "MB"); }
                if (!any) System.out.println("(none)");
            }
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT table_name FROM information_schema.tables " +
                         "WHERE table_schema='data_collect' AND table_name LIKE 't%' ORDER BY table_rows DESC LIMIT 1")) {
                if (rs.next()) {
                    String t = rs.getString(1);
                    try (ResultSet rs2 = st.executeQuery("SHOW CREATE TABLE data_collect.`" + t + "`")) {
                        if (rs2.next()) System.out.println("\n== DDL of " + t + "\n" + rs2.getString(2));
                    }
                }
            }
        }
    }
}
