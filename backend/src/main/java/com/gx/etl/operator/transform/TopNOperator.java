package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TopNOperator extends AbstractOperator {
    @Override public String getId() { return "TopN"; }
    @Override public String getName() { return "TopN"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("count", "数量", OperatorSchema.PropertyType.NUMBER, true)
                .property("sortField", "排序字段", OperatorSchema.PropertyType.STRING)
                .property("sortOrder", "排序方式(asc/desc)", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        Integer count = ctx.getInt("count");
        String sortField = ctx.getString("sortField");
        String sortOrder = ctx.getString("sortOrder");
        if (count == null || count <= 0) return input;

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) rows.add(row);

        if (sortField != null && !sortField.isEmpty()) {
            boolean asc = !"desc".equalsIgnoreCase(sortOrder);
            rows.sort((a, b) -> {
                Object va = a.get(sortField);
                Object vb = b.get(sortField);
                if (va == null && vb == null) return 0;
                if (va == null) return asc ? -1 : 1;
                if (vb == null) return asc ? 1 : -1;
                int cmp = va.toString().compareTo(vb.toString());
                return asc ? cmp : -cmp;
            });
        }

        if (rows.size() > count) {
            rows = rows.subList(0, count);
        }
        return DataSet.of(input.getColumnNames(), rows);
    }
}
