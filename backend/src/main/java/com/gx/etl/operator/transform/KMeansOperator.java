package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * K均值聚类算子
 * 从零实现K-Means聚类算法，无需外部ML库
 */
@Component
public class KMeansOperator extends AbstractOperator {

    @Override public String getId() { return "KMeans"; }
    @Override public String getName() { return "K均值聚类"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("k", "聚类簇数", OperatorSchema.PropertyType.NUMBER, true)
                .property("maxIterations", "最大迭代次数", OperatorSchema.PropertyType.NUMBER)
                .property("outputColumn", "输出列名", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        int k = ctx.getInt("k") != null ? ctx.getInt("k") : 3;
        int maxIter = ctx.getInt("maxIterations") != null ? ctx.getInt("maxIterations") : 100;
        String outputColumn = ctx.getString("outputColumn");
        if (outputColumn == null || outputColumn.isEmpty()) outputColumn = "cluster";

        if (featureColumnsStr == null || featureColumnsStr.isEmpty()) return input;

        List<String> featureCols = Arrays.asList(featureColumnsStr.split(","));
        for (int i = 0; i < featureCols.size(); i++) {
            featureCols.set(i, featureCols.get(i).trim());
        }

        List<DataRow> rows = input.collect();
        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(outputColumn)) columns.add(outputColumn);

        if (rows.isEmpty() || k <= 0) {
            return DataSet.of(columns, rows);
        }

        // 提取特征矩阵
        double[][] features = new double[rows.size()][featureCols.size()];
        for (int i = 0; i < rows.size(); i++) {
            DataRow row = rows.get(i);
            for (int j = 0; j < featureCols.size(); j++) {
                Object val = row.get(featureCols.get(j));
                features[i][j] = val != null ? parseDouble(val) : 0.0;
            }
        }

        // K-Means 聚类
        int n = features.length;
        int dim = features[0].length;
        if (k > n) k = n;

        // 初始化质心：随机选择k个样本
        double[][] centroids = new double[k][dim];
        Random rand = new Random(42);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);
        Collections.shuffle(indices, rand);
        for (int i = 0; i < k; i++) {
            System.arraycopy(features[indices.get(i)], 0, centroids[i], 0, dim);
        }

        int[] labels = new int[n];

        for (int iter = 0; iter < maxIter; iter++) {
            boolean changed = false;

            // 分配每个点到最近的质心
            for (int i = 0; i < n; i++) {
                int nearest = 0;
                double minDist = Double.MAX_VALUE;
                for (int c = 0; c < k; c++) {
                    double dist = euclideanDistance(features[i], centroids[c]);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = c;
                    }
                }
                if (labels[i] != nearest) {
                    labels[i] = nearest;
                    changed = true;
                }
            }

            if (!changed) break;

            // 更新质心
            double[][] newCentroids = new double[k][dim];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                counts[c]++;
                for (int d = 0; d < dim; d++) {
                    newCentroids[c][d] += features[i][d];
                }
            }
            for (int c = 0; c < k; c++) {
                if (counts[c] > 0) {
                    for (int d = 0; d < dim; d++) {
                        newCentroids[c][d] /= counts[c];
                    }
                } else {
                    // 空簇：保留旧质心
                    System.arraycopy(centroids[c], 0, newCentroids[c], 0, dim);
                }
            }
            centroids = newCentroids;
        }

        // 输出结果
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            newRow.put(outputColumn, labels[i]);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
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
