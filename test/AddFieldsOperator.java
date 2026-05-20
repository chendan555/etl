package com.example;

import com.gx.etl.operator.core.*;
import java.util.ArrayList;
import java.util.List;

public class AddFieldsOperator extends AbstractOperator {

    @Override
    public String getId() {
        return "AddFields";
    }

    @Override
    public String getName() {
        return "字段相加";
    }

    @Override
    public String getCategory() {
        return "transform";
    }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("field1", "字段1", OperatorSchema.PropertyType.STRING, true)
                .property("field2", "字段2", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String field1 = ctx.getString("field1");
        String field2 = ctx.getString("field2");
        String resultField = ctx.getString("resultField");

        if (field1 == null || field2 == null || resultField == null) {
            return input;
        }

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) {
            columns.add(resultField);
        }

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val1 = row.get(field1);
            Object val2 = row.get(field2);

            double sum = 0;
            try {
                if (val1 != null) sum += Double.parseDouble(val1.toString());
                if (val2 != null) sum += Double.parseDouble(val2.toString());
            } catch (NumberFormatException e) {
                // 如果不是数字，尝试字符串拼接
                newRow.put(resultField, String.valueOf(val1) + String.valueOf(val2));
                rows.add(newRow);
                continue;
            }

            // 如果结果是整数，去掉小数点
            if (sum == Math.floor(sum) && !Double.isInfinite(sum)) {
                newRow.put(resultField, (long) sum);
            } else {
                newRow.put(resultField, sum);
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
