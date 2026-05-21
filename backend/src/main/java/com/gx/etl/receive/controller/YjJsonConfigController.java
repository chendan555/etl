package com.gx.etl.receive.controller;

import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjJsonConfig;
import com.gx.etl.receive.service.YjJsonConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yj/jsonData")
public class YjJsonConfigController {

    private final YjJsonConfigService yjJsonConfigService;

    public YjJsonConfigController(YjJsonConfigService yjJsonConfigService) {
        this.yjJsonConfigService = yjJsonConfigService;
    }

    @PostMapping("/saveJsonConfig")
    public Result<?> saveJsonConfig(@RequestBody java.util.Map<String, Object> params) {
        YjJsonConfig entity = new YjJsonConfig();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            entity.setId(Long.parseLong(params.get("id").toString()));
        }
        if (params.get("historyId") != null) {
            try { entity.setHistoryId(Long.parseLong(params.get("historyId").toString())); } catch (Exception e) {}
        }
        if (params.get("dataSourceId") != null) {
            try { entity.setDataSourceId(Long.parseLong(params.get("dataSourceId").toString())); } catch (Exception e) {}
        }
        if (params.get("schema") != null) entity.setSchema(params.get("schema").toString());
        if (params.get("tableName") != null) entity.setTableName(params.get("tableName").toString());
        if (params.get("columnName") != null) entity.setColumnName(params.get("columnName").toString());
        if (params.get("instanceId") != null) entity.setInstanceId(params.get("instanceId").toString());
        if (params.get("modelCode") != null) entity.setModelCode(params.get("modelCode").toString());

        if (entity.getId() == null) {
            yjJsonConfigService.save(entity);
        } else {
            yjJsonConfigService.updateById(entity);
        }
        return Result.ok();
    }

    @GetMapping("/queryJsonConfigById")
    public Result<?> queryJsonConfigById(@RequestParam(required = false) Long historyId) {
        if (historyId == null) {
            return Result.ok(null);
        }
        YjJsonConfig config = yjJsonConfigService.getByHistoryId(historyId);
        return Result.ok(config);
    }
}
