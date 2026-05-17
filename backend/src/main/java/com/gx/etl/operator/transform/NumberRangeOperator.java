package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

@Component
public class NumberRangeOperator extends AbstractOperator {
    @Override public String getId() { return "NumberRange"; }
    @Override public String getName() { return "数字范围"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("minValue", "最小值", OperatorSchema.PropertyType.NUMBER)
                .property("maxValue", "最大值", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        Object minObj = ctx.get("minValue");
        Object maxObj = ctx.get("maxValue");
        Double minVal = minObj != null ? Double.parseDouble(minObj.toString()) : null;
        Double maxVal = maxObj != null ? Double.parseDouble(maxObj.toString()) : null;
        if (fieldName == null || fieldName.isEmpty()) return input;

        java.util.List<DataRow> rows = new java.util.ArrayList<>();
        for (DataRow row : input) {
            Object val = row.get(fieldName);
            if (val != null) {
                try {
                    double d = Double.parseDouble(val.toString());
                    if (minVal != null && d < minVal) continue;
                    if (maxVal != null && d > maxVal) continue;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            rows.add(row);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
