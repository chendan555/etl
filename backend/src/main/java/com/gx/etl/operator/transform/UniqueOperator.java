package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 去除重复算子 - 基于指定字段去重，支持忽略大小写
 * 参考 Kettle UniqueRowsMeta 实现
 */
@Component
public class UniqueOperator extends AbstractOperator {

    @Override
    public String getId() { return "Unique"; }

    @Override
    public String getName() { return "去除重复"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("zjjsl", "增加计数列", OperatorSchema.PropertyType.BOOLEAN)
                .property("xlmc", "计数列名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");
        boolean addCount = Boolean.TRUE.equals(ctx.getBoolean("zjjsl"));
        String countField = ctx.getString("xlmc");
        if (countField == null || countField.isEmpty()) countField = "count_field";

        // 解析比较字段配置
        List<CompareField> compareFields = new ArrayList<>();
        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> td : tableData) {
                String name = getString(td, "name");
                if (name == null || name.isEmpty()) continue;
                CompareField cf = new CompareField();
                cf.name = name;
                cf.caseInsensitive = "Y".equalsIgnoreCase(getString(td, "case_insensitive"));
                compareFields.add(cf);
            }
        }

        // 兼容旧格式：从 ruleForm.fields 读取
        if (compareFields.isEmpty()) {
            String fieldsStr = ctx.getString("fields");
            if (fieldsStr != null && !fieldsStr.isEmpty()) {
                for (String f : fieldsStr.split(",")) {
                    CompareField cf = new CompareField();
                    cf.name = f.trim();
                    cf.caseInsensitive = false;
                    compareFields.add(cf);
                }
            }
        }

        if (compareFields.isEmpty()) return input;

        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (addCount && !columns.contains(countField)) columns.add(countField);

        Map<String, Long> countMap = new LinkedHashMap<>();
        List<DataRow> result = new ArrayList<>();

        for (DataRow row : input.collect()) {
            StringBuilder key = new StringBuilder();
            for (CompareField cf : compareFields) {
                Object v = row.get(cf.name);
                String s = v != null ? v.toString() : "NULL";
                if (cf.caseInsensitive) s = s.toLowerCase();
                key.append(s).append("|");
            }
            String keyStr = key.toString();
            Long count = countMap.get(keyStr);
            if (count == null) {
                countMap.put(keyStr, 1L);
                DataRow newRow = row.copy();
                if (addCount) newRow.put(countField, 1L);
                result.add(newRow);
            } else {
                countMap.put(keyStr, count + 1);
                if (addCount) {
                    // 更新已有行的计数
                    for (DataRow r : result) {
                        if (matchKey(r, compareFields, keyStr)) {
                            r.put(countField, count + 1);
                            break;
                        }
                    }
                }
            }
        }

        return DataSet.of(columns, result);
    }

    private boolean matchKey(DataRow row, List<CompareField> compareFields, String targetKey) {
        StringBuilder key = new StringBuilder();
        for (CompareField cf : compareFields) {
            Object v = row.get(cf.name);
            String s = v != null ? v.toString() : "NULL";
            if (cf.caseInsensitive) s = s.toLowerCase();
            key.append(s).append("|");
        }
        return key.toString().equals(targetKey);
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private static class CompareField {
        String name;
        boolean caseInsensitive;
    }
}
