package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 朴素贝叶斯算子
 * 实现朴素贝叶斯分类器（支持数值特征的高斯朴素贝叶斯）
 */
@Component
public class NativeBayesOperator extends AbstractOperator {

    @Override public String getId() { return "NativeBayes"; }
    @Override public String getName() { return "朴素贝叶斯"; }
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

        // 提取数据
        double[][] X = new double[n][d];
        String[] y = new String[n];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            for (int j = 0; j < d; j++) {
                X[i][j] = parseDouble(row.get(featureCols.get(j)));
            }
            Object targetVal = row.get(targetColumn);
            y[i] = targetVal != null ? targetVal.toString() : "unknown";
        }

        // 获取所有类别
        Set<String> classes = new LinkedHashSet<>(Arrays.asList(y));

        // 计算每个类别的先验概率和特征统计量
        Map<String, Integer> classCounts = new LinkedHashMap<>();
        Map<String, double[]> classMeans = new LinkedHashMap<>();
        Map<String, double[]> classVars = new LinkedHashMap<>();

        for (String cls : classes) {
            List<double[]> classSamples = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (y[i].equals(cls)) {
                    classSamples.add(X[i]);
                }
            }
            classCounts.put(cls, classSamples.size());

            // 计算均值
            double[] mean = new double[d];
            for (double[] sample : classSamples) {
                for (int j = 0; j < d; j++) {
                    mean[j] += sample[j];
                }
            }
            for (int j = 0; j < d; j++) {
                mean[j] /= classSamples.size();
            }
            classMeans.put(cls, mean);

            // 计算方差
            double[] variance = new double[d];
            for (double[] sample : classSamples) {
                for (int j = 0; j < d; j++) {
                    double diff = sample[j] - mean[j];
                    variance[j] += diff * diff;
                }
            }
            for (int j = 0; j < d; j++) {
                variance[j] /= classSamples.size();
                if (variance[j] < 1e-10) variance[j] = 1e-10; // 防止除零
            }
            classVars.put(cls, variance);
        }

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            String bestClass = null;
            double bestLogProb = Double.NEGATIVE_INFINITY;

            for (String cls : classes) {
                double logProb = Math.log((double) classCounts.get(cls) / n);

                double[] mean = classMeans.get(cls);
                double[] variance = classVars.get(cls);

                for (int j = 0; j < d; j++) {
                    double diff = X[i][j] - mean[j];
                    logProb += -0.5 * Math.log(2 * Math.PI * variance[j])
                             - (diff * diff) / (2 * variance[j]);
                }

                if (logProb > bestLogProb) {
                    bestLogProb = logProb;
                    bestClass = cls;
                }
            }

            newRow.put(outputColumn, bestClass);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
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
