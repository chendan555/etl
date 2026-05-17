package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChangeColumnNameOperator extends AbstractOperator {
    @Override public String getId() { return "ChangeColumnName"; }
    @Override public String getName() { return "改名"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("oldName", "原字段名", OperatorSchema.PropertyType.STRING, true)
                .property("newName", "新字段名", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String oldName = ctx.getString("oldName");
        String newName = ctx.getString("newName");
        if (oldName == null || newName == null || oldName.isEmpty() || newName.isEmpty()) return input;

        List<String> columns = new ArrayList<>();
        for (String col : input.getColumnNames()) {
            columns.add(col.equals(oldName) ? newName : col);
        }

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = new DataRow();
            for (Map.Entry<String, Object> entry : row.getFields().entrySet()) {
                String key = entry.getKey().equals(oldName) ? newName : entry.getKey();
                newRow.put(key, entry.getValue());
            }
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }
}
