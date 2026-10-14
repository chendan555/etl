import java.sql.*;

/** 探测达梦主库:数据源配置、主题/字段、方案现状 */
public class Probe {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:dm://127.0.0.1:5236?compatibleMode=mysql";
        try (Connection c = DriverManager.getConnection(url, "CASIC_ASSESS", "123456789")) {
            System.out.println("connected as " + c.getMetaData().getUserName());
            String schema = findSchema(c);
            System.out.println("using schema: " + schema);

            dump(c, schema, "T_DATABASE",
                    "ID,NAME,DB_TYPE,HOST,PORT,DATABASE_NAME,USERNAME,PASSWORD,SID,STATUS");
            dumpTopics(c, schema);
            dump(c, schema, "T_CJ_CONFIG", "ID,YB_NAME,YB_LIST");
            dump(c, schema, "T_SCHEME", "ID,SCHEME_NAME,CJ_LIST_ID,STATUS");
            dump(c, schema, "T_SCHEME_DETAIL", "ID,SCHEME_ID,YB_ID,YB_NAME,YB_LIST,STATUS");
        }
    }

    static String findSchema(Connection c) throws Exception {
        for (String s : new String[]{"CASIC_ASSESS", "ETL", "SYSDBA"}) {
            try (Statement st = c.createStatement()) {
                st.executeQuery("SELECT 1 FROM " + s + ".T_SCHEME");
                return s;
            } catch (Exception ignore) { }
        }
        throw new RuntimeException("T_SCHEME not found in any candidate schema");
    }

    static void dump(Connection c, String schema, String table, String cols) throws Exception {
        System.out.println("\n== " + table);
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT " + cols + " FROM " + schema + "." + table)) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    if (i > 1) sb.append(" | ");
                    sb.append(md.getColumnName(i)).append("=").append(rs.getString(i));
                }
                System.out.println(sb);
            }
        }
    }

    static void dumpTopics(Connection c, String schema) throws Exception {
        System.out.println("\n== T_TOPIC_CONFIG + T_TOPIC_FIELD");
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT t.ID, t.TOPIC_NAME, t.TOPIC_CODE, t.TABLE_NAME, COUNT(f.ID) FIELDS " +
                     "FROM " + schema + ".T_TOPIC_CONFIG t LEFT JOIN " + schema + ".T_TOPIC_FIELD f ON f.TOPIC_ID = t.ID " +
                     "GROUP BY t.ID, t.TOPIC_NAME, t.TOPIC_CODE, t.TABLE_NAME ORDER BY t.ID")) {
            while (rs.next()) {
                System.out.println("topic id=" + rs.getLong(1) + " name=" + rs.getString(2)
                        + " code=" + rs.getString(3) + " table=" + rs.getString(4)
                        + " fields=" + rs.getInt(5));
            }
        }
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT TOPIC_ID, FIELD_NAME, FIELD_TYPE, COMPLEX_TYPE, ARRAY_INDEX, SUB_INDEX, SORT_ORDER " +
                     "FROM " + schema + ".T_TOPIC_FIELD ORDER BY TOPIC_ID, SORT_ORDER")) {
            while (rs.next()) {
                System.out.println("  field topic=" + rs.getLong(1) + " name=" + rs.getString(2)
                        + " type=" + rs.getString(3) + " complex=" + rs.getString(4)
                        + " arrIdx=" + rs.getInt(5) + " subIdx=" + rs.getInt(6) + " sort=" + rs.getInt(7));
            }
        }
    }
}
