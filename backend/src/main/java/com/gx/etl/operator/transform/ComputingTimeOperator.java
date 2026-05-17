package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ComputingTimeOperator extends AbstractOperator {
    @Override public String getId() { return "ComputingTime"; }
    @Override public String getName() { return "时间计算"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true)
                .property("format", "格式", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String resultField = ctx.getString("resultField");
        String format = ctx.getString("format");
        if (resultField == null) resultField = "currentTime";

        String value;
        if (format != null && !format.isEmpty()) {
            value = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
        } else {
            value = LocalDateTime.now().toString();
        }

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            newRow.put(resultField, value);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
