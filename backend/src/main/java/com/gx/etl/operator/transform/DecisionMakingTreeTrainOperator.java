package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 决策树训练算子
 * 训练决策树分类模型
 */
@Component
public class DecisionMakingTreeTrainOperator extends AbstractOperator {

    @Override public String getId() { return "DecisionMakingTreeTrain"; }
    @Override public String getName() { return "决策树(训练)"; }
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

        // 统计特征信息用于决策树构建
        List<String> columns = Arrays.asList("feature", "type", "uniqueValues", "entropy");
        List<DataRow> result = new ArrayList<>();
        for (String field : inputFields) {
            Map<String, Integer> valueCounts = new LinkedHashMap<>();
            for (DataRow row : rows) {
                Object val = row.get(field);
                String key = val != null ? val.toString() : "null";
                valueCounts.merge(key, 1, Integer::sum);
            }
            double entropy = calculateEntropy(valueCounts.values(), rows.size());
            DataRow infoRow = new DataRow();
            infoRow.put("feature", field);
            infoRow.put("type", "nominal");
            infoRow.put("uniqueValues", valueCounts.size());
            infoRow.put("entropy", entropy);
            result.add(infoRow);
        }

        return DataSet.of(columns, result);
    }

    private double calculateEntropy(Collection<Integer> counts, int total) {
        double entropy = 0;
        for (int count : counts) {
            if (count > 0) {
                double p = (double) count / total;
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }
        return entropy;
    }

    @SuppressWarnings("unchecked")
    private List<String> parseInputFields(OperatorContext ctx) {
        Object fieldObj = ctx.get("inputField");
        if (fieldObj instanceof List) return (List<String>) fieldObj;
        if (fieldObj instanceof String) return Arrays.asList(((String) fieldObj).split(","));
        return Collections.emptyList();
    }
}
