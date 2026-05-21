package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjTableRelation;
import com.gx.etl.receive.service.YjTableRelationService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/yj/tableConfig")
public class YjTableRelationController {

    private final YjTableRelationService yjTableRelationService;

    public YjTableRelationController(YjTableRelationService yjTableRelationService) {
        this.yjTableRelationService = yjTableRelationService;
    }

    @PostMapping("/queryListTableConfig")
    public Result<?> queryListTableConfig(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        String configName = null;
        if (params.get("searchData") instanceof Map) {
            Map<?, ?> searchData = (Map<?, ?>) params.get("searchData");
            if (searchData.get("configName") != null) {
                configName = searchData.get("configName").toString();
            }
        }
        Page<YjTableRelation> page = yjTableRelationService.listPage(pageIndex, pageSize, configName);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/saveTableConfigAndDetail")
    public Result<?> saveTableConfigAndDetail(@RequestBody Map<String, Object> params) {
        YjTableRelation entity = new YjTableRelation();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            try { entity.setId(Long.parseLong(params.get("id").toString())); } catch (Exception e) {}
        }
        if (params.get("configName") != null) entity.setConfigName(params.get("configName").toString());
        if (params.get("taskId") != null) entity.setTaskId(params.get("taskId").toString());
        if (params.get("taskName") != null) entity.setTaskName(params.get("taskName").toString());
        if (params.get("nodeId") != null) entity.setNodeId(params.get("nodeId").toString());
        if (params.get("nodeName") != null) entity.setNodeName(params.get("nodeName").toString());
        if (params.get("dataSourceId") != null && !params.get("dataSourceId").toString().isEmpty()) {
            try { entity.setDataSourceId(Long.parseLong(params.get("dataSourceId").toString())); } catch (Exception e) {}
        }
        if (params.get("databaseName") != null) entity.setDatabaseName(params.get("databaseName").toString());
        if (params.get("schemaId") != null) entity.setSchemaId(params.get("schemaId").toString());
        if (params.get("schema") != null) entity.setSchema(params.get("schema").toString());
        if (params.get("remark") != null) entity.setRemark(params.get("remark").toString());
        if (params.get("mainTableName") != null) entity.setMainTableName(params.get("mainTableName").toString());
        if (params.get("jsonForm") != null) entity.setJsonForm(params.get("jsonForm").toString());

        boolean success;
        if (entity.getId() == null) {
            success = yjTableRelationService.save(entity);
        } else {
            success = yjTableRelationService.updateById(entity);
        }
        Map<String, Object> r = new HashMap<>();
        r.put("result", success ? "保存成功" : "保存失败");
        r.put("success", success);
        return Result.ok(r);
    }

    @DeleteMapping("/deleteTableConfigAndDetail")
    public Result<?> deleteTableConfigAndDetail(@RequestParam String id) {
        boolean success = yjTableRelationService.removeById(Long.parseLong(id));
        Map<String, Object> r = new HashMap<>();
        r.put("result", success ? "删除成功" : "删除失败");
        r.put("success", success);
        return Result.ok(r);
    }

    @PostMapping("/getTableConfigList")
    public Result<?> getTableConfigList() {
        List<YjTableRelation> list = yjTableRelationService.listAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (YjTableRelation item : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("value", item.getId());
            map.put("lable", item.getConfigName());
            result.add(map);
        }
        return Result.ok(result);
    }

    @GetMapping("/queryTableConfigById")
    public Result<?> queryTableConfigById(@RequestParam String id) {
        YjTableRelation entity = yjTableRelationService.getById(Long.parseLong(id));
        if (entity == null) {
            return Result.fail("配置不存在");
        }
        // 把 mainTableName 同时作为 mainTable 返回，供前端回显
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());
        result.put("configName", entity.getConfigName());
        result.put("taskId", entity.getTaskId());
        result.put("taskName", entity.getTaskName());
        result.put("nodeId", entity.getNodeId());
        result.put("nodeName", entity.getNodeName());
        result.put("dataSourceId", entity.getDataSourceId());
        result.put("databaseName", entity.getDatabaseName());
        result.put("schemaId", entity.getSchemaId());
        result.put("schema", entity.getSchema());
        result.put("mainTable", entity.getMainTableName());
        result.put("mainTableName", entity.getMainTableName());
        result.put("jsonForm", entity.getJsonForm());
        result.put("remark", entity.getRemark());
        result.put("createTime", entity.getCreateTime());
        result.put("updateTime", entity.getUpdateTime());
        return Result.ok(result);
    }
}
