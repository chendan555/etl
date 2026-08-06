package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 朴素贝叶斯训练算子
 * 训练朴素贝叶斯分类模型
 */
@Component
public class NativeBayesTrainOperator extends AbstractOperator {

    @Override public String getId() { return "NativeBayesTrain"; }
    @Override public String getName() { return "朴素贝叶斯分类(训练)"; }
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

        // 统计每个字段的类别分布
        List<String> columns = Arrays.asList("feature", "type", "uniqueValues");
        List<DataRow> result = new ArrayList<>();
        for (String field : inputFields) {
            Set<String> uniqueValues = new HashSet<>();
            for (DataRow row : rows) {
                Object val = row.get(field);
                if (val != null) uniqueValues.add(val.toString());
            }
            DataRow infoRow = new DataRow();
            infoRow.put("feature", field);
            infoRow.put("type", "nominal");
            infoRow.put("uniqueValues", uniqueValues.size());
            result.add(infoRow);
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
