import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.*;

/**
 * 构建流程性能测试:处理 t_topic_ingest_perf_test(引接进来的660万行测试数据)
 *
 * 节点结构以 209 号流程(实际批量执行成功过的)为模板克隆,保证页面与引擎双向兼容:
 *   - 节点必须带 data 包装(data.pluginId 才能被 DagParser 识别,直接抄 204 的扁平结构会 NPE)
 *   - 顶层 properties.ruleForm 用 JSONObject(fastjson getString 会转成字符串,引擎可解析)
 *   - data.dom 保留原表单定义,旧版 FlowChart 页面的配置弹窗才能正常打开
 *
 * 流程: 表输入(按${schemeId}参数读引接表) -> 目标表输出BoundTableOutput(写 t_perf_test_output)
 * 用法: 旧版FlowChart -> 打开「性能测试-引接表处理」-> 参数执行 -> 添加多行 {"schemeId":"990000003"} 等
 */
public class GenFlow {
    static final String DM_URL = "jdbc:dm://127.0.0.1:5236?compatibleMode=mysql";
    static final String OUT_TABLE = "t_perf_test_output";
    static final String FLOW_NAME = "性能测试-引接表处理";
    static final int TEMPLATE_FLOW_ID = 209; // 实际批量执行成功过的流程,克隆其节点

    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection(DM_URL, "CASIC_ASSESS", "123456789")) {
            clean(c);
            createOutTable(c);
            insertFlow(c);
            printGuide();
        }
    }

    static void clean(Connection c) throws Exception {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM T_TRANSFORMATION WHERE NAME='" + FLOW_NAME + "'");
            try { st.execute("DROP TABLE \"" + OUT_TABLE + "\""); } catch (Exception ignore) { }
        }
        System.out.println("[DM] 旧测试流程/输出表已清理");
    }

    static void createOutTable(Connection c) throws Exception {
        StringBuilder ddl = new StringBuilder("CREATE TABLE \"" + OUT_TABLE + "\" (\"SCHEME_ID\" VARCHAR(500)");
        for (String f : new String[]{"simTime", "missileUID", "longitude", "latitude", "altitude", "vx", "vy",
                "vz", "game", "theta", "psi", "phase", "velocity"}) {
            ddl.append(", \"").append(f).append("\" VARCHAR(500)");
        }
        ddl.append(")");
        try (Statement st = c.createStatement()) {
            st.execute(ddl.toString());
        }
        System.out.println("[DM] 输出表 " + OUT_TABLE + " 已创建");
    }

    /** 从模板流程深拷贝节点并替换 id/位置/配置 */
    static JSONObject cloneNode(JSONObject template, String newId, int left,
                                JSONObject ruleForm, String sourceEndpoint, String targetEndpoint) {
        JSONObject node = new JSONObject(true);
        node.put("data", JSON.parseObject(JSON.toJSONString(template.getJSONObject("data")))); // 深拷贝,含dom
        node.put("id", newId);
        node.put("position", new JSONObject(true) {{ put("top", 180); put("left", left); }});
        node.put("nodeId", newId);
        node.getJSONObject("data").put("nodeId", newId);
        node.put("properties", new JSONObject(true) {{ put("ruleForm", ruleForm); }});
        JSONObject points = new JSONObject(true);
        points.put("sources", new JSONArray() {{ if (sourceEndpoint != null) add(sourceEndpoint); }});
        points.put("targets", new JSONArray() {{ if (targetEndpoint != null) add(targetEndpoint); }});
        node.put("points", points);
        return node;
    }

    static void insertFlow(Connection c) throws Exception {
        // 取模板流程的节点
        String tplJson;
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT FLOW_JSON FROM T_TRANSFORMATION WHERE ID=" + TEMPLATE_FLOW_ID)) {
            if (!rs.next()) throw new RuntimeException("模板流程 " + TEMPLATE_FLOW_ID + " 不存在");
            tplJson = rs.getString(1);
        }
        JSONArray tplNodes = JSON.parseObject(tplJson).getJSONArray("nodes");
        JSONObject tplIn = tplNodes.getJSONObject(0);  // TableInput
        JSONObject tplOut = tplNodes.getJSONObject(1); // BoundTableOutput
        if (!"TableInput".equals(tplIn.getJSONObject("data").getString("pluginId"))
                || !"BoundTableOutput".equals(tplOut.getJSONObject("data").getString("pluginId"))) {
            throw new RuntimeException("模板流程节点类型不符,请检查 " + TEMPLATE_FLOW_ID);
        }

        // 表输入:按 ${schemeId} 参数读取引接表(DM连接账号是SYSDBA,SQL必须带CASIC_ASSESS前缀)
        JSONObject inRule = new JSONObject(true);
        inRule.put("stepName", "表输入-引接表");
        inRule.put("connection", "1");
        inRule.put("sql", "SELECT * FROM CASIC_ASSESS.t_topic_ingest_perf_test WHERE SCHEME_ID = '${schemeId}'");
        inRule.put("table", "t_topic_ingest_perf_test");
        JSONObject in = cloneNode(tplIn, "node-in-perf", 80, inRule, "sp-in-perf", null);

        // 目标表输出:BoundTableOutput 直接用系统主数据源(CASIC_ASSESS),表名不带前缀即可
        JSONObject outRule = new JSONObject(true);
        outRule.put("stepName", "表输出-结果表");
        outRule.put("boundTable", OUT_TABLE);
        outRule.put("commit", 5000);
        outRule.put("truncateTable", false);
        JSONObject out = cloneNode(tplOut, "node-out-perf", 480, outRule, null, "tp-out-perf");

        JSONObject flow = new JSONObject(true);
        flow.put("nodes", new JSONArray() {{ add(in); add(out); }});
        flow.put("endpoints", new JSONArray() {{
            add(new JSONObject(true) {{ put("data", new JSONObject(true) {{ put("value", "输出"); }}); put("id", "sp-in-perf"); }});
            add(new JSONObject(true) {{ put("data", new JSONObject(true) {{ put("value", "输入"); }}); put("id", "tp-out-perf"); }});
        }});
        flow.put("edges", new JSONArray() {{ add("sp-in-perf&&tp-out-perf"); }});
        flow.put("head", "");

        // 固定复用 211 这个ID,避免每次重建流程ID漂移导致页面里打开的旧入口失效
        int flowId;
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(ID)+1 FROM T_TRANSFORMATION")) {
            rs.next();
            flowId = rs.getInt(1);
        }
        try (Statement st = c.createStatement()) {
            st.execute("SET IDENTITY_INSERT \"CASIC_ASSESS\".\"T_TRANSFORMATION\" ON");
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO T_TRANSFORMATION(ID, NAME, CATEGORIES_ID, FLOW_JSON, STATUS, REMARK, CREATE_TIME, UPDATE_TIME) " +
                            "VALUES(?,?,0,?,0,?,SYSDATE,SYSDATE)")) {
                ps.setInt(1, flowId);
                ps.setString(2, FLOW_NAME);
                ps.setString(3, flow.toJSONString());
                ps.setString(4, "流程性能测试:表输入(按schemeId参数) -> 目标表输出,配合参数执行多行并行验证");
                ps.executeUpdate();
            } finally {
                st.execute("SET IDENTITY_INSERT \"CASIC_ASSESS\".\"T_TRANSFORMATION\" OFF");
            }
        }
        System.out.println("[DM] 测试流程已创建: id=" + flowId + " 名称=" + FLOW_NAME
                + " (结构克隆自流程" + TEMPLATE_FLOW_ID + ",含data.dom,页面/引擎双兼容)");
    }

    static void printGuide() {
        System.out.println();
        System.out.println("===== 使用说明 =====");
        System.out.println("1. 刷新流程列表,打开「" + FLOW_NAME + "」(旧入口/旧标签页作废,以列表里的为准)");
        System.out.println("2. 点「参数执行」,添加参数行(schemeId 列):");
        System.out.println("   先试 1 行: {\"schemeId\": \"990000003\"}   (60万行,验证单批)");
        System.out.println("   再加 4 行: 990000003~990000006 (验证4路并行)");
        System.out.println("3. 进度看「终端-批量进度」;后端日志应出现多批「第 X 批开始执行」交错");
        System.out.println("4. 结果核对: SELECT COUNT(*) FROM t_perf_test_output");
        System.out.println("注意: 单次「执行」不带参数会匹配0行;批量并行大表对JVM堆有要求,OOM时调大-Xmx");
    }
}
