package com.gx.etl.datasource.controller;

import com.gx.etl.common.Result;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.service.DatabaseSourceService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/database")
public class DatabaseApiController {

    private final DatabaseSourceService databaseSourceService;

    public DatabaseApiController(DatabaseSourceService databaseSourceService) {
        this.databaseSourceService = databaseSourceService;
    }

    /**
     * 获取数据库 Schema 列表
     */
    @PostMapping("/getSchema")
    public Result<?> getSchema(@RequestParam(required = false) String connection) {
        try {
            if (connection == null || connection.isEmpty()) {
                return Result.ok(new ArrayList<>());
            }
            DatabaseSource ds = databaseSourceService.getByName(connection);
            if (ds == null) {
                return Result.ok(new ArrayList<>());
            }
            List<String> schemas = databaseSourceService.getSchemas(ds.getId());
            // 前端期望对象数组，每个对象需要 text 属性
            List<Map<String, Object>> result = new ArrayList<>();
            for (String schema : schemas) {
                Map<String, Object> item = new HashMap<>();
                item.put("text", schema);
                result.add(item);
            }
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取数据库所有 Schema 列表（根据连接参数）
     */
    @PostMapping("/getSchemaAll")
    public Result<?> getSchemaAll(@RequestBody Map<String, Object> params) {
        try {
            DatabaseSource ds = mapToDatabaseSource(params);
            List<String> schemas = databaseSourceService.getSchemasFromConnection(ds);
            return Result.ok(schemas);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取数据库表列表
     * tableName：按表名关键字模糊过滤（不区分大小写）
     */
    @PostMapping("/getDataTables")
    public Result<?> getDataTables(@RequestParam(required = false) String connection,
                                   @RequestParam(required = false) String schema,
                                   @RequestParam(required = false) String tableName,
                                   @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                   @RequestParam(required = false, defaultValue = "0") int pageSize) {
        try {
            if (connection == null || connection.isEmpty()) {
                return Result.ok(buildTablePage(new ArrayList<>(), pageIndex, pageSize));
            }
            DatabaseSource ds = databaseSourceService.getByName(connection);
            if (ds == null) {
                return Result.ok(buildTablePage(new ArrayList<>(), pageIndex, pageSize));
            }
            List<String> tables = databaseSourceService.getTables(ds.getId(), schema);
            String keyword = tableName == null ? "" : tableName.trim();
            List<Map<String, Object>> records = new ArrayList<>();
            for (String table : tables) {
                if (!keyword.isEmpty()
                        && !table.toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }
                Map<String, Object> record = new HashMap<>();
                record.put("tableName", table);
                record.put("keyNums", 0);
                record.put("remarks", "");
                records.add(record);
            }
            return Result.ok(buildTablePage(records, pageIndex, pageSize));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 表列表分页：pageSize<=0 表示不分页（兼容旧调用） */
    private Map<String, Object> buildTablePage(List<Map<String, Object>> records, int pageIndex, int pageSize) {
        int total = records.size();
        List<Map<String, Object>> pageRecords = records;
        if (pageSize > 0) {
            int from = Math.max(0, (Math.max(pageIndex, 1) - 1) * pageSize);
            int to = Math.min(total, from + pageSize);
            pageRecords = from >= total ? new ArrayList<>() : new ArrayList<>(records.subList(from, to));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageRecords);
        result.put("total", total);
        return result;
    }

    /**
     * 获取表字段列表
     */
    @PostMapping("/getColumnsByTable")
    public Result<?> getColumnsByTable(@RequestParam(required = false) String connection,
                                       @RequestParam(required = false) String tableName,
                                       @RequestParam(required = false) String table,
                                       @RequestParam(required = false) String schema) {
        try {
            System.out.println("[getColumnsByTable] connection=" + connection + ", tableName=" + tableName + ", table=" + table + ", schema=" + schema);
            if (connection == null || connection.isEmpty()) {
                return Result.ok(new ArrayList<>());
            }
            DatabaseSource ds = databaseSourceService.getByName(connection);
            if (ds == null) {
                return Result.ok(new ArrayList<>());
            }
            String tbl = (tableName != null && !tableName.isEmpty()) ? tableName : table;
            System.out.println("[getColumnsByTable] actual table=" + tbl);
            if (tbl == null || tbl.isEmpty()) {
                return Result.ok(new ArrayList<>());
            }
            List<Map<String, Object>> columns = databaseSourceService.getColumns(ds.getId(), tbl, schema);
            return Result.ok(columns);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 浏览数据库结构
     */
    @PostMapping("/explorer")
    public Result<?> explorer(@RequestParam(required = false) String idDatabase) {
        try {
            Long id = Long.parseLong(idDatabase);
            List<String> tables = databaseSourceService.getTables(id);
            return Result.ok(tables);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 引接库获取表列表
     */
    @PostMapping("/yj/getDataTables")
    public Result<?> yjGetDataTables(@RequestParam(required = false) String connection,
                                     @RequestParam(required = false) String schema,
                                     @RequestParam(required = false) String tableName,
                                     @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                     @RequestParam(required = false, defaultValue = "0") int pageSize) {
        return getDataTables(connection, schema, tableName, pageIndex, pageSize);
    }

    /**
     * 引接库获取表数据
     */
    @PostMapping("/yj/getTableDatas")
    public Result<?> yjGetTableDatas(@RequestParam(required = false) String connection,
                                     @RequestParam(required = false) String tableName,
                                     @RequestParam(required = false) String schema,
                                     @RequestParam(required = false, defaultValue = "1") int pageIndex,
                                     @RequestParam(required = false, defaultValue = "100") int pageSize) {
        try {
            DatabaseSource ds = databaseSourceService.getByName(connection);
            if (ds == null) {
                return Result.fail("数据源不存在: " + connection);
            }
            if (tableName == null || tableName.isEmpty()) {
                Map<String, Object> empty = new HashMap<>();
                empty.put("column", new ArrayList<>());
                Map<String, Object> page = new HashMap<>();
                page.put("total", 0);
                page.put("records", new ArrayList<>());
                empty.put("page", page);
                return Result.ok(empty);
            }
            Map<String, Object> result = databaseSourceService.getTableData(ds.getId(), tableName, pageIndex, pageSize, schema);
            return Result.ok(result);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 引接库获取列值
     */
    @PostMapping("/yj/getValusByColumn")
    public Result<?> yjGetValusByColumn(@RequestParam(required = false) String connection,
                                        @RequestParam(required = false) String tableName,
                                        @RequestParam(required = false) String columnName) {
        try {
            DatabaseSource ds = databaseSourceService.getByName(connection);
            if (ds == null) {
                return Result.fail("数据源不存在: " + connection);
            }

            List<Object> values = databaseSourceService.getColumnValues(ds.getId(), tableName, columnName);
            return Result.ok(values);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    private DatabaseSource mapToDatabaseSource(Map<String, Object> params) {
        DatabaseSource ds = new DatabaseSource();
        ds.setDbType(getStringValue(params, "dbCode", "databaseType", "DM"));
        ds.setHost(getStringValue(params, "serverName", "host", "127.0.0.1"));
        ds.setPort(getIntValue(params, "port", 5236));
        ds.setUsername(getStringValue(params, "userName", "username", ""));
        ds.setPassword(getStringValue(params, "password", "password", ""));
        ds.setSid(getStringValue(params, "sid", "sid", ""));

        // databaseName 处理：过滤掉无效值（如纯数字的类型ID）
        String dbName = getStringValue(params, "databaseName", "dbName", "");
        if (dbName != null && !dbName.isEmpty()) {
            if (dbName.matches("\\d+")) {
                dbName = "";
            }
        }
        ds.setDatabaseName(dbName);

        return ds;
    }

    private String getStringValue(Map<String, Object> params, String key1, String key2, String defaultValue) {
        Object value = params.get(key1);
        if (value == null) value = params.get(key2);
        return value != null ? value.toString() : defaultValue;
    }

    private int getIntValue(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
