package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddConstantOperator extends AbstractOperator {
    @Override public String getId() { return "AddConstant"; }
    @Override public String getName() { return "增加常量"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("fieldValue", "常量值", OperatorSchema.PropertyType.STRING, true)
                .property("fieldType", "类型", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String fieldValue = ctx.getString("fieldValue");
        if (fieldName == null || fieldName.isEmpty()) return input;

        List<String> columns = input.getColumnNames();
        if (!columns.contains(fieldName)) columns.add(fieldName);

        java.util.List<DataRow> rows = new java.util.ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            newRow.put(fieldName, fieldValue);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
