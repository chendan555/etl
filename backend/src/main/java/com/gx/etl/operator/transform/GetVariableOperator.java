package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetVariableOperator extends AbstractOperator {
    @Override public String getId() { return "GetVariable"; }
    @Override public String getName() { return "获取变量"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("variableName", "变量名", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String variableName = ctx.getString("variableName");
        String fieldName = ctx.getString("fieldName");
        if (variableName == null || fieldName == null) return input;

        String value = System.getProperty(variableName, System.getenv(variableName));

        List<String> columns = input.getColumnNames();
        if (!columns.contains(fieldName)) columns.add(fieldName);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            newRow.put(fieldName, value);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
