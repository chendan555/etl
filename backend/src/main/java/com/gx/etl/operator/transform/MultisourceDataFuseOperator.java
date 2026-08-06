package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多源数据融合算子
 * 将多个输入数据源按关联键进行合并（INNER / FULL OUTER）。
 * 参考: gx-plugin-multisourcedatafuse MultisourceDataFuse / MultisourceDataFuse2 / MultisourceDataFuse3
 *
 * 三个版本（Fuse/Fuse2/Fuse3）逻辑完全一致，区别仅在于支持的输入源数量不同。
 * 本算子统一实现，支持任意数量的输入源。
 *
 * 参数:
 *   joinType      - 连接方式: INNER / FULL OUTER
 *   keyFields     - 关联键字段（逗号分隔，所有输入共用同一组键）
 *   upstreamNames - 上游数据源名称列表（逗号分隔，对应 ctx.getUpstreamOutputs 的 key）
 */
@Component
public class MultisourceDataFuseOperator extends AbstractOperator {

    @Override public String getId() { return "MultisourceDataFuse"; }
    @Override public String getName() { return "多源数据融合"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("joinType", "连接方式", OperatorSchema.PropertyType.SELECT,
                        Arrays.asList(
                                OperatorSchema.Option.of("INNER", "INNER"),
                                OperatorSchema.Option.of("FULL OUTER", "FULL OUTER")
                        ))
                .property("keyFields", "关联键字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("upstreamNames", "上游数据源名称(逗号分隔)", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String joinType = ctx.getString("joinType");
        String keyFieldsStr = ctx.getString("keyFields");
        String upstreamNamesStr = ctx.getString("upstreamNames");

        if (joinType == null || joinType.isEmpty()) joinType = "INNER";
        if (keyFieldsStr == null || keyFieldsStr.isEmpty()) return input;

        String[] keyFields = keyFieldsStr.split(",");
        for (int i = 0; i < keyFields.length; i++) {
            keyFields[i] = keyFields[i].trim();
        }

        boolean isFullOuter = "FULL OUTER".equalsIgnoreCase(joinType);

        // 收集所有输入源
        List<DataSet> inputs = new ArrayList<>();
        inputs.add(input); // 主输入

        if (upstreamNamesStr != null && !upstreamNamesStr.isEmpty()) {
            Map<String, DataSet> upstreamOutputs = ctx.getUpstreamOutputs();
            if (upstreamOutputs != null) {
                for (String name : upstreamNamesStr.split(",")) {
                    String trimmed = name.trim();
                    if (!trimmed.isEmpty()) {
                        DataSet ds = upstreamOutputs.get(trimmed);
                        if (ds != null) {
                            inputs.add(ds);
                        }
                    }
                }
            }
        }

        if (inputs.size() < 2) {
            return input; // 只有一个输入，无需融合
        }

        // 将所有输入物化到内存
        List<List<DataRow>> materializedInputs = new ArrayList<>();
        for (DataSet ds : inputs) {
            materializedInputs.add(ds.collect());
        }

        // 合并所有输入的列名
        LinkedHashSet<String> allColumns = new LinkedHashSet<>();
        for (DataSet ds : inputs) {
            allColumns.addAll(ds.getColumnNames());
        }
        List<String> columns = new ArrayList<>(allColumns);

        // 按关联键分组每个输入
        List<Map<String, List<DataRow>>> groupedInputs = new ArrayList<>();
        for (List<DataRow> rows : materializedInputs) {
            groupedInputs.add(groupByKey(rows, keyFields));
        }

        // 收集所有键（用于 FULL OUTER）
        LinkedHashSet<String> allKeys = new LinkedHashSet<>();
        for (Map<String, List<DataRow>> grouped : groupedInputs) {
            allKeys.addAll(grouped.keySet());
        }

        // 执行合并
        List<DataRow> result = new ArrayList<>();

        if (isFullOuter) {
            // FULL OUTER: 遍历所有键，每个输入匹配或填空
            for (String key : allKeys) {
                // 计算每个输入在该键下的行数
                int maxRows = 0;
                for (Map<String, List<DataRow>> grouped : groupedInputs) {
                    List<DataRow> rows = grouped.getOrDefault(key, Collections.emptyList());
                    maxRows = Math.max(maxRows, rows.size());
                }
                if (maxRows == 0) maxRows = 1; // 至少输出一行

                for (int i = 0; i < maxRows; i++) {
                    DataRow merged = new DataRow();
                    boolean anyMatch = false;
                    for (Map<String, List<DataRow>> grouped : groupedInputs) {
                        List<DataRow> rows = grouped.getOrDefault(key, Collections.emptyList());
                        if (i < rows.size()) {
                            mergeRowInto(merged, rows.get(i));
                            anyMatch = true;
                        }
                        // 无匹配的输入填充 null（merged 中缺失的列自动为 null）
                    }
                    if (anyMatch) {
                        result.add(merged);
                    }
                }
            }
        } else {
            // INNER: 只输出所有输入都有匹配的键
            for (String key : groupedInputs.get(0).keySet()) {
                boolean allMatch = true;
                for (int i = 1; i < groupedInputs.size(); i++) {
                    if (!groupedInputs.get(i).containsKey(key)) {
                        allMatch = false;
                        break;
                    }
                }
                if (!allMatch) continue;

                // 计算笛卡尔积
                List<List<DataRow>> perInput = new ArrayList<>();
                for (Map<String, List<DataRow>> grouped : groupedInputs) {
                    perInput.add(grouped.get(key));
                }
                cartesianProduct(perInput, 0, new DataRow(), result);
            }
        }

        return DataSet.of(columns, result);
    }

    private Map<String, List<DataRow>> groupByKey(List<DataRow> rows, String[] keyFields) {
        Map<String, List<DataRow>> groups = new LinkedHashMap<>();
        for (DataRow row : rows) {
            StringBuilder key = new StringBuilder();
            for (String kf : keyFields) {
                Object v = row.get(kf);
                key.append(v != null ? v.toString() : "").append("\0");
            }
            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    private void mergeRowInto(DataRow target, DataRow source) {
        for (Map.Entry<String, Object> entry : source.getFields().entrySet()) {
            if (target.get(entry.getKey()) == null) {
                target.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void cartesianProduct(List<List<DataRow>> lists, int index, DataRow current, List<DataRow> result) {
        if (index == lists.size()) {
            result.add(current.copy());
            return;
        }
        for (DataRow row : lists.get(index)) {
            DataRow next = current.copy();
            mergeRowInto(next, row);
            cartesianProduct(lists, index + 1, next, result);
        }
    }
}
