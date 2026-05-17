package com.gx.etl.flow.service;

import java.util.*;

/**
 * Represents a node in the flow chart, parsed from jsPlumb JSON.
 */
public class FlowChartNode {

    private String id;
    private String pluginId;
    private String name;
    private Map<String, Object> config;
    private List<Map<String, Object>> tableData;
    private List<String> upstreamIds = new ArrayList<>();
    private List<String> downstreamIds = new ArrayList<>();

    public FlowChartNode(String id, String pluginId, String name) {
        this.id = id;
        this.pluginId = pluginId;
        this.name = name;
    }

    public String getId() { return id; }
    public String getPluginId() { return pluginId; }
    public String getName() { return name; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public List<Map<String, Object>> getTableData() { return tableData; }
    public void setTableData(List<Map<String, Object>> tableData) { this.tableData = tableData; }
    public List<String> getUpstreamIds() { return upstreamIds; }
    public List<String> getDownstreamIds() { return downstreamIds; }

    public void addUpstream(String nodeId) {
        if (!upstreamIds.contains(nodeId)) upstreamIds.add(nodeId);
    }

    public void addDownstream(String nodeId) {
        if (!downstreamIds.contains(nodeId)) downstreamIds.add(nodeId);
    }
}
