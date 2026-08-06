package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 数值离散化处理算子
 * 将连续数值字段离散化为区间标签
 */
@Component
public class NumberParseOperator extends AbstractOperator {

    @Override public String getId() { return "number_parse"; }
    @Override public String getName() { return "数值离散化处理"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("inputField", "输入字段", OperatorSchema.PropertyType.STRING, true)
                .property("outputField", "输出字段", OperatorSchema.PropertyType.STRING, true)
                .property("numBins", "分箱数量", OperatorSchema.PropertyType.NUMBER)
                .property("method", "离散化方法", OperatorSchema.PropertyType.SELECT,
                        Arrays.asList(
                                OperatorSchema.Option.of("等宽", "equal_width"),
                                OperatorSchema.Option.of("等频", "equal_freq")));
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String inputField = ctx.getString("inputField");
        String outputField = ctx.getString("outputField");
        int numBins = ctx.getInt("numBins") != null ? ctx.getInt("numBins") : 5;
        String method = ctx.getString("method");
        if (method == null) method = "equal_width";

        if (inputField == null || inputField.isEmpty()) return input;

        if (outputField == null || outputField.isEmpty()) {
            outputField = inputField + "_discrete";
        }

        List<DataRow> rows = input.collect();
        List<String> columns = new ArrayList<>(input.getColumnNames());
        if (!columns.contains(outputField)) {
            columns.add(outputField);
        }

        if (rows.isEmpty()) return DataSet.of(columns, rows);

        // 收集所有数值
        List<Double> values = new ArrayList<>();
        for (DataRow row : rows) {
            values.add(toDouble(row.get(inputField)));
        }

        // 计算分箱边界
        double[] boundaries;
        if ("equal_freq".equals(method)) {
            boundaries = computeEqualFreqBoundaries(values, numBins);
        } else {
            boundaries = computeEqualWidthBoundaries(values, numBins);
        }

        // 离散化
        List<DataRow> result = new ArrayList<>();
        for (DataRow row : rows) {
            DataRow newRow = row.copy();
            double val = toDouble(row.get(inputField));
            String label = discretize(val, boundaries);
            newRow.put(outputField, label);
            result.add(newRow);
        }

        return DataSet.of(columns, result);
    }

    private double[] computeEqualWidthBoundaries(List<Double> values, int numBins) {
        double min = Collections.min(values);
        double max = Collections.max(values);
        double width = (max - min) / numBins;
        double[] boundaries = new double[numBins - 1];
        for (int i = 0; i < numBins - 1; i++) {
            boundaries[i] = min + width * (i + 1);
        }
        return boundaries;
    }

    private double[] computeEqualFreqBoundaries(List<Double> values, int numBins) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        double[] boundaries = new double[numBins - 1];
        for (int i = 0; i < numBins - 1; i++) {
            int idx = (int) ((double) (i + 1) / numBins * n);
            if (idx >= n) idx = n - 1;
            boundaries[i] = sorted.get(idx);
        }
        return boundaries;
    }

    private String discretize(double value, double[] boundaries) {
        for (int i = 0; i < boundaries.length; i++) {
            if (value <= boundaries[i]) {
                return "bin_" + (i + 1);
            }
        }
        return "bin_" + (boundaries.length + 1);
    }

    private double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0.0; }
    }
}
