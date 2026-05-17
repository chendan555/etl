package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DenormaliserOperator extends AbstractOperator {

    @Override
    public String getId() { return "Denormaliser"; }

    @Override
    public String getName() { return "列转行"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("groupFields", "分组字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("unpivotFields", "转行字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("nameColumn", "名称列名", OperatorSchema.PropertyType.STRING)
                .property("valueColumn", "值列名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String groupFieldsStr = ctx.getString("groupFields");
        String unpivotFieldsStr = ctx.getString("unpivotFields");
        String nameColumn = ctx.getString("nameColumn");
        String valueColumn = ctx.getString("valueColumn");
        if (nameColumn == null || nameColumn.isEmpty()) nameColumn = "name";
        if (valueColumn == null || valueColumn.isEmpty()) valueColumn = "value";

        String[] groupFields = groupFieldsStr.split(",");
        for (int i = 0; i < groupFields.length; i++) groupFields[i] = groupFields[i].trim();

        String[] unpivotFields = unpivotFieldsStr.split(",");
        for (int i = 0; i < unpivotFields.length; i++) unpivotFields[i] = unpivotFields[i].trim();

        List<String> newColumns = new ArrayList<>(Arrays.asList(groupFields));
        newColumns.add(nameColumn);
        newColumns.add(valueColumn);

        List<DataRow> result = new ArrayList<>();
        for (DataRow row : input) {
            for (String field : unpivotFields) {
                DataRow newRow = new DataRow();
                for (String gf : groupFields) {
                    newRow.put(gf, row.get(gf));
                }
                newRow.put(nameColumn, field);
                newRow.put(valueColumn, row.get(field));
                result.add(newRow);
            }
        }

        return DataSet.of(newColumns, result);
    }
}
