package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 线性回归算子
 * 使用最小二乘法实现线性回归，训练并预测
 */
@Component
public class LinearRegressionOperator extends AbstractOperator {

    @Override public String getId() { return "LinearRegression"; }
    @Override public String getName() { return "线性回归"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("targetColumn", "目标列", OperatorSchema.PropertyType.STRING, true)
                .property("outputColumn", "预测输出列", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        String targetColumn = ctx.getString("targetColumn");
        String outputColumn = ctx.getString("outputColumn");
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
            X[i][0] = 1.0; // intercept
            for (int j = 0; j < d; j++) {
                X[i][j + 1] = parseDouble(row.get(featureCols.get(j)));
            }
            y[i] = parseDouble(row.get(targetColumn));
        }

        // 最小二乘法: w = (X^T * X)^-1 * X^T * y
        double[] weights = solveLeastSquares(X, y);

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            double pred = 0;
            for (int j = 0; j < weights.length; j++) {
                pred += weights[j] * X[i][j];
            }
            newRow.put(outputColumn, pred);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    /**
     * 使用正规方程求解最小二乘法: w = (X^T * X)^-1 * X^T * y
     */
    private double[] solveLeastSquares(double[][] X, double[] y) {
        int n = X.length;
        int d = X[0].length;

        // 计算 X^T * X
        double[][] XtX = new double[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += X[k][i] * X[k][j];
                }
                XtX[i][j] = sum;
            }
        }

        // 计算 X^T * y
        double[] Xty = new double[d];
        for (int i = 0; i < d; i++) {
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += X[k][i] * y[k];
            }
            Xty[i] = sum;
        }

        // 求解线性方程组 XtX * w = Xty (使用高斯消元法)
        return gaussianElimination(XtX, Xty);
    }

    private double[] gaussianElimination(double[][] A, double[] b) {
        int n = b.length;

        // 增广矩阵
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }

        // 前向消元（部分主元选择）
        for (int col = 0; col < n; col++) {
            // 找最大主元
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[maxRow][col])) {
                    maxRow = row;
                }
            }
            double[] temp = aug[col];
            aug[col] = aug[maxRow];
            aug[maxRow] = temp;

            if (Math.abs(aug[col][col]) < 1e-12) {
                continue; // 奇异矩阵，跳过
            }

            for (int row = col + 1; row < n; row++) {
                double factor = aug[row][col] / aug[col][col];
                for (int j = col; j <= n; j++) {
                    aug[row][j] -= factor * aug[col][j];
                }
            }
        }

        // 回代
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= aug[i][j] * x[j];
            }
            if (Math.abs(aug[i][i]) > 1e-12) {
                x[i] /= aug[i][i];
            }
        }

        return x;
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
