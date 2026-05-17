package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CheckSumOperator extends AbstractOperator {
    @Override public String getId() { return "CheckSum"; }
    @Override public String getName() { return "校验和"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fields", "字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldsStr = ctx.getString("fields");
        String resultField = ctx.getString("resultField");
        if (fieldsStr == null || resultField == null) return input;

        String[] fields = fieldsStr.split(",");
        for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            StringBuilder sb = new StringBuilder();
            for (String f : fields) {
                Object val = row.get(f);
                sb.append(val != null ? val.toString() : "");
            }
            newRow.put(resultField, sb.toString().hashCode());
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
