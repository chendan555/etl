package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 逻辑回归算子
 * 使用梯度下降法实现逻辑回归（二分类）
 */
@Component
public class LogisticsRegressionOperator extends AbstractOperator {

    @Override public String getId() { return "LogisticsRegression"; }
    @Override public String getName() { return "逻辑回归"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("targetColumn", "目标列（0/1）", OperatorSchema.PropertyType.STRING, true)
                .property("outputColumn", "预测输出列", OperatorSchema.PropertyType.STRING, true)
                .property("learningRate", "学习率", OperatorSchema.PropertyType.NUMBER)
                .property("maxIterations", "最大迭代次数", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        String targetColumn = ctx.getString("targetColumn");
        String outputColumn = ctx.getString("outputColumn");
        double lr = ctx.getDouble("learningRate") != null ? ctx.getDouble("learningRate") : 0.01;
        int maxIter = ctx.getInt("maxIterations") != null ? ctx.getInt("maxIterations") : 1000;

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

        // 提取特征和目标
        double[][] X = new double[n][d + 1]; // +1 for intercept
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            X[i][0] = 1.0;
            for (int j = 0; j < d; j++) {
                X[i][j + 1] = parseDouble(row.get(featureCols.get(j)));
            }
            y[i] = parseDouble(row.get(targetColumn));
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

        // 梯度下降训练
        double[] weights = new double[d + 1];
        for (int iter = 0; iter < maxIter; iter++) {
            double[] gradient = new double[d + 1];
            for (int i = 0; i < n; i++) {
                double z = dotProduct(weights, X[i]);
                double h = sigmoid(z);
                double error = h - y[i];
                for (int j = 0; j <= d; j++) {
                    gradient[j] += error * X[i][j];
                }
            }
            for (int j = 0; j <= d; j++) {
                weights[j] -= lr * gradient[j] / n;
            }
        }

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            double z = dotProduct(weights, X[i]);
            double prob = sigmoid(z);
            newRow.put(outputColumn, prob >= 0.5 ? 1.0 : 0.0);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private double sigmoid(double z) {
        if (z >= 0) {
            return 1.0 / (1.0 + Math.exp(-z));
        } else {
            double expZ = Math.exp(z);
            return expZ / (1.0 + expZ);
        }
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
