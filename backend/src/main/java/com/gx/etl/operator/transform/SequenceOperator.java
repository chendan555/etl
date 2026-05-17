package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SequenceOperator extends AbstractOperator {
    @Override public String getId() { return "Sequence"; }
    @Override public String getName() { return "序列"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("startValue", "起始值", OperatorSchema.PropertyType.NUMBER)
                .property("increment", "递增", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        Long startVal = ctx.getLong("startValue");
        Long inc = ctx.getLong("increment");
        if (startVal == null) startVal = 1L;
        if (inc == null) inc = 1L;
        if (fieldName == null || fieldName.isEmpty()) fieldName = "seq";

        List<String> columns = input.getColumnNames();
        if (!columns.contains(fieldName)) columns.add(fieldName);

        java.util.List<DataRow> rows = new java.util.ArrayList<>();
        long seq = startVal;
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            newRow.put(fieldName, seq);
            rows.add(newRow);
            seq += inc;
        }
        return DataSet.of(columns, rows);
    }
}
