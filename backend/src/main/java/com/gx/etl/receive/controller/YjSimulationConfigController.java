package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjSimulationConfig;
import com.gx.etl.receive.service.YjSimulationConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/config/simulation")
public class YjSimulationConfigController {

    private final YjSimulationConfigService yjSimulationConfigService;

    public YjSimulationConfigController(YjSimulationConfigService yjSimulationConfigService) {
        this.yjSimulationConfigService = yjSimulationConfigService;
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        Page<YjSimulationConfig> page = yjSimulationConfigService.listPage(pageIndex, pageSize);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        YjSimulationConfig entity = new YjSimulationConfig();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            try { entity.setId(Long.parseLong(params.get("id").toString())); } catch (Exception e) {}
        }
        if (params.get("name") != null) entity.setName(params.get("name").toString());
        if (params.get("sourceUrl") != null) entity.setSourceUrl(params.get("sourceUrl").toString());
        if (params.get("sourceId") != null) entity.setSourceId(params.get("sourceId").toString());

        boolean success;
        if (entity.getId() == null) {
            success = yjSimulationConfigService.save(entity);
        } else {
            success = yjSimulationConfigService.updateById(entity);
        }
        Map<String, Object> r = new HashMap<>();
        r.put("result", success ? "保存成功" : "保存失败");
        r.put("success", success);
        return Result.ok(r);
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Object> params) {
        String ids = params.getOrDefault("ids", "").toString();
        boolean success = true;
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                try {
                    if (!yjSimulationConfigService.removeById(Long.parseLong(idStr.trim()))) {
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
