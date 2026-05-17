package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ColumnOperationOperator extends AbstractOperator {
    @Override public String getId() { return "ColumnOperation"; }
    @Override public String getName() { return "列运算"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldA", "字段A", OperatorSchema.PropertyType.STRING, true)
                .property("fieldB", "字段B", OperatorSchema.PropertyType.STRING, true)
                .property("operator", "运算符(+,-,*,/)", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldA = ctx.getString("fieldA");
        String fieldB = ctx.getString("fieldB");
        String op = ctx.getString("operator");
        String resultField = ctx.getString("resultField");
        if (fieldA == null || fieldB == null || resultField == null) return input;

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object va = row.get(fieldA);
            Object vb = row.get(fieldB);
            Double a = parseDouble(va);
            Double b = parseDouble(vb);
            Double result = null;
            if (a != null && b != null && op != null) {
                switch (op) {
                    case "+": result = a + b; break;
                    case "-": result = a - b; break;
                    case "*": result = a * b; break;
                    case "/": result = b != 0 ? a / b : null; break;
                }
            }
            newRow.put(resultField, result);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }

    private Double parseDouble(Object v) {
        if (v == null) return null;
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
}
