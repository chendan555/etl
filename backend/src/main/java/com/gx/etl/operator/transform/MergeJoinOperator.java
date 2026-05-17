package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 列合并算子 - 将同一行的多个字段合并为一个新字段
 * 参考 Kettle ColumnMerge 实现
 *
 * mergeType: 0=字符串拼接, 1=数值数组JSON, 2=字符串数组JSON
 */
@Component
public class MergeJoinOperator extends AbstractOperator {

    @Override
    public String getId() { return "ColumnMerge"; }

    @Override
    public String getName() { return "列合并"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("newFieldName", "新字段名", OperatorSchema.PropertyType.STRING, true)
                .property("separator", "分隔符", OperatorSchema.PropertyType.STRING)
                .property("nullValue", "空值替换", OperatorSchema.PropertyType.STRING)
                .property("mergeType", "合并类型(0=字符串拼接,1=数值数组,2=字符串数组)", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String newFieldName = ctx.getString("newFieldName");
        if (newFieldName == null || newFieldName.isEmpty()) return input;

        String separator = ctx.getString("separator");
        if (separator == null) separator = "";

        String nullValue = ctx.getString("nullValue");
        int mergeType = ctx.getInt("mergeType") != null ? ctx.getInt("mergeType") : 0;

        // 获取要合并的字段列表
        List<String> fieldNames = new ArrayList<>();
        Object fieldNamesObj = ctx.get("fieldName");
        if (fieldNamesObj instanceof List) {
            for (Object item : (List<?>) fieldNamesObj) {
                fieldNames.add(item.toString());
            }
        } else if (fieldNamesObj instanceof String && !((String) fieldNamesObj).isEmpty()) {
            // 兼容逗号分隔的字符串格式
            for (String f : ((String) fieldNamesObj).split(",")) {
                fieldNames.add(f.trim());
            }
        }

        // 从 tableData 读取字段列表
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");
        if (fieldNames.isEmpty() && tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> td : tableData) {
                String name = getString(td, "name");
                if (name != null && !name.isEmpty()) fieldNames.add(name);
            }
        }

        if (fieldNames.isEmpty()) return input;

        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(newFieldName)) columns.add(newFieldName);

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            List<String> values = new ArrayList<>();

            for (String field : fieldNames) {
                Object val = row.get(field);
                if (val != null && !val.toString().trim().isEmpty()) {
                    values.add(val.toString());
                } else if (nullValue != null && !nullValue.isEmpty()) {
                    values.add(nullValue);
                } else {
                    values.add("");
                }
            }

            String merged;
            if (mergeType == 0) {
                // 字符串拼接模式
                if (nullValue != null && !nullValue.isEmpty()) {
                    // 有空值替换：保留所有值
                    merged = String.join(separator, values);
                } else {
                    // 无空值替换：过滤空值
                    List<String> filtered = new ArrayList<>();
                    for (String v : values) {
                        if (!v.isEmpty()) filtered.add(v);
                    }
                    merged = filtered.isEmpty() ? "" : String.join(separator, filtered);
                }
            } else {
                // 数组模式
                List<String> filtered = new ArrayList<>();
                for (String v : values) {
                    if (!v.isEmpty()) filtered.add(v);
                }
                if (filtered.isEmpty()) {
                    merged = "";
                } else if (mergeType == 1) {
                    // 数值数组 [1,2,3]
                    merged = "[" + String.join(",", filtered) + "]";
                } else {
                    // 字符串数组 ["a","b","c"]
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < filtered.size(); i++) {
                        if (i > 0) sb.append(",");
                        sb.append("\"").append(filtered.get(i)).append("\"");
                    }
                    sb.append("]");
                    merged = sb.toString();
                }
            }

            newRow.put(newFieldName, merged);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null && !v.toString().isEmpty() ? v.toString() : null;
    }
}
