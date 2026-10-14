import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.sql.*;

/** ① 看达梦 ETL 目标表列结构 ② 解一条现有真实 msgpack 验证载荷格式 */
public class CheckPayload {
    public static void main(String[] args) throws Exception {
        // ① DM ETL 目标表列结构
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789")) {
            DatabaseMetaData md = c.getMetaData();
            System.out.println("== ETL.t_topic_qy_yj18btrajectorydata columns");
            try (ResultSet rs = md.getColumns(null, "ETL", "t_topic_qy_yj18btrajectorydata", "%")) {
                while (rs.next()) {
                    System.out.println("  " + rs.getString("COLUMN_NAME") + " " + rs.getString("TYPE_NAME")
                            + "(" + rs.getInt("DATA_TYPE") + ") len=" + rs.getInt("COLUMN_SIZE"));
                }
            }
            System.out.println("== tables in ETL schema");
            try (ResultSet rs = md.getTables(null, "ETL", "%", new String[]{"TABLE"})) {
                int n = 0;
                while (rs.next()) { n++; if (n <= 20) System.out.println("  " + rs.getString("TABLE_NAME")); }
                System.out.println("  total: " + n);
            }
        }

        // ② MySQL 现有 msgpack 样本
        String my = "jdbc:mysql://127.0.0.1:3306/data_collect?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
        try (Connection c = DriverManager.getConnection(my, "root", "root");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT `topic`, msg_info FROM t123 LIMIT 5")) {
            while (rs.next()) {
                byte[] raw = rs.getBytes("msg_info");
                System.out.println("\ntopic=" + rs.getString(1) + " msgLen=" + (raw == null ? -1 : raw.length));
                if (raw != null) {
                    MessageUnpacker u = MessagePack.newDefaultUnpacker(raw);
                    Value v = u.unpackValue();
                    System.out.println("  unpacked: " + v);
                }
            }
        }
    }
}
