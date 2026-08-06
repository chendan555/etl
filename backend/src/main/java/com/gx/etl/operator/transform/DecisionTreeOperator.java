package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 决策树算子
 * 实现ID3决策树分类器（基于信息增益）
 */
@Component
public class DecisionTreeOperator extends AbstractOperator {

    @Override public String getId() { return "DecisionTree"; }
    @Override public String getName() { return "决策树"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("targetColumn", "目标列", OperatorSchema.PropertyType.STRING, true)
                .property("outputColumn", "预测输出列", OperatorSchema.PropertyType.STRING, true)
                .property("maxDepth", "最大深度", OperatorSchema.PropertyType.NUMBER);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        String targetColumn = ctx.getString("targetColumn");
        String outputColumn = ctx.getString("outputColumn");
        int maxDepth = ctx.getInt("maxDepth") != null ? ctx.getInt("maxDepth") : 10;

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

        // 构建决策树
        boolean[] usedFeatures = new boolean[d];
        TreeNode tree = buildTree(X, y, usedFeatures, 0, maxDepth);

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            String prediction = predictTree(tree, X[i]);
            newRow.put(outputColumn, prediction);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private static class TreeNode {
        boolean isLeaf;
        String label;
        int featureIndex;
        double threshold;
        TreeNode left, right;
    }

    private TreeNode buildTree(double[][] X, String[] y, boolean[] usedFeatures, int depth, int maxDepth) {
        TreeNode node = new TreeNode();

        // 统计类别
        Map<String, Integer> classCounts = new LinkedHashMap<>();
        for (String label : y) {
            classCounts.merge(label, 1, Integer::sum);
        }

        // 终止条件
        if (classCounts.size() == 1 || depth >= maxDepth) {
            node.isLeaf = true;
            node.label = Collections.max(classCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return node;
        }

        // 计算当前熵
        double currentEntropy = computeEntropy(y);

        // 找最佳分裂特征和阈值
        double bestGain = 0;
        int bestFeature = -1;
        double bestThreshold = 0;

        int d = X[0].length;
        for (int f = 0; f < d; f++) {
            if (usedFeatures[f]) continue;

            Set<Double> thresholds = new TreeSet<>();
            for (double[] row : X) {
                thresholds.add(row[f]);
            }
            List<Double> sortedThresholds = new ArrayList<>(thresholds);

            for (int t = 0; t < sortedThresholds.size() - 1; t++) {
                double threshold = (sortedThresholds.get(t) + sortedThresholds.get(t + 1)) / 2.0;

                List<String> leftY = new ArrayList<>();
                List<String> rightY = new ArrayList<>();
                for (int i = 0; i < X.length; i++) {
                    if (X[i][f] <= threshold) {
                        leftY.add(y[i]);
                    } else {
                        rightY.add(y[i]);
                    }
                }

                if (leftY.isEmpty() || rightY.isEmpty()) continue;

                double leftEntropy = computeEntropy(leftY.toArray(new String[0]));
                double rightEntropy = computeEntropy(rightY.toArray(new String[0]));

                double weightedEntropy = (double) leftY.size() / y.length * leftEntropy
                                       + (double) rightY.size() / y.length * rightEntropy;

                double gain = currentEntropy - weightedEntropy;
                if (gain > bestGain) {
                    bestGain = gain;
                    bestFeature = f;
                    bestThreshold = threshold;
                }
            }
        }

        if (bestFeature == -1 || bestGain < 1e-10) {
            node.isLeaf = true;
            node.label = Collections.max(classCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return node;
        }

        node.featureIndex = bestFeature;
        node.threshold = bestThreshold;

        // 分裂数据
        List<Integer> leftIdx = new ArrayList<>();
        List<Integer> rightIdx = new ArrayList<>();
        for (int i = 0; i < X.length; i++) {
            if (X[i][bestFeature] <= bestThreshold) {
                leftIdx.add(i);
            } else {
                rightIdx.add(i);
            }
        }

        double[][] leftX = extractRows(X, leftIdx);
        double[][] rightX = extractRows(X, rightIdx);
        String[] leftY = extractLabels(y, leftIdx);
        String[] rightY = extractLabels(y, rightIdx);

        boolean[] leftUsed = usedFeatures.clone();
        boolean[] rightUsed = usedFeatures.clone();
        leftUsed[bestFeature] = true;
        rightUsed[bestFeature] = true;

        node.left = buildTree(leftX, leftY, leftUsed, depth + 1, maxDepth);
        node.right = buildTree(rightX, rightY, rightUsed, depth + 1, maxDepth);

        return node;
    }

    private double computeEntropy(String[] labels) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String label : labels) {
            counts.merge(label, 1, Integer::sum);
        }
        double entropy = 0;
        int n = labels.length;
        for (int count : counts.values()) {
            double p = (double) count / n;
            if (p > 0) {
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }
        return entropy;
    }

    private String predictTree(TreeNode node, double[] x) {
        if (node.isLeaf) return node.label;
        if (x[node.featureIndex] <= node.threshold) {
            return predictTree(node.left, x);
        } else {
            return predictTree(node.right, x);
        }
    }

    private double[][] extractRows(double[][] X, List<Integer> indices) {
        double[][] result = new double[indices.size()][];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = X[indices.get(i)];
        }
        return result;
    }

    private String[] extractLabels(String[] y, List<Integer> indices) {
        String[] result = new String[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = y[indices.get(i)];
        }
        return result;
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
