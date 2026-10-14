import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.*;
public class FindOutTpl {
    public static void main(String[] a) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:dm://127.0.0.1:5236?compatibleMode=mysql", "CASIC_ASSESS", "123456789");
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT ID, FLOW_JSON FROM T_TRANSFORMATION WHERE FLOW_JSON LIKE '%TableOutput%' AND FLOW_JSON LIKE '%dom%' ORDER BY LENGTH(FLOW_JSON)")) {
            while (rs.next()) {
                long id = rs.getLong(1);
                JSONObject flow = JSON.parseObject(rs.getString(2));
                JSONArray nodes = flow.getJSONArray("nodes");
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject n = nodes.getJSONObject(i);
                    JSONObject data = n.getJSONObject("data");
                    if (data != null && "TableOutput".equals(data.getString("pluginId"))) {
                        System.out.println("FOUND template in trans id=" + id + " node keys=" + n.keySet());
                        System.out.println("data keys=" + data.keySet());
                        // 打印该节点去掉 dom 的完整结构
                        JSONObject copy = new JSONObject(n);
                        String dom = data.getString("dom");
                        data.put("dom", dom != null ? "(dom len=" + dom.length() + ")" : null);
                        System.out.println("node=" + JSON.toJSONString(copy));
                        return;
                    }
                }
            }
            System.out.println("NO TableOutput-with-dom template found");
        }
    }
}
