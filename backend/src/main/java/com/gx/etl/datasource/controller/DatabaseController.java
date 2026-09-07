package com.gx.etl.datasource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.service.DatabaseSourceService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/center/database")
public class DatabaseController {

    private final DatabaseSourceService databaseSourceService;

    public DatabaseController(DatabaseSourceService databaseSourceService) {
        this.databaseSourceService = databaseSourceService;
    }

    @GetMapping("/list/page")
    public Result<?> listPage(@RequestParam(defaultValue = "1") int pageIndex,
                              @RequestParam(defaultValue = "10") int pageSize,
                              @RequestParam(required = false) String databaseName,
                              @RequestParam(required = false) String categoriesId) {
        Long catId = null;
        if (categoriesId != null && !categoriesId.isEmpty() && !"undefined".equals(categoriesId)) {
            try {
                catId = Long.parseLong(categoriesId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        Page<DatabaseSource> page = databaseSourceService.listPage(pageIndex, pageSize, databaseName, catId);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageIndex", pageIndex);
        result.put("pageSize", pageSize);
        return Result.ok(result);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        DatabaseSource ds = new DatabaseSource();
        // 直接映射，前端字段名与实体一致
        if (params.get("id") != null) ds.setId(Long.parseLong(params.get("id").toString()));
        if (params.get("name") != null) ds.setName(params.get("name").toString());
        // 优先使用 dbCode（实际类型编码如 DM/MYSQL），其次用 dbType
        String dbCode = params.get("dbCode") != null ? params.get("dbCode").toString() : null;
        String dbType = params.get("dbType") != null ? params.get("dbType").toString() : null;
        ds.setDbType(dbCode != null && !dbCode.isEmpty() ? dbCode : dbType);
        if (params.get("host") != null) ds.setHost(params.get("host").toString());
        if (params.get("port") != null) ds.setPort(Integer.parseInt(params.get("port").toString()));
        if (params.get("databaseName") != null) ds.setDatabaseName(params.get("databaseName").toString());
        if (params.get("username") != null) ds.setUsername(params.get("username").toString());
        if (params.get("password") != null) ds.setPassword(params.get("password").toString());
        if (params.get("sid") != null) ds.setSid(params.get("sid").toString());
        if (params.get("schemaFilter") != null) ds.setSchemaFilter(params.get("schemaFilter").toString());
        if (params.get("isInclude") != null) ds.setIsInclude(params.get("isInclude").toString());
        if (params.get("categoriesId") != null && !params.get("categoriesId").toString().isEmpty()) {
            try { ds.setCategoriesId(Long.parseLong(params.get("categoriesId").toString())); } catch (Exception e) {}
        }
        if (params.get("remark") != null) ds.setRemark(params.get("remark").toString());
        if (params.get("status") != null) ds.setStatus(Integer.parseInt(params.get("status").toString()));

        if (ds.getId() == null) {
            databaseSourceService.save(ds);
        } else {
            databaseSourceService.updateById(ds);
            databaseSourceService.removeDataSourceCache(ds.getId());
        }
        return Result.ok(ds);
    }

    @PostMapping("/del")
    public Result<?> delete(@RequestParam Long idDatabase) {
        // 禁止删除核心数据源（id=1 达梦, id=2 MySQL），避免引接功能失效
        if (idDatabase != null && (idDatabase == 1L || idDatabase == 2L)) {
            return Result.fail("该数据源为系统核心连接，不允许删除");
        }
        databaseSourceService.removeDataSourceCache(idDatabase);
        databaseSourceService.removeById(idDatabase);
        return Result.ok();
    }

    @PostMapping("/connect/dm/testConnect")
    public Result<?> testConnect(@RequestBody Map<String, Object> params) {
        try {
            DatabaseSource ds = mapToDatabaseSourceFromObject(params);
            boolean success = databaseSourceService.testConnection(ds);
            if (success) {
                return Result.ok(true);
            } else {
                return Result.fail("连接失败");
            }
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    private DatabaseSource mapToDatabaseSourceFromObject(Map<String, Object> params) {
        DatabaseSource ds = new DatabaseSource();
        // 优先用 dbCode（前端计算的数据库类型编码），其次用 dbType（数据库字段名）
        String dbCode = getStringValue(params, "dbCode", null);
        if (dbCode == null) dbCode = getStringValue(params, "dbType", "DM");
        ds.setDbType(dbCode);
        ds.setHost(getStringValue(params, "host", "127.0.0.1"));
        ds.setPort(getIntValue(params, "port", 5236));
        ds.setUsername(getStringValue(params, "username", ""));
        ds.setPassword(getStringValue(params, "password", ""));
        ds.setSid(getStringValue(params, "sid", ""));
        String dbName = getStringValue(params, "databaseName", "");
        if (dbName != null && !dbName.isEmpty() && dbName.matches("\\d+")) {
            dbName = "";
        }
        ds.setDatabaseName(dbName);
        return ds;
    }

    private String getStringValue(Map<String, Object> params, String key, String defaultValue) {
        Object value = params.get(key);
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

    @GetMapping("/connect/dm/getTables")
    public Result<?> getTables(@RequestParam Long idDatabase) {
        try {
            List<String> tables = databaseSourceService.getTables(idDatabase);
            return Result.ok(tables);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/connect/dm/getColumns")
    public Result<?> getColumns(@RequestParam Long idDatabase, @RequestParam String tableName) {
        try {
            return Result.ok(databaseSourceService.getColumns(idDatabase, tableName));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * Browse database structure (tables grouped by schema)
     */
    @PostMapping("/explorer")
    public Result<?> explorer(@RequestParam Long idDatabase) {
        try {
            List<String> tables = databaseSourceService.getTables(idDatabase);
            return Result.ok(tables);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取数据库类型列表（用于前端下拉选择）
     */
    @GetMapping("/dbTypes")
    public Result<?> getDbTypes() {
        List<Map<String, String>> dbTypes = new ArrayList<>();
        dbTypes.add(buildDbType("1", "DM", "达梦"));
        dbTypes.add(buildDbType("2", "MYSQL", "MySQL"));
        dbTypes.add(buildDbType("3", "ORACLE", "Oracle"));
        dbTypes.add(buildDbType("4", "POSTGRESQL", "PostgreSQL"));
        dbTypes.add(buildDbType("5", "KINGBASE", "人大金仓"));
        return Result.ok(dbTypes);
    }

    private Map<String, String> buildDbType(String id, String dbCode, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("dbCode", dbCode);
        map.put("categoriesName", name);
        return map;
    }

    /**
     * 获取数据库类型列表（兼容旧接口）
     */
    @GetMapping("/databasetype/list")
    public Result<?> getDatabaseTypeList() {
        return getDbTypes();
    }

    /**
     * 获取数据库连接类型列表
     */
    @GetMapping("/databasecontype/list")
    public Result<?> getDatabaseConTypeList() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(buildConType("1", "源数据库"));
        list.add(buildConType("2", "引接库"));
        list.add(buildConType("3", "处理库"));
        return Result.ok(list);
    }

    private Map<String, String> buildConType(String id, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        return map;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int pageIndex,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String databaseName,
                          @RequestParam(required = false) String categoriesId) {
        return listPage(pageIndex, pageSize, databaseName, categoriesId);
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, Object> params) {
        return save(params);
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody Map<String, Object> params) {
        return save(params);
    }

    @GetMapping("/back/show/assign")
    public Result<?> showAssign() {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/connect/dm/getViews")
    public Result<?> getViews(@RequestParam Long idDatabase) {
        return Result.ok(new ArrayList<>());
    }

    @GetMapping("/connect/dm/getTableData")
    public Result<?> getTableData(@RequestParam Long idDatabase, @RequestParam String tableName) {
        return Result.ok(new HashMap<>());
    }

    @PostMapping("/assign/tree")
    public Result<?> assignTree() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/assign/save")
    public Result<?> assignSave(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @PostMapping("/task/group/list")
    public Result<?> taskGroupList() {
        return Result.ok(new ArrayList<>());
    }

    @PostMapping("/task/group/del")
    public Result<?> taskGroupDel(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }
}
