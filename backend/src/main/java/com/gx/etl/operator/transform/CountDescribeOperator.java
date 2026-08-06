package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 统计描述算子
 * 对数值字段进行统计描述：均值、中位数、标准差、最小值、最大值、计数等
 */
@Component
public class CountDescribeOperator extends AbstractOperator {

    @Override public String getId() { return "countDescribe"; }
    @Override public String getName() { return "统计描述"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("inputField", "输入字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String inputFieldStr = ctx.getString("inputField");
        if (inputFieldStr == null || inputFieldStr.isEmpty()) return input;

        List<String> fields = Arrays.asList(inputFieldStr.split(","));
        for (int i = 0; i < fields.size(); i++) {
            fields.set(i, fields.get(i).trim());
        }

        List<DataRow> rows = input.collect();
        if (rows.isEmpty()) return input;

        List<String> columns = Arrays.asList("field", "count", "mean", "stddev", "min", "max", "median", "sum");
        List<DataRow> result = new ArrayList<>();

        for (String field : fields) {
            List<Double> values = new ArrayList<>();
            for (DataRow row : rows) {
                Object val = row.get(field);
                if (val instanceof Number) {
                    values.add(((Number) val).doubleValue());
                }
            }

            if (values.isEmpty()) continue;

            int count = values.size();
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (double v : values) {
                sum += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
            double mean = sum / count;

            double sumSq = 0;
            for (double v : values) {
                sumSq += (v - mean) * (v - mean);
            }
            double stddev = count > 1 ? Math.sqrt(sumSq / (count - 1)) : 0;

            Collections.sort(values);
            double median;
            if (count % 2 == 0) {
                median = (values.get(count / 2 - 1) + values.get(count / 2)) / 2.0;
            } else {
                median = values.get(count / 2);
            }

            DataRow statRow = new DataRow();
            statRow.put("field", field);
            statRow.put("count", count);
            statRow.put("mean", mean);
            statRow.put("stddev", stddev);
            statRow.put("min", min);
            statRow.put("max", max);
            statRow.put("median", median);
            statRow.put("sum", sum);
            result.add(statRow);
        }

        return DataSet.of(columns, result);
    }
}
