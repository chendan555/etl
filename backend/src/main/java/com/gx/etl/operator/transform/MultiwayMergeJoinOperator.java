package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多表格连接算子 - 将多个上游数据流按指定字段做 JOIN
 * 支持 INNER 和 FULL OUTER 两种连接方式
 */
@Component
public class MultiwayMergeJoinOperator extends AbstractOperator {

    @Override
    public String getId() { return "MultiwayMergeJoin"; }

    @Override
    public String getName() { return "多表格连接"; }

    @Override
    public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresSeparateInputs() {
        return true;
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        Map<String, DataSet> upstreamOutputs = ctx.getUpstreamOutputs();
        System.out.println("[MultiwayMergeJoin] upstreamOutputs=" + (upstreamOutputs != null ? upstreamOutputs.keySet() : "null"));
        if (upstreamOutputs == null || upstreamOutputs.size() < 2) {
            System.out.println("[MultiwayMergeJoin] 上游数量不足，跳过");
            return input;
        }

        String joinType = ctx.getString("joinType");
        if (joinType == null) joinType = "INNER";

        // 从 _tableData 读取节点和字段配置
        List<Map<String, Object>> tableData = getTableData(ctx);
        System.out.println("[MultiwayMergeJoin] tableData=" + tableData);
        if (tableData == null || tableData.isEmpty()) {
            System.out.println("[MultiwayMergeJoin] tableData为空，跳过");
            return input;
        }

        // 按顺序收集各上游的 DataSet 和对应 keyFields
        List<DataSet> dataSets = new ArrayList<>();
        List<List<String>> allKeyFields = new ArrayList<>();
        List<String> stepNames = new ArrayList<>();

        for (Map<String, Object> row : tableData) {
            String stepName = getStringValue(row, "inputSteps");
            if (stepName == null || stepName.isEmpty()) continue;

            DataSet ds = upstreamOutputs.get(stepName);
            if (ds == null) {
                System.out.println("[MultiwayMergeJoin] 上游[" + stepName + "]不存在，跳过");
                continue;
            }

            List<String> keyFields = parseKeyFields(row);
            if (keyFields.isEmpty()) {
                System.out.println("[MultiwayMergeJoin] 步骤[" + stepName + "]无key字段，跳过");
                continue;
            }

            System.out.println("[MultiwayMergeJoin] 步骤[" + stepName + "] keyFields=" + keyFields + " rows=" + ds.count());
            stepNames.add(stepName);
            dataSets.add(ds);
            allKeyFields.add(keyFields);
        }

        if (dataSets.isEmpty()) {
            System.out.println("[MultiwayMergeJoin] 无有效数据集，跳过");
            return input;
        }

        // 逐个 JOIN
        DataSet result = dataSets.get(0);
        List<String> resultKeys = allKeyFields.get(0);
        for (int i = 1; i < dataSets.size(); i++) {
            result = joinTwo(result, resultKeys, dataSets.get(i), allKeyFields.get(i), joinType);
        }

        System.out.println("[MultiwayMergeJoin] 结果行数=" + result.count());
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getTableData(OperatorContext ctx) {
        Object td = ctx.get("_tableData");
        if (td instanceof List) {
            return (List<Map<String, Object>>) td;
        }
        return null;
    }

    private List<String> parseKeyFields(Map<String, Object> row) {
        List<String> keys = new ArrayList<>();
        Object kf = row.get("keyFields");
        if (kf == null) return keys;
        if (kf instanceof List) {
            for (Object item : (List<?>) kf) {
                keys.add(item.toString());
            }
        } else {
            String str = kf.toString().trim();
            if (!str.isEmpty()) {
                for (String s : str.split(",")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        keys.add(trimmed);
                    }
                }
            }
        }
        return keys;
    }

    private String getStringValue(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString().trim() : null;
    }

    private DataSet joinTwo(DataSet left, List<String> leftKeys,
                            DataSet right, List<String> rightKeys,
                            String joinType) {
        List<DataRow> leftRows = left.collect();
        List<DataRow> rightRows = right.collect();

        // 合并列名：重名字段加 _1, _2 后缀
        List<String> leftCols = left.getColumnNames();
        List<String> rightCols = right.getColumnNames();
        List<String> mergedCols = new ArrayList<>(leftCols);

        // 统计左表列名出现次数，用于生成不重复后缀
        Map<String, Integer> leftColCount = new LinkedHashMap<>();
        for (String c : leftCols) {
            leftColCount.merge(c, 1, Integer::sum);
        }

        // 右表列名重命名映射
        Map<String, String> rightRename = new LinkedHashMap<>();
        Map<String, Integer> rightColSeen = new LinkedHashMap<>();
        for (String rc : rightCols) {
            if (leftCols.contains(rc)) {
                int seq = rightColSeen.merge(rc, 1, Integer::sum);
                String newName = rc + "_" + seq;
                // 避免与左表或已生成的名称冲突
                while (leftCols.contains(newName) || rightRename.containsValue(newName)) {
                    seq++;
                    newName = rc + "_" + seq;
                }
                rightRename.put(rc, newName);
                mergedCols.add(newName);
            } else {
                rightColSeen.merge(rc, 1, Integer::sum);
                mergedCols.add(rc);
            }
        }

        // 如果有重命名，同步更新右表行数据和 keyFields
        if (!rightRename.isEmpty()) {
            for (DataRow rRow : rightRows) {
                for (Map.Entry<String, String> entry : rightRename.entrySet()) {
                    if (rRow.hasField(entry.getKey())) {
                        rRow.put(entry.getValue(), rRow.get(entry.getKey()));
                        rRow.remove(entry.getKey());
                    }
                }
            }
            // 更新右表 keyFields 中被重命名的字段
            List<String> newRightKeys = new ArrayList<>();
            for (String rk : rightKeys) {
                newRightKeys.add(rightRename.getOrDefault(rk, rk));
            }
            rightKeys = newRightKeys;
        }

        // 为右表行建立 key 索引
        Map<String, List<DataRow>> rightIndex = new LinkedHashMap<>();
        for (DataRow rRow : rightRows) {
            String key = buildKey(rRow, rightKeys);
            rightIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(rRow);
        }

        // 重命名后的右表列名列表
        List<String> actualRightCols = new ArrayList<>();
        for (String rc : rightCols) {
            actualRightCols.add(rightRename.getOrDefault(rc, rc));
        }

        // 用于 FULL OUTER 跟踪已匹配的右表 key
        Set<String> matchedRightKeys = new LinkedHashSet<>();
        List<DataRow> result = new ArrayList<>();

        // 左表行匹配右表
        for (DataRow lRow : leftRows) {
            String key = buildKey(lRow, leftKeys);
            List<DataRow> matched = rightIndex.get(key);
            if (matched != null && !matched.isEmpty()) {
                matchedRightKeys.add(key);
                for (DataRow rRow : matched) {
                    result.add(mergeRow(lRow, leftCols, rRow, actualRightCols, mergedCols));
                }
            } else if ("FULL OUTER".equalsIgnoreCase(joinType)) {
                result.add(mergeRow(lRow, leftCols, null, actualRightCols, mergedCols));
            }
        }

        // FULL OUTER：补充右表未匹配的行
        if ("FULL OUTER".equalsIgnoreCase(joinType)) {
            for (Map.Entry<String, List<DataRow>> entry : rightIndex.entrySet()) {
                if (!matchedRightKeys.contains(entry.getKey())) {
                    for (DataRow rRow : entry.getValue()) {
                        result.add(mergeRow(null, leftCols, rRow, actualRightCols, mergedCols));
                    }
                }
            }
        }

        return DataSet.of(mergedCols, result);
    }

    private String buildKey(DataRow row, List<String> keys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) sb.append(" ");
            Object v = row.get(keys.get(i));
            sb.append(v != null ? v.toString() : "");
        }
        return sb.toString();
    }

    private DataRow mergeRow(DataRow left, List<String> leftCols,
                             DataRow right, List<String> rightCols,
                             List<String> mergedCols) {
        DataRow merged = new DataRow();
        for (String col : mergedCols) {
            if (left != null && leftCols.contains(col) && left.hasField(col)) {
                merged.put(col, left.get(col));
            } else if (right != null && rightCols.contains(col) && right.hasField(col)) {
                merged.put(col, right.get(col));
            } else {
                merged.put(col, null);
            }
        }
        return merged;
    }
}
