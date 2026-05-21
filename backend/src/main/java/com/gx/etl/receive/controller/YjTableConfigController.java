package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjTableConfig;
import com.gx.etl.receive.service.YjTableConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/config/table")
public class YjTableConfigController {

    private final YjTableConfigService yjTableConfigService;

    public YjTableConfigController(YjTableConfigService yjTableConfigService) {
        this.yjTableConfigService = yjTableConfigService;
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        Page<YjTableConfig> page = yjTableConfigService.listPage(pageIndex, pageSize, null);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        YjTableConfig entity = new YjTableConfig();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            entity.setId(Long.parseLong(params.get("id").toString()));
        }
        if (params.get("tableName") != null) entity.setTableName(params.get("tableName").toString());
        if (params.get("dataSourceId") != null && !params.get("dataSourceId").toString().isEmpty()) {
            try { entity.setDataSourceId(Long.parseLong(params.get("dataSourceId").toString())); } catch (Exception e) {}
        }
        if (params.get("databaseName") != null) entity.setDataSourceName(params.get("databaseName").toString());
        if (params.get("schema") != null) entity.setSchema(params.get("schema").toString());
        if (params.get("tableConfigId") != null && !params.get("tableConfigId").toString().isEmpty()) {
            try { entity.setTableConfigId(Long.parseLong(params.get("tableConfigId").toString())); } catch (Exception e) {}
        }
        if (params.get("tableConfigName") != null) entity.setTableConfigName(params.get("tableConfigName").toString());
        if (params.get("targetDataSourceId") != null && !params.get("targetDataSourceId").toString().isEmpty()) {
            try { entity.setTargetDataSourceId(Long.parseLong(params.get("targetDataSourceId").toString())); } catch (Exception e) {}
        }
        if (params.get("targetDataSourceName") != null) entity.setTargetDataSourceName(params.get("targetDataSourceName").toString());
        if (params.get("targetSchema") != null) entity.setTargetSchema(params.get("targetSchema").toString());
        if (params.get("remarks") != null) entity.setRemarks(params.get("remarks").toString());

        if (entity.getId() == null) {
            yjTableConfigService.save(entity);
        } else {
            yjTableConfigService.updateById(entity);
        }
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Object> params) {
        String ids = params.getOrDefault("ids", "").toString();
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                try {
                    yjTableConfigService.removeById(Long.parseLong(idStr.trim()));
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return Result.ok();
    }
}
