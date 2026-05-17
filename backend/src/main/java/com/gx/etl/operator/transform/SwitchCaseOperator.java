package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SwitchCaseOperator extends AbstractOperator {
    @Override public String getId() { return "SwitchCase"; }
    @Override public String getName() { return "Switch/Case"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("resultField", "结果字段", OperatorSchema.PropertyType.STRING, true)
                .property("cases", "映射(格式: 值1:结果1,值2:结果2)", OperatorSchema.PropertyType.STRING, true)
                .property("defaultValue", "默认值", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String resultField = ctx.getString("resultField");
        String casesStr = ctx.getString("cases");
        String defaultValue = ctx.getString("defaultValue");
        if (fieldName == null || resultField == null || casesStr == null) return input;

        Map<String, String> caseMap = new LinkedHashMap<>();
        for (String pair : casesStr.split(",")) {
            String[] kv = pair.split(":");
            if (kv.length == 2) caseMap.put(kv[0].trim(), kv[1].trim());
        }

        List<String> columns = input.getColumnNames();
        if (!columns.contains(resultField)) columns.add(resultField);

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            String key = val != null ? val.toString() : "";
            newRow.put(resultField, caseMap.getOrDefault(key, defaultValue));
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
