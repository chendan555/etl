package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class TypeConvertersOperator extends AbstractOperator {
    @Override public String getId() { return "TypeConverters"; }
    @Override public String getName() { return "类型转换"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("targetType", "目标类型(String/Number/Date/Integer)", OperatorSchema.PropertyType.SELECT, true)
                .property("format", "日期格式", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String targetType = ctx.getString("targetType");
        String format = ctx.getString("format");
        if (fieldName == null || targetType == null) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val != null) {
                try {
                    switch (targetType.toLowerCase()) {
                        case "string":
                            newRow.put(fieldName, val.toString());
                            break;
                        case "number":
                        case "double":
                            newRow.put(fieldName, Double.parseDouble(val.toString()));
                            break;
                        case "integer":
                        case "int":
                            newRow.put(fieldName, Integer.parseInt(val.toString()));
                            break;
                        case "date":
                            if (format != null && !format.isEmpty()) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
                                newRow.put(fieldName, LocalDate.parse(val.toString(), dtf).toString());
                            } else {
                                newRow.put(fieldName, val.toString());
                            }
                            break;
                        case "bigdecimal":
                            newRow.put(fieldName, new BigDecimal(val.toString()));
                            break;
                    }
                } catch (Exception e) {
                    newRow.put(fieldName, null);
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
