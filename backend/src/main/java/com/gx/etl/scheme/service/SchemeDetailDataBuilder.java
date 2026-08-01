package com.gx.etl.scheme.service;

import com.gx.etl.scheme.vo.EquipmentInfoVO;
import com.gx.etl.scheme.vo.EquipmentVO;
import com.gx.etl.scheme.vo.InfoCategoryVO;
import com.gx.etl.scheme.vo.SideVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案详细数据（红蓝方/装备/信息分类）构建器。
 * 当前为固定数据，接入真实数据源后替换 buildFixedSides() 的实现即可。
 */
@Component
public class SchemeDetailDataBuilder {

    private static final String KEY_BASIC = "basic";
    private static final String KEY_STATUS = "status";
    private static final String KEY_COMBAT = "combat";
    private static final String KEY_RECORDS = "records";

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

    private static EquipmentVO buildEquip(Long id, String name, String type, Map<String, String> o) {
        EquipmentVO vo = new EquipmentVO();
        vo.setId(id);
        vo.setName(name);
        vo.setType(type);

        String status = o.getOrDefault("status", "作战中");
        String tag = o.getOrDefault("tag", "danger");
        String speed = o.getOrDefault("speed", "60 km/h");
        String ammo = o.getOrDefault("ammo", "85%");

        EquipmentInfoVO info = new EquipmentInfoVO();
        info.setCategories(Arrays.asList(
            category(KEY_BASIC, "基本信息", Arrays.asList(
                item("label", "型号", "value", o.getOrDefault("model", name)),
                item("label", "类型", "value", type),
                item("label", "状态", "value", status, "tag", tag),
                item("label", "位置", "value", o.getOrDefault("pos", "东经120° 北纬35°")),
                item("label", "速度", "value", speed),
                item("label", "航向", "value", o.getOrDefault("course", "东北 45°")),
                item("label", "武器配置", "value", o.getOrDefault("weapon", "制式装备")),
                item("label", "油弹量", "value", ammo)
            )),
            category(KEY_STATUS, "实时状态", Arrays.asList(
                item("label", "当前状态", "value", status, "tag", tag),
                item("label", "航速", "value", speed),
                item("label", "油量", "value", ammo),
                item("label", "电子系统", "value", "正常", "tag", "success")
            )),
            category(KEY_COMBAT, "作战指标", Arrays.asList(
                item("label", "命中率", "value", "92%"),
                item("label", "侦测范围", "value", "80 km"),
                item("label", "载弹量", "value", "42 枚"),
                item("label", "持续作战时间", "value", "6 h")
            )),
            category(KEY_RECORDS, "数据记录", Arrays.asList(
                item("time", "2026-08-01 10:30:00", "item", "航速", "value", "58 km/h"),
                item("time", "2026-08-01 10:20:00", "item", "油量", "value", "86%"),
                item("time", "2026-08-01 10:10:00", "item", "航向", "value", "45°"),
                item("time", "2026-08-01 10:00:00", "item", "海拔", "value", "320 m"),
                item("time", "2026-08-01 09:50:00", "item", "航速", "value", "55 km/h"),
                item("time", "2026-08-01 09:40:00", "item", "油量", "value", "88%")
            ))
        ));
        vo.setInfo(info);
        return vo;
    }
}
