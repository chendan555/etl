package com.gx.etl.operator.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 条件过滤算子 - 对上游数据按多条件过滤
 * 支持表格形式配置多个过滤条件，条件之间为AND关系
 */
@Component
public class ConditionFilterOperator extends AbstractOperator {

    @Override
    public String getId() { return "ConditionFilter"; }

    @Override
    public String getName() { return "条件过滤"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String conditionsJson = ctx.getString("conditions");
        Boolean negate = ctx.getBoolean("negate");

        if (conditionsJson == null || conditionsJson.isEmpty()) {
            return input;
        }
        if (negate == null) negate = false;

        List<FilterCondition> conditions = parseConditions(conditionsJson);
        if (conditions.isEmpty()) {
            return input;
        }

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            boolean match = true;
            for (FilterCondition cond : conditions) {
                Object fieldValue = row.get(cond.field);
                if (!evaluate(fieldValue, cond.operator, cond.value)) {
                    match = false;
                    break;
                }
            }
            if (negate) match = !match;
            if (match) {
                result.add(row);
            }
        }
        return DataSet.of(input.getColumnNames(), result);
    }

    private List<FilterCondition> parseConditions(String json) {
        List<FilterCondition> list = new ArrayList<>();
        try {
            JSONArray arr = JSON.parseArray(json);
            if (arr == null) return list;
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String field = obj.getString("field");
                String operator = obj.getString("operator");
                String value = obj.getString("value");
                if (field != null && !field.isEmpty() && operator != null && !operator.isEmpty()) {
                    list.add(new FilterCondition(field, operator, value));
                }
            }
        } catch (Exception e) {
            // ignore parse error
        }
        return list;
    }

    private boolean evaluate(Object fieldValue, String operator, String value) {
        String left = fieldValue != null ? fieldValue.toString() : null;

        switch (operator) {
            case "is_null":
                return left == null || left.isEmpty();
            case "is_not_null":
                return left != null && !left.isEmpty();
            default:
                if (left == null) return false;
        }

        switch (operator) {
            case "eq": return left.equals(value);
            case "ne": return !left.equals(value);
            case "gt": return compareNumeric(left, value) > 0;
            case "lt": return compareNumeric(left, value) < 0;
            case "ge": return compareNumeric(left, value) >= 0;
            case "le": return compareNumeric(left, value) <= 0;
            case "contains": return left.contains(value != null ? value : "");
            case "not_contains": return !left.contains(value != null ? value : "");
            case "starts_with": return left.startsWith(value != null ? value : "");
            case "ends_with": return left.endsWith(value != null ? value : "");
            case "regex":
                try { return Pattern.matches(value, left); } catch (Exception e) { return false; }
            case "like": return likeMatch(left, value);
            default: return left.equals(value);
        }
    }

    private int compareNumeric(String a, String b) {
        try {
            return Double.compare(Double.parseDouble(a), Double.parseDouble(b));
        } catch (Exception e) {
            return a.compareTo(b);
        }
    }

    private boolean likeMatch(String value, String pattern) {
        if (pattern == null) return false;
        String regex = pattern.replace("%", ".*").replace("_", ".");
        try {
            return Pattern.matches(regex, value);
        } catch (Exception e) {
            return false;
        }
    }

    private static class FilterCondition {
        String field;
        String operator;
        String value;

        FilterCondition(String field, String operator, String value) {
            this.field = field;
            this.operator = operator;
            this.value = value;
        }
    }
}
