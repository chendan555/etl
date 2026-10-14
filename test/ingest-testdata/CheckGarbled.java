import java.sql.*;
/** 确认乱码旧数据已清干净：查所有含测试标识的对象 */
public class CheckGarbled {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT ID, TOPIC_NAME, TOPIC_CODE FROM T_TOPIC_CONFIG WHERE TOPIC_CODE LIKE 'TOPIC_INGEST%' OR TOPIC_NAME LIKE '%性能%'");
            while (rs.next()) System.out.println("topic id=" + rs.getLong(1) + " name=" + rs.getString(2) + " code=" + rs.getString(3));
            rs = st.executeQuery("SELECT ID, YB_NAME, YB_LIST FROM T_CJ_CONFIG WHERE YB_NAME LIKE '%采集%' OR YB_LIST='75' OR YB_LIST='76'");
            while (rs.next()) System.out.println("cj id=" + rs.getLong(1) + " name=" + rs.getString(2) + " ybList=" + rs.getString(3));
            rs = st.executeQuery("SELECT COUNT(*) FROM T_SCHEME WHERE ID IN (18,19)");
            rs.next();
            System.out.println("scheme 18/19 存在数量=" + rs.getInt(1) + " (应为1: 只有新的19)");
        }
    }
}
