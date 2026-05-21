package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjFormModel;
import com.gx.etl.receive.service.YjFormModelService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/form")
public class YjFormModelController {

    private final YjFormModelService yjFormModelService;

    public YjFormModelController(YjFormModelService yjFormModelService) {
        this.yjFormModelService = yjFormModelService;
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        Page<YjFormModel> page = yjFormModelService.listPage(pageIndex, pageSize);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        YjFormModel entity = new YjFormModel();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            try { entity.setId(Long.parseLong(params.get("id").toString())); } catch (Exception e) {}
        }
        if (params.get("name") != null) entity.setName(params.get("name").toString());
        if (params.get("formName") != null) entity.setFormName(params.get("formName").toString());
        if (params.get("tableName") != null) entity.setTableName(params.get("tableName").toString());
        if (params.get("allConfig") != null) entity.setAllConfig(params.get("allConfig").toString());
        if (params.get("formModel") != null) entity.setFormModel(params.get("formModel").toString());
        if (params.get("remarks") != null) entity.setRemarks(params.get("remarks").toString());

        boolean success;
        if (entity.getId() == null) {
            success = yjFormModelService.save(entity);
        } else {
            success = yjFormModelService.updateById(entity);
        }
        Map<String, Object> r = new HashMap<>();
        r.put("result", success ? "保存成功" : "保存失败");
        r.put("success", success);
        return Result.ok(r);
    }

    @DeleteMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Object> params) {
        String ids = params.getOrDefault("ids", "").toString();
        boolean success = true;
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                try {
                    if (!yjFormModelService.removeById(Long.parseLong(idStr.trim()))) {
                        success = false;
                    }
                } catch (NumberFormatException e) {
                    success = false;
                }
            }
        }
        Map<String, Object> r = new HashMap<>();
        r.put("result", success ? "删除成功" : "删除失败");
        r.put("success", success);
        return Result.ok(r);
    }
}
