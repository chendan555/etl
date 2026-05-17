package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValueMapperOperator extends AbstractOperator {
    @Override public String getId() { return "ValueMapper"; }
    @Override public String getName() { return "值映射"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldName", "字段名", OperatorSchema.PropertyType.STRING, true)
                .property("mappings", "映射(格式: 源值1:目标值1,源值2:目标值2)", OperatorSchema.PropertyType.STRING, true)
                .property("defaultValue", "默认值", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldName = ctx.getString("fieldName");
        String mappingsStr = ctx.getString("mappings");
        String defaultValue = ctx.getString("defaultValue");
        if (fieldName == null || mappingsStr == null) return input;

        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : mappingsStr.split(",")) {
            String[] kv = pair.split(":");
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            Object val = row.get(fieldName);
            String key = val != null ? val.toString() : "";
            newRow.put(fieldName, map.getOrDefault(key, defaultValue));
            rows.add(newRow);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
