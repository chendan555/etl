import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 引接性能测试数据生成器
 *
 * 达梦(CASIC_ASSESS):
 *   - 测试主题 TOPIC_INGEST_PERF_TEST + 13 个字段(仿弹道数据,全 simpleType)
 *   - ETL."t_topic_ingest_perf_test" 目标表(全 VARCHAR(500),仿现有目标表)
 *   - 采集配置 + 测试方案「性能测试-引接」(2 个样本明细 × 各 5 个实例,STATUS=0)
 * MySQL(data_collect):
 *   - 10 个实例表 t990000001..t990000010,共 660 万行 msgpack
 *     (实例1=100万, 实例2=80万, 实例3~10=各60万),topic 均为 TOPIC_INGEST_PERF_TEST
 *
 * 可重复执行:先清理旧测试对象再重建。
 */
public class Gen {
    static final String DM_URL = "jdbc:dm://127.0.0.1:5236?compatibleMode=mysql";
    static final String MY_URL = "jdbc:mysql://127.0.0.1:3306/data_collect?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";

    static final String TOPIC_CODE = "TOPIC_INGEST_PERF_TEST";
    static final String TOPIC_NAME = "引接性能测试";
    static final String TABLE_NAME = "t_topic_ingest_perf_test";
    static final String SCHEME_NAME = "性能测试-660万行";
    static final String CJ_NAME = "引接性能测试采集";

    /** 字段定义:名称/类型(与后端 T_TOPIC_FIELD 对应,arrayIndex=顺序) */
    static final String[][] FIELDS = {
            {"simTime", "long"}, {"missileUID", "long"}, {"longitude", "double"},
            {"latitude", "int"}, {"altitude", "long"}, {"vx", "string"}, {"vy", "string"},
            {"vz", "long"}, {"game", "string"}, {"theta", "long"}, {"psi", "long"},
            {"phase", "long"}, {"velocity", "string"}};

    /** 实例ID → 行数:1个100万、1个80万、8个60万 = 660万 */
    static final long[][] INSTANCES = {
            {990000001, 1_000_000}, {990000002, 800_000},
            {990000003, 600_000}, {990000004, 600_000}, {990000005, 600_000}, {990000006, 600_000},
            {990000007, 600_000}, {990000008, 600_000}, {990000009, 600_000}, {990000010, 600_000}};

    static final int INST_BASE = 990000001;
    static final int INST_COUNT = 10;

    public static void main(String[] args) throws Exception {
        Class.forName("dm.jdbc.driver.DmDriver");
        Class.forName("com.mysql.cj.jdbc.Driver");
        long t0 = System.currentTimeMillis();
        setupDm();
        System.out.println("[DM] 测试主题/方案就绪, 耗时" + (System.currentTimeMillis() - t0) + "ms");
        long t1 = System.currentTimeMillis();
        genMysql();
        System.out.println("[MySQL] 660万行生成完成, 耗时" + (System.currentTimeMillis() - t1) / 1000 + "s");
        verify();
    }

    // ================= 达梦侧:主题 + 目标表 + 采集配置 + 方案 =================

    static void setupDm() throws Exception {
        try (Connection c = DriverManager.getConnection(DM_URL, "CASIC_ASSESS", "123456789")) {
            cleanDm(c);

            // 目标表(仿现有目标表:全 VARCHAR(500),列名保持与字段名一致)。
            // 注意：数据表在 CASIC_ASSESS schema(登录用户默认 schema，与后端 scheme.ingest.dm-schema 一致)，不写死 ETL 前缀
            StringBuilder ddl = new StringBuilder("CREATE TABLE \"" + TABLE_NAME + "\" (");
            ddl.append("\"SCHEME_ID\" VARCHAR(500)");
            for (String[] f : FIELDS) ddl.append(", \"").append(f[0]).append("\" VARCHAR(500)");
            ddl.append(")");
            try (Statement st = c.createStatement()) {
                st.execute(ddl.toString());
            }
            System.out.println("[DM] 目标表 " + TABLE_NAME + " 已创建(当前schema)");

            // 测试主题
            long topicId;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_TOPIC_CONFIG(TOPIC_NAME, TOPIC_CODE, TABLE_NAME, CREATE_TIME, UPDATE_TIME) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, TOPIC_NAME);
                ps.setString(2, TOPIC_CODE);
                ps.setString(3, TABLE_NAME);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    topicId = rs.getLong(1);
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_TOPIC_FIELD(TOPIC_ID, FIELD_NAME, FIELD_TYPE, COMPLEX_TYPE, ARRAY_INDEX, SUB_INDEX, SORT_ORDER) VALUES(?,?,?,?,?,0,?)")) {
                for (int i = 0; i < FIELDS.length; i++) {
                    ps.setLong(1, topicId);
                    ps.setString(2, FIELDS[i][0]);
                    ps.setString(3, FIELDS[i][1]);
                    ps.setString(4, "N");
                    ps.setInt(5, i);
                    ps.setInt(6, i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            System.out.println("[DM] 测试主题 id=" + topicId + " (" + FIELDS.length + "个字段)");

            // 采集配置
            long cjId;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_CJ_CONFIG(YB_NAME, YB_LIST, CREATE_TIME, UPDATE_TIME) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, CJ_NAME);
                ps.setString(2, String.valueOf(topicId));
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    cjId = rs.getLong(1);
                }
            }

            // 测试方案 + 2个样本明细(各5个实例)
            long schemeId;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_SCHEME(SCHEME_NAME, SCHEME_DESC, CJ_LIST_ID, STATUS, CREATE_TIME, UPDATE_TIME) VALUES(?,?,?,0,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, SCHEME_NAME);
                ps.setString(2, "引接性能测试:10实例/660万行,实例ID 990000001~990000010");
                ps.setLong(3, cjId);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    schemeId = rs.getLong(1);
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_SCHEME_DETAIL(SCHEME_ID, YB_ID, YB_NAME, YB_LIST, STATUS, CREATE_TIME, UPDATE_TIME) VALUES(?,?,?,?,0,?,?)")) {
                for (int d = 0; d < 2; d++) {
                    StringBuilder ybList = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        if (i > 0) ybList.append(",");
                        ybList.append(INST_BASE + (long) d * 5 + i);
                    }
                    ps.setLong(1, schemeId);
                    ps.setLong(2, INST_BASE + (long) d * 5);
                    ps.setString(3, "性能测试样本" + (d + 1));
                    ps.setString(4, ybList.toString());
                    ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    ps.executeUpdate();
                    System.out.println("[DM] 明细: 性能测试样本" + (d + 1) + " ybList=" + ybList);
                }
            }
        }
    }

    /** 清理旧的测试对象(可重复执行)。按结构关联清理(主题代码/数字ID)，不依赖中文名称，避免编码问题漏删 */
    static void cleanDm(Connection c) throws Exception {
        List<Long> topicIds = new ArrayList<>();
        List<Long> cjIds = new ArrayList<>();
        List<Long> schemeIds = new ArrayList<>();
        try (Statement st = c.createStatement()) {
            // 1. 主题(TOPIC_CODE 是ASCII，不受编码影响)及字段
            ResultSet rs = st.executeQuery("SELECT ID FROM T_TOPIC_CONFIG WHERE TOPIC_CODE='" + TOPIC_CODE + "'");
            while (rs.next()) topicIds.add(rs.getLong(1));
            for (Long id : topicIds) {
                st.executeUpdate("DELETE FROM T_TOPIC_FIELD WHERE TOPIC_ID=" + id);
                st.executeUpdate("DELETE FROM T_TOPIC_CONFIG WHERE ID=" + id);
            }
            // 2. 采集配置：YB_LIST = 主题ID(纯数字串)，再按名称兜底
            for (Long topicId : topicIds) {
                rs = st.executeQuery("SELECT ID FROM T_CJ_CONFIG WHERE YB_LIST='" + topicId + "'");
                while (rs.next()) cjIds.add(rs.getLong(1));
            }
            rs = st.executeQuery("SELECT ID FROM T_CJ_CONFIG WHERE YB_NAME=N'" + CJ_NAME + "'");
            while (rs.next()) cjIds.add(rs.getLong(1));
            // 3. 方案与明细：按 CJ_LIST_ID 结构关联
            if (!cjIds.isEmpty()) {
                rs = st.executeQuery("SELECT ID FROM T_SCHEME WHERE CJ_LIST_ID IN (" + ids(cjIds) + ")");
                while (rs.next()) schemeIds.add(rs.getLong(1));
            }
            rs = st.executeQuery("SELECT ID FROM T_SCHEME WHERE SCHEME_NAME=N'" + SCHEME_NAME + "'");
            while (rs.next()) schemeIds.add(rs.getLong(1));
            if (!schemeIds.isEmpty()) {
                st.executeUpdate("DELETE FROM T_SCHEME_DETAIL WHERE SCHEME_ID IN (" + ids(schemeIds) + ")");
                st.executeUpdate("DELETE FROM T_SCHEME WHERE ID IN (" + ids(schemeIds) + ")");
            }
            for (Long cjId : cjIds) st.executeUpdate("DELETE FROM T_CJ_CONFIG WHERE ID=" + cjId);
            // 4. 目标表(当前schema) + 历史遗留的 ETL schema 下的测试表
            try { st.execute("DROP TABLE \"" + TABLE_NAME + "\""); } catch (Exception ignore) { }
            try { st.execute("DROP TABLE ETL.\"" + TABLE_NAME + "\""); } catch (Exception ignore) { }
        }
        System.out.println("[DM] 旧测试对象已清理");
    }

    static String ids(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        for (Long id : list) {
            if (sb.length() > 0) sb.append(",");
            sb.append(id);
        }
        return sb.toString();
    }

    // ================= MySQL 侧:实例表 + msgpack 数据 =================

    /** 预生成字符串池,避免逐行格式化 */
    static String[] pool(String prefix) {
        String[] p = new String[64];
        for (int i = 0; i < 64; i++) p[i] = prefix + (i / 10.0);
        return p;
    }

    static void genMysql() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Future<?>> fs = new ArrayList<>();
        for (long[] inst : INSTANCES) {
            fs.add(pool.submit(() -> {
                try {
                    genOneTable(inst[0], (int) inst[1]);
                } catch (Exception e) {
                    throw new RuntimeException("生成 t" + inst[0] + " 失败", e);
                }
            }));
        }
        for (Future<?> f : fs) f.get();
        pool.shutdown();
    }

    static void genOneTable(long instanceId, int rows) throws Exception {
        long t0 = System.currentTimeMillis();
        String table = "t" + instanceId;
        try (Connection c = DriverManager.getConnection(MY_URL, "root", "root")) {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.execute("DROP TABLE IF EXISTS `" + table + "`");
                st.execute("CREATE TABLE `" + table + "` (" +
                        "`id` int(11) NOT NULL, `msg_info` blob, `topic` varchar(255) COLLATE utf8_bin DEFAULT NULL, " +
                        "PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin");
            }
            String[] vx = pool("1."), vy = pool("2."), game = pool("g"), velocity = pool("3.");
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO `" + table + "`(id, msg_info, topic) VALUES(?,?,?)")) {
                int pending = 0, committed = 0;
                for (int i = 1; i <= rows; i++) {
                    MessageBufferPacker pk = MessagePack.newDefaultBufferPacker();
                    pk.packArrayHeader(FIELDS.length);
                    pk.packLong(1_000_000_000L + i);            // simTime
                    pk.packLong(instanceId);                     // missileUID
                    pk.packDouble(116.0 + (i % 10000) / 10000.0);// longitude
                    pk.packInt(39 + (i % 90));                   // latitude
                    pk.packLong(8000 + (i % 5000));              // altitude
                    pk.packString(vx[i % vx.length]);            // vx
                    pk.packString(vy[i % vy.length]);            // vy
                    pk.packLong(i % 360);                        // vz
                    pk.packString(game[i % game.length]);        // game
                    pk.packLong(i % 90);                         // theta
                    pk.packLong(i % 180);                        // psi
                    pk.packLong(i % 4);                          // phase
                    pk.packString(velocity[i % velocity.length]);// velocity
                    ps.setInt(1, i);
                    ps.setBytes(2, pk.toByteArray());
                    pk.close();
                    ps.setString(3, TOPIC_CODE);
                    ps.addBatch();
                    pending++;
                    if (pending >= 5000) {
                        ps.executeBatch();
                        pending = 0;
                        committed += 5000;
                        if (committed % 50000 == 0) {
                            c.commit();
                            System.out.println("  " + table + " " + committed + "/" + rows);
                        }
                    }
                }
                if (pending > 0) ps.executeBatch();
                c.commit();
            }
        }
        System.out.println(table + " 完成: " + rows + " 行, 耗时" + (System.currentTimeMillis() - t0) / 1000 + "s");
    }

    // ================= 验证 =================

    static void verify() throws Exception {
        System.out.println("\n===== 验证 =====");
        long total = 0;
        try (Connection c = DriverManager.getConnection(MY_URL, "root", "root");
             Statement st = c.createStatement()) {
            for (long[] inst : INSTANCES) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `t" + inst[0] + "`")) {
                    rs.next();
                    total += rs.getLong(1);
                }
            }
            System.out.println("[MySQL] 实例表总行数 = " + total);
            // 解一条样例验证格式
            try (ResultSet rs = st.executeQuery("SELECT msg_info FROM `t" + INSTANCES[0][0] + "` WHERE id=1")) {
                rs.next();
                MessageUnpacker u = MessagePack.newDefaultUnpacker(rs.getBytes(1));
                System.out.println("[MySQL] 样例解包(t" + INSTANCES[0][0] + " id=1): " + u.unpackValue());
            }
        }
        try (Connection c = DriverManager.getConnection(DM_URL, "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM \"" + TABLE_NAME + "\"");
            rs.next();
            System.out.println("[DM] 目标表当前行数 = " + rs.getLong(1) + " (引接前应为0)");
            rs = st.executeQuery("SELECT s.ID, s.SCHEME_NAME, s.STATUS, d.YB_NAME, d.YB_LIST, d.STATUS " +
                    "FROM T_SCHEME s JOIN T_SCHEME_DETAIL d ON d.SCHEME_ID = s.ID " +
                    "WHERE s.SCHEME_NAME='" + SCHEME_NAME + "'");
            while (rs.next()) {
                System.out.println("[DM] 方案 id=" + rs.getLong(1) + " " + rs.getString(2) + " status=" + rs.getInt(3)
                        + " | " + rs.getString(4) + " ybList=" + rs.getString(5) + " status=" + rs.getInt(6));
            }
        }
    }
}
