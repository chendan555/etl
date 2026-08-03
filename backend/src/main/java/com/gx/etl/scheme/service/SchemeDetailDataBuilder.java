package com.gx.etl.scheme.service;

import com.gx.etl.scheme.vo.EquipmentInfoVO;
import com.gx.etl.scheme.vo.EquipmentVO;
import com.gx.etl.scheme.vo.InfoCategoryVO;
import com.gx.etl.scheme.vo.SideVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案详细数据（红蓝方/装备/信息分类）构建器。
 * 装备数据为固定数据，信息分类从数据库表动态查询。
 */
@Component
public class SchemeDetailDataBuilder {

    @Resource
    private DataSource dataSource;

    /** 需要显示的数据库表名列表 */
    private static final List<String> TABLE_NAMES = Arrays.asList(
        "TABLE_1", "TABLE_2", "TABLE_3", "TABLE_4"
    );

    public List<SideVO> buildFixedSides() {
        List<SideVO> sides = new ArrayList<>();
        sides.add(buildSide("red", "红方", "#E54D42", buildRedEquips()));
        sides.add(buildSide("blue", "蓝方", "#2F7DE1", buildBlueEquips()));
        return sides;
    }

    private SideVO buildSide(String side, String name, String color, List<EquipmentVO> equips) {
        SideVO vo = new SideVO();
        vo.setSide(side);
        vo.setName(name);
        vo.setColor(color);
        vo.setEquipmentList(equips);
        return vo;
    }

    private List<EquipmentVO> buildRedEquips() {
        return Arrays.asList(
            buildEquip(1L, "99式主战坦克", "主战坦克"),
            buildEquip(2L, "歼-20", "隐身战斗机"),
            buildEquip(3L, "空警-2000", "预警机"),
            buildEquip(4L, "红旗-9", "防空导弹车"),
            buildEquip(5L, "东风-17", "弹道导弹车"),
            buildEquip(6L, "武直-10", "武装直升机"),
            buildEquip(7L, "山猫侦察车", "侦察车"),
            buildEquip(8L, "指挥方舱", "指挥车")
        );
    }

    private List<EquipmentVO> buildBlueEquips() {
        return Arrays.asList(
            buildEquip(11L, "M1A2主战坦克", "主战坦克"),
            buildEquip(12L, "F-35战机", "隐身战斗机"),
            buildEquip(13L, "宙斯盾驱逐舰", "驱逐舰"),
            buildEquip(14L, "全球鹰无人机", "无人机"),
            buildEquip(15L, "爱国者导弹", "防空导弹车"),
            buildEquip(16L, "CH-53直升机", "运输直升机"),
            buildEquip(17L, "斯特赖克装甲车", "装甲运兵车"),
            buildEquip(18L, "两栖登陆舰", "登陆舰")
        );
    }

    /** 构造一个信息分类（含 key/label/items，label 为前端 Tab 显示文字） */
    private static InfoCategoryVO category(String key, String label, List<Map<String, Object>> items) {
        InfoCategoryVO vo = new InfoCategoryVO();
        vo.setKey(key);
        vo.setLabel(label);
        vo.setItems(items);
        return vo;
    }

    private EquipmentVO buildEquip(Long id, String name, String type) {
        EquipmentVO vo = new EquipmentVO();
        vo.setId(id);
        vo.setName(name);
        vo.setType(type);

        EquipmentInfoVO info = new EquipmentInfoVO();
        info.setCategories(buildTableCategories());
        vo.setInfo(info);
        return vo;
    }

    /**
     * 动态查询配置的数据库表，构建信息分类列表。
     * 每个表作为一个分类，表头是字段名，数据是行数据，Tab 标签为表注释。
     */
    private List<InfoCategoryVO> buildTableCategories() {
        List<InfoCategoryVO> categories = new ArrayList<>();
        for (String tableName : TABLE_NAMES) {
            try {
                String comment = queryTableComment(tableName);
                List<Map<String, Object>> rows = queryTableData(tableName);
                categories.add(category(tableName, comment, rows));
            } catch (Exception e) {
                // 查询失败时使用表名作为显示名
                categories.add(category(tableName, tableName, new ArrayList<>()));
            }
        }
        return categories;
    }

    /**
     * 查询表注释（达梦数据库）
     */
    private String queryTableComment(String tableName) {
        String sql = "SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE TABLE_NAME = '" + tableName + "'";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String comment = rs.getString("COMMENTS");
                if (comment != null && !comment.isEmpty()) {
                    return comment;
                }
            }
        } catch (Exception e) {
            // 查询失败返回表名
        }
        return tableName;
    }

    /**
     * 查询指定表的所有数据，返回行列表（每行是一个 Map，key 为字段名）
     */
    private List<Map<String, Object>> queryTableData(String tableName) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnLabel(i);
                    row.put(columnName, rs.getObject(i));
                }
                rows.add(row);
            }
        } catch (Exception e) {
            // 查询失败返回空列表
        }
        return rows;
    }
}
