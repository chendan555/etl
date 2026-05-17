package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CaseTransformOperator extends AbstractOperator {
    @Override public String getId() { return "CaseTransform"; }
    @Override public String getName() { return "大小写转换"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("transformType", "转换类型(upper/lower/capitalize)", OperatorSchema.PropertyType.SELECT, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String type = ctx.getString("transformType");
        if (fieldName == null || type == null) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            if (val != null) {
                String s = val.toString();
                switch (type.toLowerCase()) {
                    case "upper": newRow.put(fieldName, s.toUpperCase()); break;
                    case "lower": newRow.put(fieldName, s.toLowerCase()); break;
                    case "capitalize":
                        if (!s.isEmpty()) {
                            newRow.put(fieldName, Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase());
                        }
                        break;
                }
            }
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
