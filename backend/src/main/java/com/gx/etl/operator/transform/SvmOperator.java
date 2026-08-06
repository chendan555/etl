package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SVM支持向量机算子
 * 实现简化的线性SVM分类器（使用SMO算法的简化版本）
 */
@Component
public class SvmOperator extends AbstractOperator {

    @Override public String getId() { return "Svm"; }
    @Override public String getName() { return "SVM支持向量机"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("targetColumn", "目标列（-1/1）", OperatorSchema.PropertyType.STRING, true)
                .property("outputColumn", "预测输出列", OperatorSchema.PropertyType.STRING, true)
                .property("learningRate", "学习率", OperatorSchema.PropertyType.NUMBER)
                .property("maxIterations", "最大迭代次数", OperatorSchema.PropertyType.NUMBER)
                .property("c", "正则化参数C", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        String targetColumn = ctx.getString("targetColumn");
        String outputColumn = ctx.getString("outputColumn");
        double lr = ctx.getDouble("learningRate") != null ? ctx.getDouble("learningRate") : 0.001;
        int maxIter = ctx.getInt("maxIterations") != null ? ctx.getInt("maxIterations") : 1000;
        double C = ctx.getDouble("c") != null ? ctx.getDouble("c") : 1.0;

        if (outputColumn == null || outputColumn.isEmpty()) outputColumn = "prediction";
        if (featureColumnsStr == null || targetColumn == null) return input;

        List<String> featureCols = parseColumnList(featureColumnsStr);
        List<DataRow> rows = input.collect();
        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(outputColumn)) columns.add(outputColumn);

        if (rows.isEmpty() || featureCols.isEmpty()) {
            return DataSet.of(columns, rows);
        }

        int n = rows.size();
        int d = featureCols.size();

        // 提取数据
        double[][] X = new double[n][d + 1]; // +1 for bias
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            X[i][0] = 1.0; // bias
            for (int j = 0; j < d; j++) {
                X[i][j + 1] = parseDouble(row.get(featureCols.get(j)));
            }
            Object targetVal = row.get(targetColumn);
            y[i] = targetVal != null ? parseDouble(targetVal) : 1.0;
            // 将标签转换为-1/1
            if (y[i] != -1.0 && y[i] != 1.0) {
                y[i] = y[i] > 0 ? 1.0 : -1.0;
            }
        }

        // 特征标准化
        double[] mean = new double[d + 1];
        double[] std = new double[d + 1];
        for (int j = 1; j <= d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) sum += X[i][j];
            mean[j] = sum / n;
            double varSum = 0;
            for (int i = 0; i < n; i++) varSum += (X[i][j] - mean[j]) * (X[i][j] - mean[j]);
            std[j] = Math.sqrt(varSum / n);
            if (std[j] == 0) std[j] = 1;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= d; j++) {
                X[i][j] = (X[i][j] - mean[j]) / std[j];
            }
        }

        // SMO简化版训练
        double[] w = new double[d + 1];
        for (int iter = 0; iter < maxIter; iter++) {
            for (int i = 0; i < n; i++) {
                double prediction = dotProduct(w, X[i]);
                if (y[i] * prediction < 1) {
                    // 违反间隔约束
                    for (int j = 0; j <= d; j++) {
                        w[j] += lr * (C * y[i] * X[i][j] - w[j] / n);
                    }
                } else {
                    // 满足间隔约束，只做正则化
                    for (int j = 0; j <= d; j++) {
                        w[j] -= lr * w[j] / n;
                    }
                }
            }
        }

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            double prediction = dotProduct(w, X[i]);
            newRow.put(outputColumn, prediction >= 0 ? 1.0 : -1.0);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private List<String> parseColumnList(String str) {
        List<String> result = new ArrayList<>();
        if (str == null || str.isEmpty()) return result;
        for (String s : str.split(",")) {
            result.add(s.trim());
        }
        return result;
    }

    private double parseDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(val.toString().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
