package com.gx.etl.flow.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * Parses jsPlumb flow chart JSON into a DAG structure for execution.
 */
public class DagParser {

    /**
     * Parse the flow chart JSON into a map of FlowChartNodes.
     * @param flowJson the full jsPlumb JSON (nodes, endpoints, edges, head)
     * @return map of nodeId -> FlowChartNode, in topological order
     */
    public static Map<String, FlowChartNode> parse(String flowJson) {
        JSONObject root = JSON.parseObject(flowJson);
        JSONArray nodes = root.getJSONArray("nodes");
        JSONArray edges = root.getJSONArray("edges");

        Map<String, FlowChartNode> nodeMap = new LinkedHashMap<>();

        // Parse nodes
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject nodeObj = nodes.getJSONObject(i);
                String id = nodeObj.getString("id");
                JSONObject data = nodeObj.getJSONObject("data");
                String pluginId = data.getString("pluginId");
                String text = data.getString("text");

                FlowChartNode node = new FlowChartNode(id, pluginId, text);

                // Parse properties.config
                JSONObject props = nodeObj.getJSONObject("properties");
                if (props != null) {
                    String ruleFormStr = props.getString("ruleForm");
                    if (ruleFormStr != null && !ruleFormStr.isEmpty()) {
                        try {
                            Map<String, Object> config = JSON.parseObject(ruleFormStr, Map.class);
                            node.setConfig(config);
                            // 从 ruleForm.table 提取表格数据（多表格连接等算子用）
                            Object tableObj = config.get("table");
                            if (tableObj instanceof List && !((List<?>) tableObj).isEmpty()) {
                                node.setTableData((List<Map<String, Object>>) tableObj);
                            }
                        } catch (Exception e) {
                            node.setConfig(new HashMap<>());
                        }
                    }
                    // Parse tableData (column mapping for TableOutput) - 兼容旧格式
                    if (node.getTableData() == null) {
                        String tableDataStr = props.getString("tableData");
                        if (tableDataStr != null && !tableDataStr.isEmpty()) {
                            try {
                                List<Map<String, Object>> tableData = JSON.parseObject(tableDataStr, List.class);
                                node.setTableData(tableData);
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    }
                }

                nodeMap.put(id, node);
            }
        }

        // Parse edges: "sourceEndpointId&&targetEndpointId"
        // We need to map endpoint IDs back to node IDs
        Map<String, String> endpointToNode = new HashMap<>();
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject nodeObj = nodes.getJSONObject(i);
                String nodeId = nodeObj.getString("id");
                JSONObject points = nodeObj.getJSONObject("points");
                if (points != null) {
                    JSONArray sources = points.getJSONArray("sources");
                    if (sources != null) {
                        for (int j = 0; j < sources.size(); j++) {
                            endpointToNode.put(sources.getString(j), nodeId);
                        }
                    }
                    JSONArray targets = points.getJSONArray("targets");
                    if (targets != null) {
                        for (int j = 0; j < targets.size(); j++) {
                            endpointToNode.put(targets.getString(j), nodeId);
                        }
                    }
                }
            }
        }

        System.out.println("[DagParser] endpointToNode=" + endpointToNode);
        System.out.println("[DagParser] edges=" + edges);

        // Build edges
        if (edges != null) {
            for (int i = 0; i < edges.size(); i++) {
                String edge = edges.getString(i);
                String[] parts = edge.split("&&");
                if (parts.length == 2) {
                    String sourceNodeId = endpointToNode.get(parts[0].trim());
                    String targetNodeId = endpointToNode.get(parts[1].trim());
                    System.out.println("[DagParser] edge: " + parts[0] + " -> " + parts[1] + " => " + sourceNodeId + " -> " + targetNodeId);
                    if (sourceNodeId != null && targetNodeId != null) {
                        FlowChartNode sourceNode = nodeMap.get(sourceNodeId);
                        FlowChartNode targetNode = nodeMap.get(targetNodeId);
                        if (sourceNode != null && targetNode != null) {
                            sourceNode.addDownstream(targetNodeId);
                            targetNode.addUpstream(sourceNodeId);
                        }
                    }
                }
            }
        }

        return nodeMap;
    }

    /**
     * Get topological order of nodes for execution.
     */
    public static List<FlowChartNode> topologicalSort(Map<String, FlowChartNode> nodeMap) {
        List<FlowChartNode> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (FlowChartNode node : nodeMap.values()) {
            if (!visited.contains(node.getId())) {
                visit(node, nodeMap, visited, visiting, result);
            }
        }

        return result;
    }

    private static void visit(FlowChartNode node, Map<String, FlowChartNode> nodeMap,
                               Set<String> visited, Set<String> visiting,
                               List<FlowChartNode> result) {
        if (visiting.contains(node.getId())) {
            throw new RuntimeException("流程图存在循环: " + node.getName());
        }
        if (visited.contains(node.getId())) return;

        visiting.add(node.getId());

        for (String upstreamId : node.getUpstreamIds()) {
            FlowChartNode upstream = nodeMap.get(upstreamId);
            if (upstream != null) {
                visit(upstream, nodeMap, visited, visiting, result);
            }
        }

        visiting.remove(node.getId());
        visited.add(node.getId());
        result.add(node);
    }
}
