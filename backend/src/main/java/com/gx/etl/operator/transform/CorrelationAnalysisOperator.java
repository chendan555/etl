package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 相关系数分析算子
 * 计算两列数值的皮尔逊相关系数，输出单行结果。
 * 参考: gx-plugin-correlationanalysis CorrelationAnalysis + GaussCheck.caculatePearson
 */
@Component
public class CorrelationAnalysisOperator extends AbstractOperator {

    @Override public String getId() { return "CorrelationAnalysis"; }
    @Override public String getName() { return "相关系数分析"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("fieldX", "X字段", OperatorSchema.PropertyType.STRING, true)
                .property("fieldY", "Y字段", OperatorSchema.PropertyType.STRING, true)
                .property("outFieldName", "输出字段名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String fieldX = ctx.getString("fieldX");
        String fieldY = ctx.getString("fieldY");
        String outFieldName = ctx.getString("outFieldName");
        if (outFieldName == null || outFieldName.isEmpty()) outFieldName = "pearson_r";

        if (fieldX == null || fieldX.isEmpty() || fieldY == null || fieldY.isEmpty()) {
            return input;
        }

        // 收集所有行的 X 和 Y 值
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();
        List<String> columns = new ArrayList<>(input.getColumnNames());

        for (DataRow row : input) {
            xList.add(toDouble(row.get(fieldX)));
            yList.add(toDouble(row.get(fieldY)));
        }

        // 计算皮尔逊相关系数
        double pearson = calculatePearson(xList, yList);

        // 输出单行结果
        if (!columns.contains(outFieldName)) {
            columns.add(outFieldName);
        }
        DataRow resultRow = new DataRow();
        resultRow.put(outFieldName, pearson);
        List<DataRow> rows = Collections.singletonList(resultRow);
        return DataSet.of(columns, rows);
    }

    /**
     * 皮尔逊相关系数计算
     * 公式: r = (sumXY - sumX*sumY/n) / sqrt((sumPowX - sumX^2/n) * (sumPowY - sumY^2/n))
     */
    private double calculatePearson(List<Double> xList, List<Double> yList) {
        int n = Math.min(xList.size(), yList.size());
        if (n == 0) return 0.0;

        double sumX = 0, sumY = 0, sumXY = 0, sumPowX = 0, sumPowY = 0;
        for (int i = 0; i < n; i++) {
            double x = xList.get(i);
            double y = yList.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumPowX += x * x;
            sumPowY += y * y;
        }

        double denominator = Math.sqrt((sumPowX - sumX * sumX / n) * (sumPowY - sumY * sumY / n));
        if (denominator == 0) return 0.0;
        return (sumXY - sumX * sumY / n) / denominator;
    }

    private double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
