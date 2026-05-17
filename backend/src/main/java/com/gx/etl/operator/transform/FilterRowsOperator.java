package com.gx.etl.operator.transform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 过滤记录算子 - 支持树形条件结构
 * 参考 Kettle FilterRows 实现
 *
 * 条件JSON格式:
 * {
 *   "negate": "true/false",
 *   "left_valuename": "字段名",
 *   "func": "0-8",        // 字符串函数
 *   "compareType": "字符串比较类型(1=等于,2=不等于,3=大于...)",
 *   "right_valuename": "字段名",
 *   "value": "常量值",
 *   "operator": "0/1/2",  // 逻辑组合: 0=无,1=AND,2=OR
 *   "children": [...]
 * }
 */
@Component
public class FilterRowsOperator extends AbstractOperator {

    @Override
    public String getId() { return "FilterRows"; }

    @Override
    public String getName() { return "过滤记录"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("condition", "条件(JSON)", OperatorSchema.PropertyType.TEXTAREA, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String conditionStr = ctx.getString("condition");
        if (conditionStr == null || conditionStr.isEmpty()) return input;

        // 解析条件树
        List<Map<String, Object>> conditions;
        try {
            if (conditionStr.trim().startsWith("[")) {
                conditions = JSONArray.parseObject(conditionStr, List.class);
            } else {
                conditions = new ArrayList<>();
                conditions.add(JSONObject.parseObject(conditionStr, Map.class));
            }
        } catch (Exception e) {
            return input;
        }

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            if (evaluateConditions(conditions, row)) {
                result.add(row);
            }
        }
        return DataSet.of(input.getColumnNames(), result);
    }

    /**
     * 评估条件列表（AND/OR 组合）
     */
    private boolean evaluateConditions(List<Map<String, Object>> conditions, DataRow row) {
        boolean result = true;
        int logicOp = 0; // 0=NONE, 1=AND, 2=OR

        for (Map<String, Object> cond : conditions) {
            boolean match = evaluateSingleCondition(cond, row);

            int op = getInt(cond, "operator", 0);
            if (op == 0) {
                // 第一个条件
                result = match;
            } else if (op == 1) {
                // AND
                result = result && match;
            } else if (op == 2) {
                // OR
                result = result || match;
            }
            logicOp = op;
        }
        return result;
    }

    /**
     * 评估单个条件（可能是叶子节点或有children的非叶子节点）
     */
    @SuppressWarnings("unchecked")
    private boolean evaluateSingleCondition(Map<String, Object> cond, DataRow row) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) cond.get("children");
        if (children != null && !children.isEmpty()) {
            // 非叶子节点：递归评估子条件
            boolean childResult = evaluateConditions(children, row);
            boolean negate = "true".equals(String.valueOf(cond.getOrDefault("negate", "false")));
            return negate ? !childResult : childResult;
        }

        // 叶子节点：执行实际比较
        String leftField = getString(cond, "left_valuename", "");
        if (leftField.isEmpty()) return true;

        Object leftValue = row.get(leftField);
        int func = getInt(cond, "func", 0);
        String leftStr = applyFunction(leftValue, func);

        // 获取右侧值
        String rightField = getString(cond, "right_valuename", "");
        String constValue = getString(cond, "value", "");
        String rightStr;
        if (rightField != null && !rightField.isEmpty()) {
            Object rv = row.get(rightField);
            rightStr = rv != null ? rv.toString() : null;
        } else {
            rightStr = constValue;
        }

        // 比较类型
        int compareType = getInt(cond, "compareType", 1);
        boolean match = compare(leftStr, rightStr, compareType);

        boolean negate = "true".equals(String.valueOf(cond.getOrDefault("negate", "false")));
        return negate ? !match : match;
    }

    /**
     * 对左值应用字符串函数
     * 0=NONE, 1=TRIM, 2=LTRIM, 3=RTRIM, 4=UPPER, 5=LOWER, 6=LENGTH, 7=REPLACE, 8=SUBSTRING
     */
    private String applyFunction(Object value, int func) {
        if (value == null) return null;
        String s = value.toString();
        switch (func) {
            case 1: return s.trim();
            case 2: return s.replaceAll("^\\s+", "");
            case 3: return s.replaceAll("\\s+$", "");
            case 4: return s.toUpperCase();
            case 5: return s.toLowerCase();
            case 6: return String.valueOf(s.length());
            case 7: return s; // REPLACE 需要额外参数，暂不支持
            case 8: return s; // SUBSTRING 需要额外参数，暂不支持
            default: return s;
        }
    }

    /**
     * 比较操作
     * 1=等于, 2=不等于, 3=大于, 4=小于, 5=大于等于, 6=小于等于,
     * 7=REGEXP, 8=IS NULL, 9=IS NOT NULL, 10=CONTAINS, 11=NOT CONTAINS,
     * 12=START WITH, 13=END WITH, 14=LIKE
     */
    private boolean compare(String left, String right, int type) {
        // NULL 处理
        if (type == 8) return left == null || left.equals("null");
        if (type == 9) return left != null && !left.equals("null");
        if (left == null) return false;
        if (right == null && type != 8 && type != 9) return false;

        switch (type) {
            case 1: return left.equals(right);
            case 2: return !left.equals(right);
            case 3: return compareNumeric(left, right) > 0;
            case 4: return compareNumeric(left, right) < 0;
            case 5: return compareNumeric(left, right) >= 0;
            case 6: return compareNumeric(left, right) <= 0;
            case 7: try { return Pattern.matches(right, left); } catch (Exception e) { return false; }
            case 10: return left.contains(right);
            case 11: return !left.contains(right);
            case 12: return left.startsWith(right);
            case 13: return left.endsWith(right);
            case 14: return likeMatch(left, right);
            default: return left.equals(right);
        }
    }

    private int compareNumeric(String a, String b) {
        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);
            return Double.compare(da, db);
        } catch (Exception e) {
            return a.compareTo(b);
        }
    }

    private boolean likeMatch(String value, String pattern) {
        String regex = pattern.replace("%", ".*").replace("_", ".");
        return Pattern.matches(regex, value);
    }

    private String getString(Map<String, Object> map, String key, String def) {
        Object v = map.get(key);
        return v != null ? v.toString() : def;
    }

    private int getInt(Map<String, Object> map, String key, int def) {
        Object v = map.get(key);
        if (v == null) return def;
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return def; }
    }
}
