package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjProtocol;
import com.gx.etl.receive.service.YjProtocolService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/protocol")
public class YjProtocolController {

    private final YjProtocolService yjProtocolService;

    public YjProtocolController(YjProtocolService yjProtocolService) {
        this.yjProtocolService = yjProtocolService;
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        String configName = null;
        if (params.get("searchData") instanceof Map) {
            Map<?, ?> searchData = (Map<?, ?>) params.get("searchData");
            if (searchData.get("configName") != null) {
                configName = searchData.get("configName").toString();
            }
        }
        Page<YjProtocol> page = yjProtocolService.listPage(pageIndex, pageSize, configName);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, Object> params) {
        YjProtocol entity = new YjProtocol();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            try { entity.setId(Long.parseLong(params.get("id").toString())); } catch (Exception e) {}
        }
        if (params.get("name") != null) entity.setName(params.get("name").toString());
        if (params.get("tableName") != null) entity.setTableName(params.get("tableName").toString());
        if (params.get("fileType") != null) entity.setFileType(params.get("fileType").toString());
        if (params.get("fileTypeName") != null) entity.setFileTypeName(params.get("fileTypeName").toString());
        if (params.get("separator") != null) entity.setSeparator(params.get("separator").toString());
        if (params.get("separatorName") != null) entity.setSeparatorName(params.get("separatorName").toString());
        if (params.get("tableIdType") != null) entity.setTableIdType(params.get("tableIdType").toString());
        if (params.get("idColumnName") != null) entity.setIdColumnName(params.get("idColumnName").toString());
        if (params.get("jsonForm") != null) entity.setJsonForm(params.get("jsonForm").toString());
        if (params.get("tableId") != null) entity.setTableId(params.get("tableId").toString());
        if (params.get("remark") != null) entity.setRemark(params.get("remark").toString());

        boolean success;
        if (entity.getId() == null) {
            success = yjProtocolService.save(entity);
        } else {
            success = yjProtocolService.updateById(entity);
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
                    if (!yjProtocolService.removeById(Long.parseLong(idStr.trim()))) {
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

    @PostMapping("/file/merge")
    public Result<?> mergeTxt(@RequestBody java.util.List<Map<String, Object>> list) {
        try {
            StringBuilder merged = new StringBuilder();
            for (Map<String, Object> item : list) {
                String filePath = item.getOrDefault("filePath", "").toString();
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                    String separator = item.getOrDefault("separator", "\t").toString();
                    java.util.List<Object> colIndex = item.get("colIndex") instanceof java.util.List
                        ? (java.util.List<Object>) item.get("colIndex")
                        : java.util.Collections.emptyList();
                    for (String line : lines) {
                        String[] cols = line.split(java.util.regex.Pattern.quote(separator), -1);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < colIndex.size(); i++) {
                            int idx = Integer.parseInt(colIndex.get(i).toString()) - 1;
                            if (idx >= 0 && idx < cols.length) {
                                sb.append(cols[idx]);
                            }
                            if (i < colIndex.size() - 1) {
                                sb.append("\t");
                            }
                        }
                        merged.append(sb).append("\n");
                    }
                }
            }
            byte[] bytes = merged.toString().getBytes("UTF-8");
            return Result.ok(bytes);
        } catch (Exception e) {
            return Result.fail("文件合并失败: " + e.getMessage());
        }
    }
}
