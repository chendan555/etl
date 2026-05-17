package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SetValueFieldOperator extends AbstractOperator {
    @Override public String getId() { return "SetValueField"; }
    @Override public String getName() { return "设置字段值"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("fieldValue", "值", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String fieldValue = ctx.getString("fieldValue");
        if (fieldName == null || fieldName.isEmpty()) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            newRow.put(fieldName, fieldValue);
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
