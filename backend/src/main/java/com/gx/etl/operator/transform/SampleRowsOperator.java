package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SampleRowsOperator extends AbstractOperator {
    @Override public String getId() { return "SampleRows"; }
    @Override public String getName() { return "抽样"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("sampleSize", "抽样数量", OperatorSchema.PropertyType.NUMBER, true)
                .property("sampleMethod", "方式(count/percent)", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        Integer sampleSize = ctx.getInt("sampleSize");
        String method = ctx.getString("sampleMethod");
        if (sampleSize == null || sampleSize <= 0) return input;

        List<DataRow> allRows = new ArrayList<>();
        for (DataRow row : input) allRows.add(row);

        List<DataRow> sampled;
        if ("percent".equalsIgnoreCase(method)) {
            int count = (int) (allRows.size() * sampleSize / 100.0);
            Collections.shuffle(allRows);
            sampled = allRows.subList(0, Math.min(count, allRows.size()));
        } else {
            Collections.shuffle(allRows);
            sampled = allRows.subList(0, Math.min(sampleSize, allRows.size()));
        }
        return DataSet.of(input.getColumnNames(), sampled);
    }
}
