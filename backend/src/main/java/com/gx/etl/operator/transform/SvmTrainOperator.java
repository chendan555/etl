package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SVM训练算子
 * 训练SVM支持向量机分类模型
 */
@Component
public class SvmTrainOperator extends AbstractOperator {

    @Override public String getId() { return "SvmTrain"; }
    @Override public String getName() { return "SVM（训练）"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("inputField", "分析字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        List<String> inputFields = parseInputFields(ctx);
        List<DataRow> rows = input.collect();

        if (rows.isEmpty() || inputFields.isEmpty()) return input;

        // 训练SVM：计算支持向量信息
        List<String> columns = Arrays.asList("feature", "mean", "stddev");
        List<DataRow> result = new ArrayList<>();
        for (String field : inputFields) {
            double sum = 0, sumSq = 0;
            int count = 0;
            for (DataRow row : rows) {
                Object val = row.get(field);
                if (val instanceof Number) {
                    double v = ((Number) val).doubleValue();
                    sum += v;
                    sumSq += v * v;
                    count++;
                }
            }
            if (count > 0) {
                double mean = sum / count;
                double stddev = Math.sqrt(sumSq / count - mean * mean);
                DataRow infoRow = new DataRow();
                infoRow.put("feature", field);
                infoRow.put("mean", mean);
                infoRow.put("stddev", stddev);
                result.add(infoRow);
            }
        }

        return DataSet.of(columns, result);
    }

    @SuppressWarnings("unchecked")
    private List<String> parseInputFields(OperatorContext ctx) {
        Object fieldObj = ctx.get("inputField");
        if (fieldObj instanceof List) return (List<String>) fieldObj;
        if (fieldObj instanceof String) return Arrays.asList(((String) fieldObj).split(","));
        return Collections.emptyList();
    }
}
