package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StringOperationsOperator extends AbstractOperator {
    @Override public String getId() { return "StringOperations"; }
    @Override public String getName() { return "字符串操作"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("operation", "操作(trim/upper/lower/reverse/length)", OperatorSchema.PropertyType.SELECT, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String operation = ctx.getString("operation");
        String resultField = ctx.getString("resultField");
        if (fieldName == null || operation == null) return input;
        if (resultField == null || resultField.isEmpty()) resultField = fieldName;

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val != null) {
                String s = val.toString();
                switch (operation.toLowerCase()) {
                    case "trim": newRow.put(resultField, s.trim()); break;
                    case "upper": newRow.put(resultField, s.toUpperCase()); break;
                    case "lower": newRow.put(resultField, s.toLowerCase()); break;
                    case "reverse": newRow.put(resultField, new StringBuilder(s).reverse().toString()); break;
                    case "length": newRow.put(resultField, s.length()); break;
                    default: newRow.put(resultField, s);
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
