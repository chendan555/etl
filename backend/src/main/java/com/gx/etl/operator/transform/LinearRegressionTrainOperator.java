package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 线性回归训练算子
 * 使用最小二乘法训练线性回归模型
 */
@Component
public class LinearRegressionTrainOperator extends AbstractOperator {

    @Override public String getId() { return "LinearRegressionTrain"; }
    @Override public String getName() { return "线性回归(训练)"; }
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

        // 训练线性回归模型（计算系数）
        int n = rows.size();
        int d = inputFields.size();

        // 提取特征和目标（最后一列为目标）
        String targetField = inputFields.get(inputFields.size() - 1);
        List<String> featureFields = inputFields.subList(0, inputFields.size() - 1);

        double[][] X = new double[n][featureFields.size() + 1];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            X[i][0] = 1.0;
            for (int j = 0; j < featureFields.size(); j++) {
                X[i][j + 1] = toDouble(row.get(featureFields.get(j)));
            }
            y[i] = toDouble(row.get(targetField));
        }

        // 计算权重
        double[] weights = solveLeastSquares(X, y);

        // 输出训练结果（权重信息）
        List<String> columns = Arrays.asList("coefficient", "value");
        List<DataRow> result = new ArrayList<>();
        DataRow interceptRow = new DataRow();
        interceptRow.put("coefficient", "intercept");
        interceptRow.put("value", weights[0]);
        result.add(interceptRow);
        for (int i = 0; i < featureFields.size(); i++) {
            DataRow wRow = new DataRow();
            wRow.put("coefficient", featureFields.get(i));
            wRow.put("value", weights[i + 1]);
            result.add(wRow);
        }

        return DataSet.of(columns, result);
    }

    private double[] solveLeastSquares(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;
        double[][] XtX = new double[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) sum += X[k][i] * X[k][j];
                XtX[i][j] = sum;
            }
        }
        double[] Xty = new double[d];
        for (int i = 0; i < d; i++) {
            double sum = 0;
            for (int k = 0; k < n; k++) sum += X[k][i] * y[k];
            Xty[i] = sum;
        }
        return gaussianElimination(XtX, Xty);
    }

    private double[] gaussianElimination(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }
        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[maxRow][col])) maxRow = row;
            }
            double[] temp = aug[col]; aug[col] = aug[maxRow]; aug[maxRow] = temp;
            if (Math.abs(aug[col][col]) < 1e-12) continue;
            for (int row = col + 1; row < n; row++) {
                double factor = aug[row][col] / aug[col][col];
                for (int j = col; j <= n; j++) aug[row][j] -= factor * aug[col][j];
            }
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) x[i] -= aug[i][j] * x[j];
            if (Math.abs(aug[i][i]) > 1e-12) x[i] /= aug[i][i];
        }
        return x;
    }

    @SuppressWarnings("unchecked")
    private List<String> parseInputFields(OperatorContext ctx) {
        Object fieldObj = ctx.get("inputField");
        if (fieldObj instanceof List) return (List<String>) fieldObj;
        if (fieldObj instanceof String) return Arrays.asList(((String) fieldObj).split(","));
        return Collections.emptyList();
    }

    private double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0.0; }
    }
}
