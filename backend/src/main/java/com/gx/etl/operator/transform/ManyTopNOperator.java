package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多路TopN算子
 * 支持按多个目标字段分别进行TopN排序，每路独立输出。
 * 原始实现依赖 Python 脚本，此版本为纯 Java 实现。
 *
 * 与 TopNJC 的区别:
 *   - TopNJC: 单目标字段，按分组取TopN
 *   - ManyTopN: 多目标字段，每路独立排序取TopN，结果合并输出
 *
 * 参数:
 *   groupFields  - 分组字段（逗号分隔）
 *   targetFields - 目标排序字段（逗号分隔，每路一个）
 *   topN         - 每路取前N条
 *   sortOrder    - 排序方式 asc/desc
 */
@Component
public class ManyTopNOperator extends AbstractOperator {

    @Override public String getId() { return "ManyTopN"; }
    @Override public String getName() { return "多路TopN"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("groupFields", "分组字段(逗号分隔)", OperatorSchema.PropertyType.STRING)
                .property("targetFields", "目标排序字段(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("topN", "每路TopN数量", OperatorSchema.PropertyType.NUMBER, true)
                .property("sortOrder", "排序方式", OperatorSchema.PropertyType.SELECT,
                        Arrays.asList(
                                OperatorSchema.Option.of("降序", "desc"),
                                OperatorSchema.Option.of("升序", "asc")
                        ));
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String groupFieldsStr = ctx.getString("groupFields");
        String targetFieldsStr = ctx.getString("targetFields");
        Integer topN = ctx.getInt("topN");
        String sortOrder = ctx.getString("sortOrder");

        if (targetFieldsStr == null || targetFieldsStr.isEmpty() || topN == null || topN <= 0) {
            return input;
        }

        boolean asc = "asc".equalsIgnoreCase(sortOrder);

        // 解析分组字段
        List<String> groupFieldList = new ArrayList<>();
        if (groupFieldsStr != null && !groupFieldsStr.isEmpty()) {
            for (String f : groupFieldsStr.split(",")) {
                String trimmed = f.trim();
                if (!trimmed.isEmpty()) groupFieldList.add(trimmed);
            }
        }

        // 解析目标字段（多路）
        List<String> targetFieldList = new ArrayList<>();
        for (String f : targetFieldsStr.split(",")) {
            String trimmed = f.trim();
            if (!trimmed.isEmpty()) targetFieldList.add(trimmed);
        }

        // 收集所有行
        List<String> columns = new ArrayList<>(input.getColumnNames());
        List<DataRow> allRows = new ArrayList<>();
        for (DataRow row : input) {
            allRows.add(row);
        }

        // 分组
        Map<String, List<DataRow>> groups;
        if (groupFieldList.isEmpty()) {
            groups = new LinkedHashMap<>();
            groups.put("__all__", allRows);
        } else {
            groups = groupBy(allRows, groupFieldList);
        }

        // 对每路目标字段分别做TopN，合并结果
        Set<String> seenKeys = new HashSet<>();
        List<DataRow> result = new ArrayList<>();

        for (String targetField : targetFieldList) {
            for (List<DataRow> groupRows : groups.values()) {
                // 拷贝后排序（不修改原始数据）
                List<DataRow> sorted = new ArrayList<>(groupRows);
                sorted.sort((a, b) -> {
                    int cmp = compareValues(a.get(targetField), b.get(targetField));
                    return asc ? cmp : -cmp;
                });

                int limit = Math.min(topN, sorted.size());
                for (int i = 0; i < limit; i++) {
                    DataRow row = sorted.get(i);
                    // 用行内容的hashCode去重，避免同一路重复输出
                    String key = targetField + "|" + rowToString(row);
                    if (seenKeys.add(key)) {
                        result.add(row);
                    }
                }
            }
        }

        return DataSet.of(columns, result);
    }

    private Map<String, List<DataRow>> groupBy(List<DataRow> rows, List<String> groupFields) {
        Map<String, List<DataRow>> groups = new LinkedHashMap<>();
        for (DataRow row : rows) {
            StringBuilder key = new StringBuilder();
            for (String f : groupFields) {
                Object v = row.get(f);
                key.append(v != null ? v.toString() : "").append("\0");
            }
            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    private int compareValues(Object a, Object b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        return a.toString().compareTo(b.toString());
    }

    private String rowToString(DataRow row) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : row.getFields().entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append(";");
        }
        return sb.toString();
    }
}
