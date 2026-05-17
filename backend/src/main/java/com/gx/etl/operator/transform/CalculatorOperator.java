package com.gx.etl.operator.transform;

import com.alibaba.fastjson.JSONArray;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 计算器算子 - 支持多种计算类型
 * 参考 Kettle CalculatorMetaFunction 实现
 *
 * calc_type:
 * 0=NONE, 1=CONSTANT, 2=ADD, 3=SUBTRACT, 4=MULTIPLY, 5=DIVIDE,
 * 6=SQUARE, 7=SQUARE_ROOT, 8=PERCENT_1(A*B/100), 9=PERCENT_2(A/B*100),
 * 10=PERCENT_3(A+A*B/100), 11=COMBINATION_1(A+B+C), 12=COMBINATION_2(A*B+C),
 * 13=ROUND_1(A,precision), 14=ROUND_2(A), 15=NVL, 16=DATE_DIFF,
 * 17=ADD_3(A+B+C), 18=YEAR, 19=MONTH, 20=DAY, 21=HOUR, 22=MINUTE, 23=SECOND,
 * 24=MODULUS, 25=ABS, 26=CEIL, 27=FLOOR
 */
@Component
public class CalculatorOperator extends AbstractOperator {

    @Override
    public String getId() { return "Calculator"; }

    @Override
    public String getName() { return "计算器"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");
        if (tableData == null || tableData.isEmpty()) return input;

        List<String> columns = new ArrayList<>(input.getColumnNames());
        List<DataRow> result = new ArrayList<>();

        // 预处理：收集计算定义
        List<CalcDef> calcDefs = new ArrayList<>();
        for (Map<String, Object> row : tableData) {
            String fieldName = getString(row, "field_name");
            if (fieldName == null || fieldName.isEmpty()) continue;
            CalcDef def = new CalcDef();
            def.fieldName = fieldName;
            def.calcType = getInt(row, "calc_type", 0);
            def.fieldA = getString(row, "field_a");
            def.fieldB = getString(row, "field_b");
            def.fieldC = getString(row, "field_c");
            def.valueType = getInt(row, "value_type", 0);
            def.valueLength = getInt(row, "value_length", -1);
            def.valuePrecision = getInt(row, "value_precision", -1);
            def.remove = "Y".equalsIgnoreCase(getString(row, "remove"));
            if (!columns.contains(fieldName)) columns.add(fieldName);
            calcDefs.add(def);
        }

        for (DataRow row : input) {
            DataRow newRow = row.copy();
            for (CalcDef def : calcDefs) {
                try {
                    Object result_val = calculate(def, row);
                    newRow.put(def.fieldName, result_val);
                } catch (Exception e) {
                    newRow.put(def.fieldName, null);
                }
            }
            result.add(newRow);
        }

        // 移除标记为 remove 的字段
        List<String> removeFields = new ArrayList<>();
        for (CalcDef def : calcDefs) {
            if (def.remove) removeFields.add(def.fieldName);
        }
        if (!removeFields.isEmpty()) {
            columns.removeAll(removeFields);
            for (DataRow row : result) {
                for (String f : removeFields) {
                    row.remove(f);
                }
            }
        }

        return DataSet.of(columns, result);
    }

    private Object calculate(CalcDef def, DataRow row) {
        double a = getDouble(row, def.fieldA);
        double b = getDouble(row, def.fieldB);
        double c = getDouble(row, def.fieldC);

        switch (def.calcType) {
            case 0: // NONE - 返回字段A的原始值
                return row.get(def.fieldA);
            case 1: // CONSTANT - 返回字段A作为常量
                return def.fieldA;
            case 2: // ADD: A + B
                return a + b;
            case 3: // SUBTRACT: A - B
                return a - b;
            case 4: // MULTIPLY: A * B
                return a * b;
            case 5: // DIVIDE: A / B
                return b != 0 ? a / b : null;
            case 6: // SQUARE: A * A
                return a * a;
            case 7: // SQUARE_ROOT: sqrt(A)
                return a >= 0 ? Math.sqrt(a) : null;
            case 8: // PERCENT_1: A * B / 100
                return a * b / 100.0;
            case 9: // PERCENT_2: A / B * 100
                return b != 0 ? a / b * 100.0 : null;
            case 10: // PERCENT_3: A + (A * B / 100)
                return a + (a * b / 100.0);
            case 11: // COMBINATION_1: A + B + C
                return a + b + c;
            case 12: // COMBINATION_2: A * B + C
                return a * b + c;
            case 13: // ROUND_1: round(A, precision B)
                return roundTo(a, (int) b);
            case 14: // ROUND_2: round(A)
                return Math.round(a);
            case 15: // NVL: if A is null then B else A
                Object aObj = row.get(def.fieldA);
                if (aObj == null || aObj.toString().isEmpty()) {
                    return row.get(def.fieldB);
                }
                return aObj;
            case 16: // DATE_DIFF: B - A (天数差)
                return dateDiff(row, def.fieldA, def.fieldB);
            case 17: // ADD_3: A + B + C (同11)
                return a + b + c;
            case 18: // YEAR
                return extractDatePart(row, def.fieldA, Calendar.YEAR);
            case 19: // MONTH
                return extractDatePart(row, def.fieldA, Calendar.MONTH) + 1;
            case 20: // DAY
                return extractDatePart(row, def.fieldA, Calendar.DAY_OF_MONTH);
            case 21: // HOUR
                return extractDatePart(row, def.fieldA, Calendar.HOUR_OF_DAY);
            case 22: // MINUTE
                return extractDatePart(row, def.fieldA, Calendar.MINUTE);
            case 23: // SECOND
                return extractDatePart(row, def.fieldA, Calendar.SECOND);
            case 24: // MODULUS: A % B
                return b != 0 ? a % b : null;
            case 25: // ABS
                return Math.abs(a);
            case 26: // CEIL
                return Math.ceil(a);
            case 27: // FLOOR
                return Math.floor(a);
            default:
                return a;
        }
    }

    private double roundTo(double value, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(value * factor) / factor;
    }

    private long dateDiff(DataRow row, String fieldA, String fieldB) {
        Object va = row.get(fieldA);
        Object vb = row.get(fieldB);
        if (va == null || vb == null) return 0;
        try {
            long la = parseDateMillis(va);
            long lb = parseDateMillis(vb);
            return (lb - la) / (24L * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    private int extractDatePart(DataRow row, String field, int part) {
        Object v = row.get(field);
        if (v == null) return 0;
        try {
            long millis = parseDateMillis(v);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);
            return cal.get(part);
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseDateMillis(Object v) throws Exception {
        if (v instanceof Date) return ((Date) v).getTime();
        if (v instanceof Number) return ((Number) v).longValue();
        String s = v.toString();
        // 尝试常见日期格式
        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd", "yyyyMMdd", "yyyyMMddHHmmss"
        };
        for (String p : patterns) {
            try {
                return new java.text.SimpleDateFormat(p).parse(s).getTime();
            } catch (Exception ignored) {}
        }
        return Long.parseLong(s);
    }

    private double getDouble(DataRow row, String field) {
        if (field == null || field.isEmpty()) return 0;
        Object v = row.get(field);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return 0; }
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private int getInt(Map<String, Object> map, String key, int def) {
        Object v = map.get(key);
        if (v == null) return def;
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return def; }
    }

    private static class CalcDef {
        String fieldName, fieldA, fieldB, fieldC;
        int calcType, valueType, valueLength, valuePrecision;
        boolean remove;
    }
}
