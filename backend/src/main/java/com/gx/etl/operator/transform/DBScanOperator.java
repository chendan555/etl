package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * DBSCAN密度聚类算子
 * 从零实现DBSCAN聚类算法
 */
@Component
public class DBScanOperator extends AbstractOperator {

    private static final int UNVISITED = -1;
    private static final int NOISE = -2;

    @Override public String getId() { return "DBScan"; }
    @Override public String getName() { return "DBSCAN密度聚类"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("epsilon", "邻域半径(epsilon)", OperatorSchema.PropertyType.NUMBER, true)
                .property("minPoints", "最小样本数(minPoints)", OperatorSchema.PropertyType.NUMBER, true)
                .property("outputColumn", "输出列名", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        double epsilon = ctx.getDouble("epsilon") != null ? ctx.getDouble("epsilon") : 0.5;
        int minPoints = ctx.getInt("minPoints") != null ? ctx.getInt("minPoints") : 5;
        String outputColumn = ctx.getString("outputColumn");
        if (outputColumn == null || outputColumn.isEmpty()) outputColumn = "cluster";

        if (featureColumnsStr == null || featureColumnsStr.isEmpty()) return input;

        List<String> featureCols = parseColumnList(featureColumnsStr);
        List<DataRow> rows = input.collect();
        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(outputColumn)) columns.add(outputColumn);

        if (rows.isEmpty() || featureCols.isEmpty()) {
            return DataSet.of(columns, rows);
        }

        int n = rows.size();
        int dim = featureCols.size();

        // 提取特征
        double[][] features = new double[n][dim];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            for (int j = 0; j < dim; j++) {
                features[i][j] = parseDouble(row.get(featureCols.get(j)));
            }
        }

        // DBSCAN算法
        int[] labels = new int[n];
        Arrays.fill(labels, UNVISITED);

        int clusterId = 0;

        for (int i = 0; i < n; i++) {
            if (labels[i] != UNVISITED) continue;

            List<Integer> neighbors = rangeQuery(features, i, epsilon);

            if (neighbors.size() < minPoints) {
                labels[i] = NOISE;
                continue;
            }

            // 扩展聚类
            labels[i] = clusterId;
            Queue<Integer> queue = new LinkedList<>(neighbors);
            Set<Integer> inQueue = new HashSet<>(neighbors);

            while (!queue.isEmpty()) {
                int pointIndex = queue.poll();

                if (labels[pointIndex] == NOISE) {
                    labels[pointIndex] = clusterId;
                }
                if (labels[pointIndex] != UNVISITED) continue;

                labels[pointIndex] = clusterId;

                List<Integer> pointNeighbors = rangeQuery(features, pointIndex, epsilon);
                if (pointNeighbors.size() >= minPoints) {
                    for (int neighbor : pointNeighbors) {
                        if (!inQueue.contains(neighbor)) {
                            queue.add(neighbor);
                            inQueue.add(neighbor);
                        }
                    }
                }
            }

            clusterId++;
        }

        // 输出结果
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            if (labels[i] == NOISE) {
                newRow.put(outputColumn, "NOISE");
            } else {
                newRow.put(outputColumn, labels[i]);
            }
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    /**
     * 范围查询：找到epsilon邻域内的所有点
     */
    private List<Integer> rangeQuery(double[][] features, int pointIndex, double epsilon) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < features.length; i++) {
            if (euclideanDistance(features[pointIndex], features[i]) <= epsilon) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
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
