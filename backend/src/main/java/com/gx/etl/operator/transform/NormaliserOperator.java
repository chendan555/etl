package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NormaliserOperator extends AbstractOperator {

    @Override
    public String getId() { return "Normaliser"; }

    @Override
    public String getName() { return "行转列"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("groupFields", "分组字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("nameField", "名称字段", OperatorSchema.PropertyType.STRING, true)
                .property("valueField", "值字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String groupFieldsStr = ctx.getString("groupFields");
        String nameField = ctx.getString("nameField");
        String valueField = ctx.getString("valueField");

        String[] groupFields = groupFieldsStr.split(",");
        for (int i = 0; i < groupFields.length; i++) groupFields[i] = groupFields[i].trim();

        // Collect all unique name values to create columns
        Set<String> nameValues = new LinkedHashSet<>();
        List<DataRow> allRows = input.collect();
        for (DataRow row : allRows) {
            Object nameVal = row.get(nameField);
            if (nameVal != null) nameValues.add(nameVal.toString());
        }

        List<String> newColumns = new ArrayList<>(Arrays.asList(groupFields));
        newColumns.addAll(nameValues);

        // Group by group fields
        Map<String, DataRow> grouped = new LinkedHashMap<>();
        for (DataRow row : allRows) {
            StringBuilder key = new StringBuilder();
            for (String gf : groupFields) {
                key.append(row.get(gf)).append("|");
            }
            String keyStr = key.toString();
            DataRow result = grouped.computeIfAbsent(keyStr, k -> {
                DataRow r = new DataRow();
                for (String gf : groupFields) {
                    r.put(gf, row.get(gf));
                }
                return r;
            });
            Object nameVal = row.get(nameField);
            Object valVal = row.get(valueField);
            if (nameVal != null) {
                result.put(nameVal.toString(), valVal);
            }
        }

        return DataSet.of(newColumns, new ArrayList<>(grouped.values()));
    }
}
