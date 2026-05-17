package com.gx.etl.flow.service;

import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.service.DatabaseSourceService;
import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DAG-based flow execution engine.
 * Executes operators in topological order, passing DataSet between them.
 */
@Service
public class FlowExecutionEngine {

    private final OperatorRegistry operatorRegistry;
    private final DatabaseSourceService databaseSourceService;

    // 存储最近一次执行结果（transformationId -> 执行结果）
    private final Map<Long, ExecutionResult> lastResults = new ConcurrentHashMap<>();

    public FlowExecutionEngine(OperatorRegistry operatorRegistry, DatabaseSourceService databaseSourceService) {
        this.operatorRegistry = operatorRegistry;
        this.databaseSourceService = databaseSourceService;
    }

    public ExecutionResult getLastResult(Long transformationId) {
        return lastResults.get(transformationId);
    }

    public ExecutionResult getLatestResult() {
        if (lastResults.isEmpty()) return null;
        // 返回最后存入的结果
        ExecutionResult latest = null;
        for (ExecutionResult r : lastResults.values()) {
            latest = r;
        }
        return latest;
    }

    /**
     * Execute a flow chart.
     * @param flowJson the jsPlumb JSON
     * @param nodeId optional: execute up to this node only (for preview)
     * @return the final DataSet (or preview DataSet)
     */
    public ExecutionResult execute(String flowJson, String nodeId) {
        return execute(flowJson, nodeId, null);
    }

    public ExecutionResult executeAndStore(String flowJson, Long transformationId) {
        return executeAndStore(flowJson, transformationId, null);
    }

    public ExecutionResult executeAndStore(String flowJson, Long transformationId, Map<String, Object> params) {
        ExecutionResult result = execute(flowJson, null, params);
        if (transformationId != null) {
            lastResults.put(transformationId, result);
        }
        return result;
    }

    /**
     * Execute a flow chart with optional parameter substitution.
     * @param flowJson the jsPlumb JSON
     * @param nodeId optional: execute up to this node only (for preview)
     * @param params optional: key-value pairs to substitute ${key} in operator configs
     */
    public ExecutionResult execute(String flowJson, String nodeId, Map<String, Object> params) {
        Map<String, FlowChartNode> nodeMap = DagParser.parse(flowJson);
        List<FlowChartNode> executionOrder = DagParser.topologicalSort(nodeMap);

        // Load all referenced data sources
        Map<Long, DatabaseSource> dataSources = loadDataSources();

        // Execute each node in order
        Map<String, DataSet> nodeOutputs = new LinkedHashMap<>();
        long totalRows = 0;
        List<Map<String, Object>> stepMetrics = new ArrayList<>();
        List<Map<String, String>> stepStatusList = new ArrayList<>();
        StringBuilder log = new StringBuilder();

        for (FlowChartNode node : executionOrder) {
            AbstractOperator operator = operatorRegistry.getOperator(node.getPluginId());
            if (operator == null) {
                throw new RuntimeException("未知算子类型: " + node.getPluginId());
            }

            // Build input: merge upstream outputs
            DataSet input;
            if (node.getUpstreamIds().isEmpty()) {
                input = DataSet.empty();
            } else if (node.getUpstreamIds().size() == 1) {
                input = nodeOutputs.getOrDefault(node.getUpstreamIds().get(0), DataSet.empty());
            } else {
                input = mergeDataSets(node, nodeMap, nodeOutputs);
            }

            // Materialize if needed
            if (operator.requiresMaterializedInput() && !input.isEmpty()) {
                input.collect();
            }

            // Build context
            Map<String, Object> config = node.getConfig() != null ? new HashMap<>(node.getConfig()) : new HashMap<>();
            config.putIfAbsent("stepName", node.getName());
            // 参数替换：把 ${key} 替换为实际参数值
            if (params != null && !params.isEmpty()) {
                System.out.println("[execute] 替换前 config=" + config);
                System.out.println("[execute] params=" + params);
                substituteParams(config, params);
                System.out.println("[execute] 替换后 config=" + config);
            }
            // 将 tableData 注入 config，供算子读取字段映射等表格数据
            if (node.getTableData() != null && !node.getTableData().isEmpty()) {
                config.put("_tableData", node.getTableData());
            }
            OperatorContext ctx = new OperatorContext(config, dataSources);

            // Execute
            DataSet output;
            long startTime = System.currentTimeMillis();
            String status = "完成";
            try {
                output = operator.execute(ctx, input);
            } catch (Exception e) {
                status = "失败";
                log.append("算子[").append(node.getName()).append("]执行失败: ").append(e.getMessage()).append("\n");
                // 记录失败步骤的指标
                stepMetrics.add(buildStepMetric(node.getName(), 0, 0, 0, 0, 0, 0, 0, status, System.currentTimeMillis() - startTime, 0));
                Map<String, String> statusItem = new HashMap<>();
                statusItem.put("stepName", node.getName());
                statusItem.put("stepStatus", "fail");
                stepStatusList.add(statusItem);
                throw new RuntimeException("算子[" + node.getName() + "]执行失败: " + e.getMessage(), e);
            }
            long elapsed = System.currentTimeMillis() - startTime;

            nodeOutputs.put(node.getId(), output);

            // Count rows
            long rowCount = output != null ? output.count() : 0;
            totalRows += rowCount;

            // 记录成功步骤的指标
            long speed = elapsed > 0 ? rowCount * 1000 / elapsed : 0;
            stepMetrics.add(buildStepMetric(node.getName(), 0, rowCount, 0, rowCount, 0, 0, 0, status, elapsed, speed));
            Map<String, String> statusItem = new HashMap<>();
            statusItem.put("stepName", node.getName());
            statusItem.put("stepStatus", "success");
            stepStatusList.add(statusItem);

            log.append("步骤[").append(node.getName()).append("] 执行完成，").append(rowCount).append(" 行\n");

            // If preview mode, stop at target node
            if (nodeId != null && nodeId.equals(node.getId())) {
                return new ExecutionResult(true, output, totalRows, null);
            }
        }

        log.append("执行完成，共 ").append(totalRows).append(" 行");

        // Return the output of the last node
        FlowChartNode lastNode = executionOrder.get(executionOrder.size() - 1);
        DataSet finalOutput = nodeOutputs.getOrDefault(lastNode.getId(), DataSet.empty());
        ExecutionResult result = new ExecutionResult(true, finalOutput, totalRows, null, log.toString(), stepMetrics, stepStatusList);
        return result;
    }

    private Map<String, Object> buildStepMetric(String name, long read, long write, long input, long output,
                                                  long update, long reject, long error, String status, long timeMs, long speed) {
        // 返回数组格式：[步骤名, 复制, 读, 写, 输入, 输出, 更新, 拒绝, 错误, 状态, 时间, 速度]
        List<Object> metric = new ArrayList<>();
        metric.add(name);       // 0 - 步骤名称
        metric.add(0);          // 1 - 复制记录行
        metric.add(read);       // 2 - 读
        metric.add(write);      // 3 - 写
        metric.add(input);      // 4 - 输入
        metric.add(output);     // 5 - 输出
        metric.add(update);     // 6 - 更新
        metric.add(reject);     // 7 - 拒绝
        metric.add(error);      // 8 - 错误
        metric.add(status);     // 9 - 状态
        metric.add(timeMs + "ms"); // 10 - 时间
        metric.add(speed);      // 11 - 速度
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < metric.size(); i++) {
            map.put(String.valueOf(i), metric.get(i));
        }
        return map;
    }

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.+?)}");

    /**
     * 递归替换 config 中所有字符串值里的 ${key} 为 params 中对应的值
     */
    @SuppressWarnings("unchecked")
    private void substituteParams(Map<String, Object> config, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof String) {
                String str = (String) val;
                Matcher m = PARAM_PATTERN.matcher(str);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String key = m.group(1);
                    Object replacement = params.get(key);
                    m.appendReplacement(sb, Matcher.quoteReplacement(
                            replacement != null ? replacement.toString() : m.group(0)));
                }
                m.appendTail(sb);
                entry.setValue(sb.toString());
            } else if (val instanceof Map) {
                substituteParams((Map<String, Object>) val, params);
            }
        }
    }

    /**
     * Merge multiple upstream outputs for multi-input operators.
     */
    private DataSet mergeDataSets(FlowChartNode node, Map<String, FlowChartNode> nodeMap,
                                   Map<String, DataSet> nodeOutputs) {
        // Default: concatenate all upstream outputs
        List<String> allColumns = new ArrayList<>();
        List<DataRow> allRows = new ArrayList<>();
        for (String upstreamId : node.getUpstreamIds()) {
            DataSet ds = nodeOutputs.get(upstreamId);
            if (ds != null) {
                if (allColumns.isEmpty()) allColumns.addAll(ds.getColumnNames());
                for (DataRow row : ds) {
                    allRows.add(row);
                }
            }
        }
        return DataSet.of(allColumns, allRows);
    }

    private Map<Long, DatabaseSource> loadDataSources() {
        List<DatabaseSource> all = databaseSourceService.list();
        Map<Long, DatabaseSource> map = new ConcurrentHashMap<>();
        for (DatabaseSource ds : all) {
            map.put(ds.getId(), ds);
        }
        return map;
    }

    public static class ExecutionResult {
        private final boolean success;
        private final DataSet dataSet;
        private final long totalRows;
        private final String error;
        private final String log;
        private final List<Map<String, Object>> stepMetrics;
        private final List<Map<String, String>> stepStatusList;

        public ExecutionResult(boolean success, DataSet dataSet, long totalRows, String error) {
            this(success, dataSet, totalRows, error, "", new ArrayList<>(), new ArrayList<>());
        }

        public ExecutionResult(boolean success, DataSet dataSet, long totalRows, String error,
                               String log, List<Map<String, Object>> stepMetrics, List<Map<String, String>> stepStatusList) {
            this.success = success;
            this.dataSet = dataSet;
            this.totalRows = totalRows;
            this.error = error;
            this.log = log;
            this.stepMetrics = stepMetrics;
            this.stepStatusList = stepStatusList;
        }

        public boolean isSuccess() { return success; }
        public DataSet getDataSet() { return dataSet; }
        public long getTotalRows() { return totalRows; }
        public String getError() { return error; }
        public String getLog() { return log; }
        public List<Map<String, Object>> getStepMetrics() { return stepMetrics; }
        public List<Map<String, String>> getStepStatusList() { return stepStatusList; }
    }
}
