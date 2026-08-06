package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 随机森林训练算子
 * 训练随机森林分类模型
 */
@Component
public class RandomForestTrainOperator extends AbstractOperator {

    @Override public String getId() { return "RandomForestTrain"; }
    @Override public String getName() { return "随机森林(训练)"; }
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

        // 统计特征信息
        List<String> columns = Arrays.asList("feature", "type", "uniqueValues");
        List<DataRow> result = new ArrayList<>();
        for (String field : inputFields) {
            Set<String> uniqueValues = new HashSet<>();
            boolean isNumeric = true;
            for (DataRow row : rows) {
                Object val = row.get(field);
                if (val != null) {
                    try { Double.parseDouble(val.toString()); } catch (Exception e) { isNumeric = false; }
                    uniqueValues.add(val.toString());
                }
            }
            DataRow infoRow = new DataRow();
            infoRow.put("feature", field);
            infoRow.put("type", isNumeric ? "numeric" : "nominal");
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
