package com.gx.etl.receive.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gx.etl.common.Result;
import com.gx.etl.receive.entity.YjExecLog;
import com.gx.etl.receive.entity.YjFileConfig;
import com.gx.etl.receive.entity.YjTableConfig;
import com.gx.etl.receive.service.YjExecService;
import com.gx.etl.receive.service.YjFileConfigService;
import com.gx.etl.receive.service.YjTableConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/yj/exec")
public class YjExecController {

    private final YjExecService yjExecService;
    private final YjTableConfigService yjTableConfigService;
    private final YjFileConfigService yjFileConfigService;

    public YjExecController(YjExecService yjExecService,
                            YjTableConfigService yjTableConfigService,
                            YjFileConfigService yjFileConfigService) {
        this.yjExecService = yjExecService;
        this.yjTableConfigService = yjTableConfigService;
        this.yjFileConfigService = yjFileConfigService;
    }

    /**
     * 获取数据表引接列表（配置列表 + 批次号）
     */
    @PostMapping("/table/page")
    public Result<?> tablePage(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        Page<YjTableConfig> page = yjTableConfigService.listPage(pageIndex, pageSize, null);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    /**
     * 获取文件引接列表（配置列表 + 批次号）
     */
    @PostMapping("/file/page")
    public Result<?> filePage(@RequestBody Map<String, Object> params) {
        int pageIndex = Integer.parseInt(params.getOrDefault("pageIndex", 1).toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", 20).toString());
        Page<YjFileConfig> page = yjFileConfigService.listPage(pageIndex, pageSize, null);
        Map<String, Object> r = new HashMap<>();
        r.put("records", page.getRecords());
        r.put("total", page.getTotal());
        return Result.ok(r);
    }

    /**
     * 执行数据表引接
     */
    @PostMapping("/table/exec")
    public Result<?> execTable(@RequestBody java.util.List<Map<String, Object>> list) {
        try {
            StringBuilder resultMsg = new StringBuilder();
            boolean allSuccess = true;
            for (Map<String, Object> params : list) {
                Object idVal = params.get("id") != null ? params.get("id") : params.get("configId");
                if (idVal == null) continue;
                Long configId = Long.parseLong(idVal.toString());
                String batch = params.get("batchNum") != null ? params.get("batchNum").toString() : yjExecService.generateBatchNumber();
                String createBy = params.get("createBy") != null ? params.get("createBy").toString() : "admin";
                YjExecLog log = yjExecService.execTable(configId, batch, createBy);
                if (log.getExecStatus() == 1) {
                    resultMsg.append("引接成功，").append(log.getErrorMsg()).append("\n");
                } else {
                    allSuccess = false;
                    resultMsg.append("引接失败: ").append(log.getErrorMsg()).append("\n");
                }
            }
            Map<String, Object> r = new HashMap<>();
            r.put("result", resultMsg.toString().trim());
            r.put("success", allSuccess);
            return Result.ok(r);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 执行文件引接
     */
    @PostMapping("/file/exec")
    public Result<?> execFile(@RequestBody java.util.List<Map<String, Object>> list) {
        try {
            StringBuilder resultMsg = new StringBuilder();
            boolean allSuccess = true;
            for (Map<String, Object> params : list) {
                Object idVal = params.get("id") != null ? params.get("id") : params.get("configId");
                if (idVal == null) continue;
                Long configId = Long.parseLong(idVal.toString());
                String batch = params.get("batchNum") != null ? params.get("batchNum").toString() : yjExecService.generateBatchNumber();
                String createBy = params.get("createBy") != null ? params.get("createBy").toString() : "admin";
                YjExecLog log = yjExecService.execFile(configId, batch, createBy);
                if (log.getExecStatus() == 1) {
                    resultMsg.append("引接成功，").append(log.getRowCount()).append("条数据\n");
                } else {
                    allSuccess = false;
                    resultMsg.append("引接失败: ").append(log.getErrorMsg()).append("\n");
                }
            }
            Map<String, Object> r = new HashMap<>();
            r.put("result", resultMsg.toString().trim());
            r.put("success", allSuccess);
            return Result.ok(r);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
