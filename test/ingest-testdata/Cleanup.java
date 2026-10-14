import java.sql.*;

/** 清理引接性能测试数据:达梦测试主题/方案/目标表 + MySQL 实例表 t990000001~t990000010 */
public class Cleanup {
    static final String DM_URL = "jdbc:dm://127.0.0.1:5236?compatibleMode=mysql";
    static final String MY_URL = "jdbc:mysql://127.0.0.1:3306/data_collect?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    static final String TOPIC_CODE = "TOPIC_INGEST_PERF_TEST";
    static final String SCHEME_NAME = "性能测试-660万行";
    static final String TABLE_NAME = "t_topic_ingest_perf_test";
    static final long INST_BASE = 990000001;
    static final int INST_COUNT = 10;

    public static void main(String[] args) throws Exception {
        Class.forName("dm.jdbc.driver.DmDriver");
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection c = DriverManager.getConnection(DM_URL, "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT ID FROM T_TOPIC_CONFIG WHERE TOPIC_CODE='" + TOPIC_CODE + "'");
            while (rs.next()) {
                long id = rs.getLong(1);
                st.executeUpdate("DELETE FROM T_TOPIC_FIELD WHERE TOPIC_ID=" + id);
                st.executeUpdate("DELETE FROM T_TOPIC_CONFIG WHERE ID=" + id);
            }
            st.executeUpdate("DELETE FROM T_SCHEME_DETAIL WHERE SCHEME_ID IN (SELECT ID FROM T_SCHEME WHERE SCHEME_NAME='" + SCHEME_NAME + "')");
            st.executeUpdate("DELETE FROM T_SCHEME WHERE SCHEME_NAME='" + SCHEME_NAME + "'");
            st.executeUpdate("DELETE FROM T_CJ_CONFIG WHERE YB_NAME='引接性能测试采集'");
            try { st.execute("DROP TABLE \"" + TABLE_NAME + "\""); } catch (Exception ignore) { }
            // 历史遗留：早期测试表建在 ETL schema，一并清理
            try { st.execute("DROP TABLE ETL.\"" + TABLE_NAME + "\""); } catch (Exception ignore) { }
            System.out.println("[DM] 测试主题/采集配置/方案/明细/目标表 已清理");
        }

        try (Connection c = DriverManager.getConnection(MY_URL, "root", "root");
             Statement st = c.createStatement()) {
            for (int i = 0; i < INST_COUNT; i++) {
                st.execute("DROP TABLE IF EXISTS `t" + (INST_BASE + i) + "`");
            }
            System.out.println("[MySQL] 实例表 t" + INST_BASE + "~t" + (INST_BASE + INST_COUNT - 1) + " 已删除");
        }
        System.out.println("清理完成");
    }
}
