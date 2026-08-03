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
import java.util.HashMap;
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

    /** 需要显示的数据库表配置：表名 → 显示名称（Tab 标签） */
    private static final Map<String, String> TABLE_CONFIG = new LinkedHashMap<>();
    static {
        TABLE_CONFIG.put("TABLE_1", "表格1");
        TABLE_CONFIG.put("TABLE_2", "表格2");
        TABLE_CONFIG.put("TABLE_3", "表格3");
        TABLE_CONFIG.put("TABLE_4", "表格4");
    }

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
            buildEquip(1L, "99式主战坦克", "主战坦克", ov("model", "99A", "weapon", "125mm滑膛炮", "ammo", "82%")),
            buildEquip(2L, "歼-20", "隐身战斗机", ov("model", "歼-20A", "speed", "2.2 马赫", "status", "巡逻中", "tag", "warning", "ammo", "90%")),
            buildEquip(3L, "空警-2000", "预警机", ov("model", "空警-2000", "speed", "850 km/h")),
            buildEquip(4L, "红旗-9", "防空导弹车", ov("model", "HQ-9", "status", "待命", "tag", "warning")),
            buildEquip(5L, "东风-17", "弹道导弹车", ov("model", "DF-17", "weapon", "DF-17高超音速导弹")),
            buildEquip(6L, "武直-10", "武装直升机", ov("model", "直-10", "speed", "270 km/h", "status", "巡逻中", "tag", "warning")),
            buildEquip(7L, "山猫侦察车", "侦察车", ov("model", "山猫", "speed", "80 km/h", "status", "侦察中", "tag", "warning")),
            buildEquip(8L, "指挥方舱", "指挥车", ov("model", "指挥方舱", "speed", "40 km/h", "status", "待命", "tag", "warning"))
        );
    }

    private List<EquipmentVO> buildBlueEquips() {
        return Arrays.asList(
            buildEquip(11L, "M1A2主战坦克", "主战坦克", ov("model", "M1A2 SEPv3", "weapon", "120mm滑膛炮", "ammo", "78%")),
            buildEquip(12L, "F-35战机", "隐身战斗机", ov("model", "F-35B", "speed", "1.6 马赫", "status", "巡逻中", "tag", "warning", "ammo", "88%")),
            buildEquip(13L, "宙斯盾驱逐舰", "驱逐舰", ov("model", "DDG-51", "speed", "30 节", "status", "巡航中", "tag", "warning")),
            buildEquip(14L, "全球鹰无人机", "无人机", ov("model", "RQ-4", "speed", "575 km/h", "status", "侦察中", "tag", "warning")),
            buildEquip(15L, "爱国者导弹", "防空导弹车", ov("model", "MIM-104", "status", "待命", "tag", "warning")),
            buildEquip(16L, "CH-53直升机", "运输直升机", ov("model", "CH-53K", "speed", "315 km/h")),
            buildEquip(17L, "斯特赖克装甲车", "装甲运兵车", ov("model", "LAV III", "speed", "97 km/h")),
            buildEquip(18L, "两栖登陆舰", "登陆舰", ov("model", "LHD-1", "speed", "22 节", "status", "待命", "tag", "warning"))
        );
    }

    /** 构造覆盖字段 Map（key: model/speed/status/tag/weapon/ammo/pos/course） */
    private static Map<String, String> ov(String... kv) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i], kv[i + 1]);
        }
        return map;
    }

    /** 构造一条 label/value 键值项（可含可选 tag） */
    private static Map<String, Object> item(String... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i], kv[i + 1]);
        }
        return map;
    }

    /** 构造一个信息分类（含 key/label/items，label 为前端 Tab 显示文字） */
    private static InfoCategoryVO category(String key, String label, List<Map<String, Object>> items) {
        InfoCategoryVO vo = new InfoCategoryVO();
        vo.setKey(key);
        vo.setLabel(label);
        vo.setItems(items);
        return vo;
    }

    private EquipmentVO buildEquip(Long id, String name, String type, Map<String, String> o) {
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
     * 每个表作为一个分类，表头是字段名，数据是行数据。
     */
    private List<InfoCategoryVO> buildTableCategories() {
        List<InfoCategoryVO> categories = new ArrayList<>();
        for (Map.Entry<String, String> entry : TABLE_CONFIG.entrySet()) {
            String tableName = entry.getKey();
            String displayName = entry.getValue();
            try {
                List<Map<String, Object>> rows = queryTableData(tableName);
                categories.add(category(tableName, displayName, rows));
            } catch (Exception e) {
                // 查询失败时添加空分类
                categories.add(category(tableName, displayName, new ArrayList<>()));
            }
        }
        return categories;
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
