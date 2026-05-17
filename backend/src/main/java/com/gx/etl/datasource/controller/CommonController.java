package com.gx.etl.datasource.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.common.Result;
import com.gx.etl.datasource.entity.DatabaseSource;
import com.gx.etl.datasource.service.DatabaseSourceService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common API endpoints used by operator config forms.
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final DatabaseSourceService databaseSourceService;

    public CommonController(DatabaseSourceService databaseSourceService) {
        this.databaseSourceService = databaseSourceService;
    }

    /**
     * Get all databases (for operator dropdowns like "connection" field)
     * Frontend calls: /api/common/getDatabases.do
     */
    @PostMapping("/getDatabases")
    public Result<?> getDatabases() {
        List<DatabaseSource> list = databaseSourceService.list(
                new LambdaQueryWrapper<DatabaseSource>()
                        .eq(DatabaseSource::getStatus, 1)
                        .orderByAsc(DatabaseSource::getName));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DatabaseSource ds : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("value", ds.getId().toString());
            item.put("name", ds.getName());
            item.put("dbType", ds.getDbType());
            result.add(item);
        }
        return Result.ok(result);
    }

    @GetMapping("/getDatabasesByType")
    public Result<?> getDatabasesByType(@RequestParam(required = false) String sourceId,
                                        @RequestParam(required = false) String businessType) {
        LambdaQueryWrapper<DatabaseSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatabaseSource::getStatus, 1);

        // 按分类过滤（businessType 对应 categoriesId）
        if (businessType != null && !businessType.isEmpty()) {
            try {
                Long catId = Long.parseLong(businessType);
                wrapper.eq(DatabaseSource::getCategoriesId, catId);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        wrapper.orderByAsc(DatabaseSource::getName);
        List<DatabaseSource> list = databaseSourceService.list(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DatabaseSource ds : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", ds.getId());
            item.put("name", ds.getName());
            item.put("dbType", ds.getDbType());
            item.put("host", ds.getHost());
            item.put("port", ds.getPort());
            item.put("databaseName", ds.getDatabaseName());
            item.put("username", ds.getUsername());
            item.put("password", ds.getPassword());
            item.put("sid", ds.getSid());
            item.put("schemaFilter", ds.getSchemaFilter());
            item.put("isInclude", ds.getIsInclude());
            item.put("categoriesId", ds.getCategoriesId());
            item.put("status", ds.getStatus());
            item.put("remark", ds.getRemark());
            result.add(item);
        }
        return Result.ok(result);
    }

    /**
     * Category cache tree (for data source management)
     */
    @PostMapping("/CategoryCache/tree")
    public Result<?> categoryCacheTree() {
        // Delegate to category controller
        return Result.ok(new ArrayList<>());
    }

    /**
     * Category cache save
     */
    @PostMapping("/CategoryCache/save")
    public Result<?> categoryCacheSave(@RequestBody Map<String, Object> params) {
        return Result.ok(params);
    }

    /**
     * Category cache delete
     */
    @GetMapping("/CategoryCache/deleteBatchIds")
    public Result<?> categoryCacheDelete(@RequestParam(required = false) String ids) {
        return Result.ok();
    }

    /**
     * Category cache update
     */
    @PostMapping("/CategoryCache/update")
    public Result<?> categoryCacheUpdate(@RequestBody Map<String, Object> params) {
        return Result.ok(params);
    }

    /**
     * Get all databases by source ID (for data center)
     */
    @PostMapping("/getDatabasesBySource")
    public Result<?> getDatabasesBySource(@RequestBody(required = false) Map<String, Object> params) {
        return getDatabases();
    }

    /**
     * Get file server list
     */
    @GetMapping("/getFileServerList")
    public Result<?> getFileServerList(@RequestParam(required = false) String sourceId) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * Get artificial data source list
     */
    @GetMapping("/getArtificial")
    public Result<?> getArtificial(@RequestParam(required = false) String sourceId) {
        return Result.ok(new ArrayList<>());
    }

    /**
     * Delete artificial data source
     */
    @PostMapping("/deleteArtificial")
    public Result<?> deleteArtificial(@RequestBody Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/getBatchNumber")
    public Result<?> getBatchNumber() {
        return Result.ok("1");
    }

    @PostMapping("/receiveData")
    public Result<?> receiveData(@RequestBody(required = false) Map<String, Object> params) {
        return Result.ok();
    }

    @GetMapping("/getFormwordListList")
    public Result<?> getFormwordListList() {
        return Result.ok(new ArrayList<>());
    }
}
