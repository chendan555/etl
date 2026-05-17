package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ColumnSplitsOperator extends AbstractOperator {
    @Override public String getId() { return "ColumnSplits"; }
    @Override public String getName() { return "列拆分"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("splitField", "拆分字段", OperatorSchema.PropertyType.STRING, true)
                .property("delimiter", "分隔符", OperatorSchema.PropertyType.STRING, true)
                .property("resultFields", "结果字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String splitField = ctx.getString("splitField");
        String delimiter = ctx.getString("delimiter");
        String resultFieldsStr = ctx.getString("resultFields");
        if (splitField == null || delimiter == null || resultFieldsStr == null) return input;

        String[] resultFields = resultFieldsStr.split(",");
        for (int i = 0; i < resultFields.length; i++) resultFields[i] = resultFields[i].trim();

        List<String> columns = input.getColumnNames();
        for (String f : resultFields) {
            if (!columns.contains(f)) columns.add(f);
        }

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(splitField);
            if (val != null) {
                String[] parts = val.toString().split(java.util.regex.Pattern.quote(delimiter), -1);
                for (int i = 0; i < resultFields.length; i++) {
                    newRow.put(resultFields[i], i < parts.length ? parts[i] : null);
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
