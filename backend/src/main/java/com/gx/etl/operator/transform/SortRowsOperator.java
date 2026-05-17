package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 排序记录算子 - 支持多字段排序，升序/降序，大小写敏感
 * 参考 Kettle SortRowsMeta 实现
 */
@Component
public class SortRowsOperator extends AbstractOperator {

    @Override
    public String getId() { return "SortRows"; }

    @Override
    public String getName() { return "排序记录"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("unique_rows", "仅输出唯一行", OperatorSchema.PropertyType.BOOLEAN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSet execute(OperatorContext ctx, DataSet input) {
        List<Map<String, Object>> tableData = (List<Map<String, Object>>) ctx.get("_tableData");
        boolean uniqueRows = "Y".equalsIgnoreCase(ctx.getString("unique_rows"));

        // 解析排序字段配置
        List<SortField> sortFields = new ArrayList<>();
        if (tableData != null && !tableData.isEmpty()) {
            for (Map<String, Object> td : tableData) {
                String name = getString(td, "name");
                if (name == null || name.isEmpty()) continue;
                SortField sf = new SortField();
                sf.name = name;
                sf.ascending = "Y".equalsIgnoreCase(getString(td, "ascending"));
                sf.caseSensitive = "Y".equalsIgnoreCase(getString(td, "case_sensitive"));
                sortFields.add(sf);
            }
        }

        // 如果没有配置排序字段，尝试从 ruleForm 兼容旧格式
        if (sortFields.isEmpty()) {
            String sortField = ctx.getString("sortField");
            if (sortField != null && !sortField.isEmpty()) {
                SortField sf = new SortField();
                sf.name = sortField;
                sf.ascending = Boolean.TRUE.equals(ctx.getBoolean("ascending"));
                sf.caseSensitive = Boolean.TRUE.equals(ctx.getBoolean("caseSensitive"));
                sortFields.add(sf);
            }
        }

        if (sortFields.isEmpty()) return input;

        List<DataRow> rows = input.collect();
        rows.sort((a, b) -> {
            for (SortField sf : sortFields) {
                Object va = a.get(sf.name);
                Object vb = b.get(sf.name);
                int cmp = compareValues(va, vb, sf.caseSensitive);
                if (cmp != 0) return sf.ascending ? cmp : -cmp;
            }
            return 0;
        });

        // 唯一行过滤
        if (uniqueRows) {
            List<DataRow> unique = new ArrayList<>();
            DataRow prev = null;
            for (DataRow row : rows) {
                if (prev == null || !rowsEqual(prev, row, sortFields)) {
                    unique.add(row);
                }
                prev = row;
            }
            return DataSet.of(input.getColumnNames(), unique);
        }

        return DataSet.of(input.getColumnNames(), rows);
    }

    private int compareValues(Object va, Object vb, boolean caseSensitive) {
        if (va == null && vb == null) return 0;
        if (va == null) return 1;
        if (vb == null) return -1;

        if (va instanceof Number && vb instanceof Number) {
            return Double.compare(((Number) va).doubleValue(), ((Number) vb).doubleValue());
        }

        String sa = va.toString();
        String sb = vb.toString();
        if (!caseSensitive) {
            sa = sa.toLowerCase();
            sb = sb.toLowerCase();
        }
        return sa.compareTo(sb);
    }

    private boolean rowsEqual(DataRow a, DataRow b, List<SortField> sortFields) {
        for (SortField sf : sortFields) {
            Object va = a.get(sf.name);
            Object vb = b.get(sf.name);
            if (compareValues(va, vb, sf.caseSensitive) != 0) return false;
        }
        return true;
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private static class SortField {
        String name;
        boolean ascending = true;
        boolean caseSensitive = true;
    }
}
