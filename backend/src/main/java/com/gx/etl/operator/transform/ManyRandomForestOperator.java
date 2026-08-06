package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多路随机森林算子
 * 支持多目标预测的随机森林（多输出随机森林）
 * 原始实现通过Python脚本调用，此版本使用纯Java实现
 */
@Component
public class ManyRandomForestOperator extends AbstractOperator {

    @Override public String getId() { return "ManyRandomForest"; }
    @Override public String getName() { return "多路随机森林"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("featureColumns", "特征列（逗号分隔）", OperatorSchema.PropertyType.STRING, true)
                .property("targetColumns", "目标列（逗号分隔，支持多目标）", OperatorSchema.PropertyType.STRING, true)
                .property("outputPrefix", "输出列前缀", OperatorSchema.PropertyType.STRING)
                .property("numTrees", "树的数量", OperatorSchema.PropertyType.NUMBER)
                .property("maxDepth", "最大深度", OperatorSchema.PropertyType.NUMBER)
                .property("iterations", "迭代模式（1:标准 2:精准 3:深度）", OperatorSchema.PropertyType.SELECT);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String featureColumnsStr = ctx.getString("featureColumns");
        String targetColumnsStr = ctx.getString("targetColumns");
        String outputPrefix = ctx.getString("outputPrefix");
        int numTrees = ctx.getInt("numTrees") != null ? ctx.getInt("numTrees") : 10;
        int maxDepth = ctx.getInt("maxDepth") != null ? ctx.getInt("maxDepth") : 10;

        if (outputPrefix == null || outputPrefix.isEmpty()) outputPrefix = "pred_";
        if (featureColumnsStr == null || targetColumnsStr == null) return input;

        List<String> featureCols = parseColumnList(featureColumnsStr);
        List<String> targetCols = parseColumnList(targetColumnsStr);
        List<DataRow> rows = input.collect();
        List<String> columns = new ArrayList<>(input.getColumnNames());

        // 添加输出列
        for (String targetCol : targetCols) {
            String outCol = outputPrefix + targetCol;
            if (!columns.contains(outCol)) columns.add(outCol);
        }

        if (rows.isEmpty() || featureCols.isEmpty() || targetCols.isEmpty()) {
            return DataSet.of(columns, rows);
        }

        int n = rows.size();
        int d = featureCols.size();

        // 提取特征
        double[][] X = new double[n][d];
        for (int i = 0; i < n; i++) {
            DataRow row = rows.get(i);
            for (int j = 0; j < d; j++) {
                X[i][j] = parseDouble(row.get(featureCols.get(j)));
            }
        }

        // 为每个目标列训练一个随机森林
        Map<String, TreeNode[]> forests = new LinkedHashMap<>();
        Random rand = new Random(42);

        for (String targetCol : targetCols) {
            String[] y = new String[n];
            for (int i = 0; i < n; i++) {
                Object val = rows.get(i).get(targetCol);
                y[i] = val != null ? val.toString() : "unknown";
            }

            TreeNode[] trees = new TreeNode[numTrees];
            for (int t = 0; t < numTrees; t++) {
                int[] sampleIdx = bootstrapSample(n, rand);
                int m = (int) Math.sqrt(d);
                if (m < 1) m = 1;
                int[] featureIdx = randomFeatureSubset(d, m, rand);
                trees[t] = buildTree(X, y, sampleIdx, featureIdx, 0, maxDepth, rand);
            }
            forests.put(targetCol, trees);
        }

        // 预测
        List<DataRow> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            DataRow newRow = rows.get(i).copy();
            for (String targetCol : targetCols) {
                TreeNode[] trees = forests.get(targetCol);
                Map<String, Integer> votes = new LinkedHashMap<>();
                for (TreeNode tree : trees) {
                    String pred = predictTree(tree, X[i]);
                    votes.merge(pred, 1, Integer::sum);
                }
                String bestClass = Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
                newRow.put(outputPrefix + targetCol, bestClass);
            }
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    // ========== 决策树相关方法 ==========

    private static class TreeNode {
        boolean isLeaf;
        String label;
        int featureIndex;
        double threshold;
        TreeNode left, right;
    }

    private int[] bootstrapSample(int n, Random rand) {
        int[] sample = new int[n];
        for (int i = 0; i < n; i++) {
            sample[i] = rand.nextInt(n);
        }
        return sample;
    }

    private int[] randomFeatureSubset(int d, int m, Random rand) {
        List<Integer> allFeatures = new ArrayList<>();
        for (int i = 0; i < d; i++) allFeatures.add(i);
        Collections.shuffle(allFeatures, rand);
        return allFeatures.subList(0, m).stream().mapToInt(Integer::intValue).toArray();
    }

    private TreeNode buildTree(double[][] X, String[] y, int[] sampleIdx,
                                int[] featureIdx, int depth, int maxDepth, Random rand) {
        TreeNode node = new TreeNode();

        Map<String, Integer> classCounts = new LinkedHashMap<>();
        for (int idx : sampleIdx) {
            classCounts.merge(y[idx], 1, Integer::sum);
        }

        if (classCounts.size() == 1 || depth >= maxDepth || featureIdx.length == 0) {
            node.isLeaf = true;
            node.label = Collections.max(classCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return node;
        }

        double bestGini = Double.MAX_VALUE;
        int bestFeature = -1;
        double bestThreshold = 0;

        for (int f : featureIdx) {
            Set<Double> thresholds = new TreeSet<>();
            for (int idx : sampleIdx) {
                thresholds.add(X[idx][f]);
            }
            List<Double> sortedThresholds = new ArrayList<>(thresholds);

            for (int t = 0; t < sortedThresholds.size() - 1; t++) {
                double threshold = (sortedThresholds.get(t) + sortedThresholds.get(t + 1)) / 2.0;
                double gini = computeGini(X, y, sampleIdx, f, threshold);
                if (gini < bestGini) {
                    bestGini = gini;
                    bestFeature = f;
                    bestThreshold = threshold;
                }
            }
        }

        if (bestFeature == -1) {
            node.isLeaf = true;
            node.label = Collections.max(classCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return node;
        }

        node.featureIndex = bestFeature;
        node.threshold = bestThreshold;

        List<Integer> leftIdx = new ArrayList<>();
        List<Integer> rightIdx = new ArrayList<>();
        for (int idx : sampleIdx) {
            if (X[idx][bestFeature] <= bestThreshold) {
                leftIdx.add(idx);
            } else {
                rightIdx.add(idx);
            }
        }

        if (leftIdx.isEmpty() || rightIdx.isEmpty()) {
            node.isLeaf = true;
            node.label = Collections.max(classCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return node;
        }

        int[] leftArr = leftIdx.stream().mapToInt(Integer::intValue).toArray();
        int[] rightArr = rightIdx.stream().mapToInt(Integer::intValue).toArray();

        node.left = buildTree(X, y, leftArr, featureIdx, depth + 1, maxDepth, rand);
        node.right = buildTree(X, y, rightArr, featureIdx, depth + 1, maxDepth, rand);

        return node;
    }

    private double computeGini(double[][] X, String[] y, int[] sampleIdx, int featureIdx, double threshold) {
        int leftCount = 0, rightCount = 0;
        Map<String, Integer> leftClasses = new LinkedHashMap<>();
        Map<String, Integer> rightClasses = new LinkedHashMap<>();

        for (int idx : sampleIdx) {
            if (X[idx][featureIdx] <= threshold) {
                leftCount++;
                leftClasses.merge(y[idx], 1, Integer::sum);
            } else {
                rightCount++;
                rightClasses.merge(y[idx], 1, Integer::sum);
            }
        }

        if (leftCount == 0 || rightCount == 0) return Double.MAX_VALUE;

        double leftGini = 1.0;
        for (int count : leftClasses.values()) {
            double p = (double) count / leftCount;
            leftGini -= p * p;
        }

        double rightGini = 1.0;
        for (int count : rightClasses.values()) {
            double p = (double) count / rightCount;
            rightGini -= p * p;
        }

        return (double) leftCount / sampleIdx.length * leftGini
             + (double) rightCount / sampleIdx.length * rightGini;
    }

    private String predictTree(TreeNode node, double[] x) {
        if (node.isLeaf) return node.label;
        if (x[node.featureIndex] <= node.threshold) {
            return predictTree(node.left, x);
        } else {
            return predictTree(node.right, x);
        }
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
