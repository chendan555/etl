package com.gx.etl.operator.core;

import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.config.DynamicDataSourceManager;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Execution context passed to each operator during execution.
 * Contains the operator's configuration and access to data sources.
 */
public class OperatorContext {

    private final Map<String, Object> config;
    private final Map<Long, DatabaseSource> dataSources;
    private Map<String, DataSet> upstreamOutputs;

    public OperatorContext(Map<String, Object> config, Map<Long, DatabaseSource> dataSources) {
        this.config = config;
        this.dataSources = dataSources;
    }

    public void setUpstreamOutputs(Map<String, DataSet> upstreamOutputs) {
        this.upstreamOutputs = upstreamOutputs;
    }

    public Map<String, DataSet> getUpstreamOutputs() {
        return upstreamOutputs;
    }

    public DataSet getUpstreamOutput(String name) {
        return upstreamOutputs != null ? upstreamOutputs.get(name) : null;
    }

    public Object get(String key) {
        return config.get(key);
    }

    public String getString(String key) {
        Object v = config.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = config.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(v.toString());
    }

    public Long getLong(String key) {
        Object v = config.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(v.toString());
    }

    public Boolean getBoolean(String key) {
        Object v = config.get(key);
        if (v == null) return null;
        if (v instanceof Boolean) return (Boolean) v;
        return Boolean.parseBoolean(v.toString());
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public DataSource getDataSource(Long id) {
        DatabaseSource ds = dataSources.get(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        // MySQL: databaseName 可能为空，尝试从 schemaFilter 获取库名
        String schema = null;
        if ("MYSQL".equalsIgnoreCase(ds.getDbType())
                && (ds.getDatabaseName() == null || ds.getDatabaseName().isEmpty())
                && ds.getSchemaFilter() != null && !ds.getSchemaFilter().isEmpty()) {
            schema = ds.getSchemaFilter().split(",")[0].trim();
        }
        return DynamicDataSourceManager.getInstance().getDataSource(
                ds.getId(), ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(), schema);
    }

    public DatabaseSource getDatabaseSource(Long id) {
        return dataSources.get(id);
    }

    public String getDataSourceDbType(Long id) {
        DatabaseSource ds = dataSources.get(id);
        return ds != null ? ds.getDbType() : null;
    }

    /**
     * 根据数据源名称查找ID，兼容前端传名称而非ID的情况
     */
    public Long findDataSourceIdByName(String name) {
        for (Map.Entry<Long, DatabaseSource> entry : dataSources.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("数据源不存在: " + name);
    }
}
