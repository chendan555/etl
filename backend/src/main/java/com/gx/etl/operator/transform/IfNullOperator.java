package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class IfNullOperator extends AbstractOperator {
    @Override public String getId() { return "IfNull"; }
    @Override public String getName() { return "空值处理"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("replaceValue", "替换值", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String replaceValue = ctx.getString("replaceValue");
        if (fieldName == null || fieldName.isEmpty()) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val == null || val.toString().isEmpty()) {
                newRow.put(fieldName, replaceValue);
            }
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
