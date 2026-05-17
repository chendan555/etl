package com.gx.etl.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.config.DynamicDataSourceManager;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.mapper.DatabaseSourceMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseSourceService extends ServiceImpl<DatabaseSourceMapper, DatabaseSource> {

    public Page<DatabaseSource> listPage(int pageIndex, int pageSize, String databaseName, Long categoriesId) {
        Page<DatabaseSource> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<DatabaseSource> wrapper = new LambdaQueryWrapper<>();
        if (databaseName != null && !databaseName.isEmpty()) {
            wrapper.like(DatabaseSource::getName, databaseName);
        }
        if (categoriesId != null) {
            wrapper.eq(DatabaseSource::getCategoriesId, categoriesId);
        }
        wrapper.orderByDesc(DatabaseSource::getCreateTime);
        return page(page, wrapper);
    }

    public boolean testConnection(DatabaseSource ds) {
        return DynamicDataSourceManager.getInstance().testConnection(
                ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid());
    }

    public List<String> getTables(Long id) throws SQLException {
        return getTables(id, null);
    }

    public List<String> getTables(Long id, String schema) throws SQLException {
        DatabaseSource ds = getById(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        return DynamicDataSourceManager.getInstance().getTables(
                id, ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(), schema);
    }

    public List<Map<String, Object>> getColumns(Long id, String tableName) throws SQLException {
        return getColumns(id, tableName, null);
    }

    public List<Map<String, Object>> getColumns(Long id, String tableName, String schema) throws SQLException {
        DatabaseSource ds = getById(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        return DynamicDataSourceManager.getInstance().getColumns(
                id, ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(), tableName, schema);
    }

    public void removeDataSourceCache(Long id) {
        DynamicDataSourceManager.getInstance().removeDataSource(id);
    }

    public DatabaseSource getByName(String name) {
        LambdaQueryWrapper<DatabaseSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatabaseSource::getName, name);
        return getOne(wrapper);
    }

    public List<String> getSchemas(Long id) throws SQLException {
        DatabaseSource ds = getById(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        return DynamicDataSourceManager.getInstance().getSchemas(
                ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid());
    }

    public List<String> getSchemasFromConnection(DatabaseSource ds) throws SQLException {
        return DynamicDataSourceManager.getInstance().getSchemas(
                ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid());
    }

    public Map<String, Object> getTableData(Long id, String tableName, int pageIndex, int pageSize) throws SQLException {
        return getTableData(id, tableName, pageIndex, pageSize, null);
    }

    public Map<String, Object> getTableData(Long id, String tableName, int pageIndex, int pageSize, String schema) throws SQLException {
        DatabaseSource ds = getById(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        return DynamicDataSourceManager.getInstance().getTableData(
                id, ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(),
                tableName, pageIndex, pageSize, schema);
    }

    public List<Object> getColumnValues(Long id, String tableName, String columnName) throws SQLException {
        DatabaseSource ds = getById(id);
        if (ds == null) throw new RuntimeException("数据源不存在: " + id);
        return DynamicDataSourceManager.getInstance().getColumnValues(
                id, ds.getDbType(), ds.getHost(), ds.getPort(),
                ds.getDatabaseName(), ds.getUsername(), ds.getPassword(), ds.getSid(),
                tableName, columnName);
    }
}
