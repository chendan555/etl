import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.*;
public class GetFlow209b {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT FLOW_JSON FROM T_TRANSFORMATION WHERE ID=209")) {
            rs.next();
            JSONObject flow = JSON.parseObject(rs.getString(1));
            JSONArray nodes = flow.getJSONArray("nodes");
            System.out.println("总节点数=" + nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject n = nodes.getJSONObject(i);
                JSONObject data = n.getJSONObject("data");
                System.out.println("\n节点" + i + " 顶层keys=" + n.keySet());
                System.out.println("  data keys=" + (data != null ? data.keySet() : "null"));
                Object props = n.get("properties");
                System.out.println("  顶层properties=" + (props instanceof JSONObject
                        ? "ruleForm类型=" + ((JSONObject) props).get("ruleForm").getClass().getSimpleName() : props));
                System.out.println("  points=" + JSON.toJSONString(n.get("points")));
                if (data != null) {
                    System.out.println("  data.pluginId=" + data.getString("pluginId") + " data.properties="
                            + (data.get("properties") != null ? data.get("properties").getClass().getSimpleName() : "null"));
                }
                if (i == 0) {
                    Object ruleForm = ((JSONObject) props).get("ruleForm");
                    System.out.println("  节点0 ruleForm内容=" + ruleForm);
                }
            }
        }
    }
}
