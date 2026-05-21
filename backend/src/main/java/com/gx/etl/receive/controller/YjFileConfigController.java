package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjFileConfig;
import com.gx.etl.receive.service.YjFileConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/config/file")
public class YjFileConfigController {

    private final YjFileConfigService yjFileConfigService;

    public YjFileConfigController(YjFileConfigService yjFileConfigService) {
        this.yjFileConfigService = yjFileConfigService;
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        String name = null;
        if (params.get("searchData") instanceof Map) {
            Object configName = ((Map<?, ?>) params.get("searchData")).get("configName");
            if (configName != null) name = configName.toString();
        }
        Page<YjFileConfig> page = yjFileConfigService.listPage(pageIndex, pageSize, name);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        YjFileConfig entity = new YjFileConfig();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            entity.setId(Long.parseLong(params.get("id").toString()));
        }
        if (params.get("name") != null) entity.setName(params.get("name").toString());
        if (params.get("fileMulti") != null) entity.setFileMulti(params.get("fileMulti").toString());
        if (params.get("filePath") != null) entity.setFilePath(params.get("filePath").toString());
        if (params.get("folderPath") != null) entity.setFolderPath(params.get("folderPath").toString());
        if (params.get("analysisProtocolId") != null && !params.get("analysisProtocolId").toString().isEmpty()) {
            entity.setAnalysisProtocolId(Long.parseLong(params.get("analysisProtocolId").toString()));
        }
        if (params.get("analysisProtocolName") != null) entity.setAnalysisProtocolName(params.get("analysisProtocolName").toString());
        if (params.get("targetDataSourceId") != null && !params.get("targetDataSourceId").toString().isEmpty()) {
            entity.setTargetDataSourceId(Long.parseLong(params.get("targetDataSourceId").toString()));
        }
        if (params.get("targetDataSourceName") != null) entity.setTargetDataSourceName(params.get("targetDataSourceName").toString());
        if (params.get("targetSchema") != null) entity.setTargetSchema(params.get("targetSchema").toString());
        if (params.get("taskDataFlag") != null) {
            try { entity.setTaskDataFlag(Integer.parseInt(params.get("taskDataFlag").toString())); } catch (Exception e) {}
        }
        if (params.get("jumpRow") != null) entity.setJumpRow(params.get("jumpRow").toString());
        if (params.get("remark") != null) entity.setRemark(params.get("remark").toString());
        if (params.get("pathType") != null) entity.setPathType(params.get("pathType").toString());
        if (params.get("createName") != null) entity.setCreateBy(params.get("createName").toString());

        if (entity.getId() == null) {
            yjFileConfigService.save(entity);
        } else {
            yjFileConfigService.updateById(entity);
        }
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestBody Map<String, Object> params) {
        String ids = params.getOrDefault("ids", "").toString();
        if (!ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                try {
                    yjFileConfigService.removeById(Long.parseLong(idStr.trim()));
                } catch (NumberFormatException e) {
                    // skip invalid id
                }
            }
        }
        return Result.ok();
    }
}
