package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StringCutOperator extends AbstractOperator {
    @Override public String getId() { return "StringCut"; }
    @Override public String getName() { return "字符串剪切"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("startIndex", "起始位置", OperatorSchema.PropertyType.NUMBER, true)
                .property("length", "长度", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        Integer start = ctx.getInt("startIndex");
        Integer length = ctx.getInt("length");
        if (fieldName == null || start == null) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val != null) {
                String s = val.toString();
                int sIdx = Math.max(0, start);
                if (sIdx >= s.length()) {
                    newRow.put(fieldName, "");
                } else if (length != null) {
                    int end = Math.min(s.length(), sIdx + length);
                    newRow.put(fieldName, s.substring(sIdx, end));
                } else {
                    newRow.put(fieldName, s.substring(sIdx));
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
