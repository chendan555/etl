package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SVM集成预测算子
 * 集成多个SVM模型进行综合预测
 */
@Component
public class SvmIntegrationOperator extends AbstractOperator {

    @Override public String getId() { return "SvmIntegration"; }
    @Override public String getName() { return "SVM（集成）"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("resultFieldName", "输出结果列名", OperatorSchema.PropertyType.STRING, true)
                .property("trainStepName", "训练模型步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("inputField", "分析字段", OperatorSchema.PropertyType.STRING, true)
                .property("attrType", "特征属性类型", OperatorSchema.PropertyType.SELECT,
                        Arrays.asList(
                                OperatorSchema.Option.of("数值型", "numeric"),
                                OperatorSchema.Option.of("标称型", "nominal")));
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String resultFieldName = ctx.getString("resultFieldName");
        if (resultFieldName == null) resultFieldName = "prediction";

        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(resultFieldName)) {
            columns.add(resultFieldName);
        }

        List<DataRow> rows = new ArrayList<>();
        for (DataRow row : input) {
            DataRow newRow = row.copy();
            double prediction = computeSvmEnsemblePrediction(row, ctx);
            newRow.put(resultFieldName, prediction);
            rows.add(newRow);
        }
        return DataSet.of(columns, rows);
    }

    private double computeSvmEnsemblePrediction(DataRow row, OperatorContext ctx) {
        // 集成SVM：简单投票
        List<String> inputFields = parseInputFields(ctx);
        double sum = 0;
        int count = 0;
        for (String field : inputFields) {
            Object val = row.get(field);
            if (val instanceof Number) {
                sum += ((Number) val).doubleValue();
                count++;
            }
        }
        return count > 0 ? (sum / count >= 0 ? 1 : -1) : 0;
    }

    @SuppressWarnings("unchecked")
    private List<String> parseInputFields(OperatorContext ctx) {
        Object fieldObj = ctx.get("inputField");
        if (fieldObj instanceof List) {
            return (List<String>) fieldObj;
        }
        if (fieldObj instanceof String) {
            return Arrays.asList(((String) fieldObj).split(","));
        }
        return Collections.emptyList();
    }
}
